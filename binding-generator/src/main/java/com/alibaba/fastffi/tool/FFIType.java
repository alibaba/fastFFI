/*
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.fastffi.tool;

import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.clang.BuiltinType;
import com.alibaba.fastffi.clang.ElaboratedType;
import com.alibaba.fastffi.clang.LValueReferenceType;
import com.alibaba.fastffi.clang.PointerType;
import com.alibaba.fastffi.clang.QualType;
import com.alibaba.fastffi.clang.RValueReferenceType;
import com.alibaba.fastffi.clang.ReferenceType;
import com.alibaba.fastffi.clang.Type;
import com.alibaba.fastffi.clang.TypeClass;
import com.alibaba.fastffi.clang.TypedefNameDecl;
import com.alibaba.fastffi.clang.TypedefType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import java.util.Objects;

/**
 * An FFI Type is one of the following type:
 * <ul>
 *     <li>void</li>
 *     <li>primitive, e.g., int</li>
 *     <li>pointer</li>
 *     <li>reference</li>
 *     <li>value</li>
 * </ul>
 * Pointer, reference and value may also be
 * Every cxxType has a javaType. If the javaType is not primitive, it must have a corresponding class builder.
 */
public class FFIType {
    final QualType cxxQualType;
    final Type cxxType;
    final TypeName javaType;

    // cache the string representation to avoid repeated computation.
    final String cxxTypeString;

