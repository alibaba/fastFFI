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
    final Type cxxType;
    final TypeName javaType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FFIType ffiType = (FFIType) o;
        return Objects.equals(cxxType, ffiType.cxxType) && Objects.equals(javaType, ffiType.javaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cxxType, javaType);
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
        return String.format("<%s, %s%s>", cxxType.getCanonicalTypeInternal().getAsString(), javaAnnotation(), javaType);
    }

    public FFIType(Type cxxType, TypeName javaType) {
        this.cxxType = cxxType;
        this.javaType = javaType;
        sanityCheck();
    }

    public Type getPointeeType() {
        if (isPointer()) {
            PointerType pointerType = PointerType.dyn_cast(cxxType);
            return pointerType.getPointeeType().getTypePtr();
        }
        if (isReference()) {
            if (cxxType.getTypeClass() == TypeClass.LValueReference) {
                return LValueReferenceType.dyn_cast(cxxType).getPointeeType().getTypePtr();
            }
            if (cxxType.getTypeClass() == TypeClass.RValueReference) {
                return RValueReferenceType.dyn_cast(cxxType).getPointeeType().getTypePtr();
            }
        }
        throw new IllegalStateException("Not a pointer or reference: " + cxxType);
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
            return isPrimitive(getPointeeType());
        }
        return false;
    }

    public boolean isPointeePointerOrReference() {
        if (isPointer() || isReference()) {
            Type pointeeType = getPointeeType();
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