    private boolean requirePointer;
    private boolean verified;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FFIType ffiType = (FFIType) o;
        return Objects.equals(cxxTypeString, ffiType.cxxTypeString) && Objects.equals(javaType, ffiType.javaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cxxTypeString, javaType);
    }

    String javaAnnotation() {
        if (isReference()) {
            return "CXXReference ";
        }
        if (isValue()) {
            return "CXXValue ";
        }
        return "";
    }

    public String toString() {
        return String.format("<%s, %s%s>", cxxTypeString, javaAnnotation(), javaType);
    }

    public FFIType(QualType cxxType, TypeName javaType) {
        this.cxxQualType = cxxType;
        this.cxxType = cxxType.getTypePtr();
        this.cxxTypeString = cxxQualType.getAsString().toJavaString();
        this.javaType = javaType;
        sanityCheck();
    }

    public QualType getPointeeType() {
        if (cxxType.getTypeClass() == TypeClass.Typedef) {
            if (javaType instanceof TypeVariableName) {
                return getPointeeType(TypedefType.dyn_cast(cxxType).getCanonicalTypeInternal());
            }
            return null;
        }
        return getPointeeType(cxxQualType);
    }

    QualType getPointeeType(QualType qualType) {
        Type type = qualType.getTypePtr();
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case Pointer: {
                PointerType pointerType = PointerType.dyn_cast(type);
                return pointerType.getPointeeType();
            }
            case LValueReference: {
                return LValueReferenceType.dyn_cast(type).getPointeeType();
            }
            case RValueReference: {
                return RValueReferenceType.dyn_cast(type).getPointeeType();
            }
            case Elaborated: {
                return getPointeeType(ElaboratedType.dyn_cast(type).desugar());
            }
        }
        return null;
    }

    boolean isVoidPointer() {
        if (!javaType.equals(TypeName.LONG)) {
            return false;
        }
        return isPointer(cxxType) && isVoid(getPointeeType().getTypePtr());
    }

    void setRequirePointer() {
        this.requirePointer = true;
    }

    boolean requirePointer() {
        return requirePointer;
    }

    boolean isVoid(Type type) {
        if (type.getTypeClass() != TypeClass.Builtin) {
            return false;
        }
        BuiltinType builtinType = BuiltinType.dyn_cast(type);
        return builtinType.getKind() == BuiltinType.Kind.Void;
    }

    boolean hasTypeVariable() {
        return hasTypeVariable(this.javaType);
    }

    boolean hasTypeVariable(TypeName typeName) {
        if (typeName instanceof TypeVariableName) {
            return true;
        }
        if (typeName instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
            for (TypeName argTypeName : parameterizedTypeName.typeArguments) {
                if (hasTypeVariable(argTypeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean needFFITypeAlias() {
        if (isEnum() || isVoidPointer()) {
            return true;
        }
        if (javaType instanceof ParameterizedTypeName) {
            return !hasTypeVariable(javaType);
        }
        return false;
    }

    private void sanityCheck() {
        if (isPointer() && isReference()) {
            throw new IllegalStateException("cannot be both pointer and reference.");
        }
        if (isPointer() || isReference()) {
            if (!(javaType instanceof ClassName
                || javaType instanceof TypeVariableName
                || javaType instanceof ParameterizedTypeName)) {
                throw new IllegalStateException("Unsupported pointee type: " + javaType);
            }
            return;
        }
        if (isValue()) {

        }
    }

    boolean isPrimitive(Type type) {
        if (type instanceof BuiltinType) {
            BuiltinType builtinType = (BuiltinType) type;
            return FFIBindingGenerator.getJavaTypeName(builtinType) != null;
        }
        // no alias of primitive type
        if (type instanceof TypedefType) {
            return isPrimitive(((TypedefType) type).getDecl().getUnderlyingType().getTypePtr());
        }
        if (type instanceof ElaboratedType) {
            return isPrimitive(((ElaboratedType) type).desugar().getTypePtr());
        }
        return false;
    }

    public boolean isPointeePrimitive() {
        if (isPointer() || isReference()) {
            return isPrimitive(getPointeeType().getTypePtr());
        }
        return false;
    }

    public boolean isPointeePointerOrReference() {
        if (isPointer() || isReference()) {
            Type pointeeType = getPointeeType().getTypePtr();
            return pointeeType.getTypeClass() == TypeClass.Pointer
                    || pointeeType.getTypeClass() == TypeClass.LValueReference
                    || pointeeType.getTypeClass() == TypeClass.RValueReference;
        }
        return false;
    }

    /**
     * May be a value, pointer or reference to a template variable
     * @return
     */
    public boolean isTemplateVariableDependent() {
        return javaType instanceof TypeVariableName;
    }

    public boolean isEnum() {
        return isEnum(cxxType);
    }

    boolean isEnum(Type type) {
        if (type.getTypeClass() == TypeClass.Enum) {
            return true;
        }
        if (type instanceof ElaboratedType) {
            return isEnum(((ElaboratedType) type).desugar().getTypePtr());
        }
        return false;
    }

    boolean isPointer(Type type) {
        if (type.getTypeClass() == TypeClass.Pointer) {
            return true;
        }
        if (type instanceof ElaboratedType) {
            return isPointer(((ElaboratedType) type).desugar().getTypePtr());
        }
        if (type instanceof TypedefType) {
            if (javaType instanceof TypeVariableName) {
                // typedef may be a pointer of type variable
                return isPointer(type.getCanonicalTypeInternal().getTypePtr());
            }
            return false;
        }
        return false;
    }


    public boolean isPointer() {
        if (isVoid() || isPrimitive()) {
            return false;
        }
        return isPointer(cxxType);
    }

    public boolean isValue() {
        if (isVoid() || isPrimitive()) {
            return false;
        }
        return !(isReference() || isPointer());
    }


    boolean isReference(Type type) {
        if (type.getTypeClass() == TypeClass.LValueReference
                || type.getTypeClass() == TypeClass.RValueReference) {
            return true;
        }
        if (type instanceof ElaboratedType) {
            return isReference(((ElaboratedType) type).desugar().getTypePtr());
        }
        if (type instanceof TypedefType) {
            if (javaType instanceof TypeVariableName) {
                // typedef may be a pointer of type variable
                return isReference(type.getCanonicalTypeInternal().getTypePtr());
            }
            return false;
        }
        return false;
    }

    public boolean isParameterizedType() {
        return javaType instanceof ParameterizedTypeName;
    }

    public boolean isReference() {
        if (isVoid() || isPrimitive()) {
            return false;
        }
        return isReference(cxxType);
    }

    public boolean isVoid() {
        return javaType.equals(TypeName.VOID);
    }

    public boolean isPrimitive() {
        return javaType.isPrimitive();
    }
}
