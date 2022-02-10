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
package com.alibaba.fastffi.annotation;

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXSuperTemplate;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXTemplates;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIApplication;
import com.alibaba.fastffi.FFIBuiltinType;
import com.alibaba.fastffi.FFIByteString;
import com.alibaba.fastffi.FFIFunGen;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFIMirror;
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFINameSpace;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFISkip;
import com.alibaba.fastffi.FFISynthetic;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeRefiner;
import com.alibaba.fastffi.FFIVector;
import com.alibaba.fastffi.impl.CXXStdString;
import com.alibaba.fastffi.impl.CXXStdVector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.alibaba.fastffi.FFIUtils.addToMapList;
import static com.alibaba.fastffi.annotation.AnnotationProcessorUtils.*;

/**
 * Put common type related method in this class
 */
public class TypeEnv {

    protected TypeDefRegistry registry;
    protected ProcessingEnvironment processingEnv;

    public TypeEnv(TypeDefRegistry registry, ProcessingEnvironment processingEnv) {
        this.registry = registry;
        this.processingEnv = processingEnv;
    }

    protected boolean isJavaPrimitive(TypeMirror typeMirror) {
        int kindOrdinal = typeMirror.getKind().ordinal();
        return kindOrdinal >= TypeKind.BOOLEAN.ordinal() && kindOrdinal <= TypeKind.VOID.ordinal();
    }

    CXXTemplate getCXXTemplate(TypeMapping[] typeMappings, CXXHead[] heads) {
        String[] cxx = new String[typeMappings.length];
        String[] java = new String[typeMappings.length];
        for (int i = 0; i < typeMappings.length; i++) {
            cxx[i] = typeMappings[i].cxx;
            java[i] = typeToTypeName(typeMappings[i].java);
        }
        return getCXXTemplate(cxx, java, heads);
    }

    CXXTemplate getCXXTemplate(String[] cxx, String[] java, CXXHead[] heads) {
        return new CXXTemplate() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return CXXTemplate.class;
            }

            @Override
            public String[] cxx() {
                return cxx;
            }

            @Override
            public String cxxFull() {
                return "";
            }

            @Override
            public String[] java() {
                return java;
            }

            @Override
            public CXXHead[] include() {
                if (heads != null) {
                    return heads;
                }
                return new CXXHead[0];
            }

            @Override
            public int hashCode() {
                int hash = Arrays.hashCode(cxx());
                hash = 31 & hash + Arrays.hashCode(java());
                return hash;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null) {
                    return false;
                }
                if (!(obj instanceof Annotation)) {
                    return false;
                }
                if (this.annotationType() != ((Annotation) obj).annotationType()) {
                    return false;
                }
                CXXTemplate casted = (CXXTemplate) obj;

                return Arrays.equals(cxx(), casted.cxx())
                        && Objects.equals(cxxFull(), casted.cxxFull())
                        && Arrays.equals(java(), casted.java());
            }
        };
    }

    CXXTemplate getCXXTemplateForBuiltinType(DeclaredType typeMirror) {
        List<? extends TypeMirror> typeArguments = typeMirror.getTypeArguments();
        if (typeArguments.isEmpty()) {
            throw new IllegalStateException("Oops...");
        }
        final String[] cxx = new String[typeArguments.size()];
        final String[] java = new String[cxx.length];
        for (int i = 0; i < cxx.length; i++) {
            cxx[i] = getFFITypeNameForBuiltinType((DeclaredType) typeArguments.get(i), false);
            java[i] = getFFITypeNameForBuiltinType((DeclaredType) typeArguments.get(i), true);
        }
        return getCXXTemplate(cxx, java, new CXXHead[0]);
    }

    protected void error(String format, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(format, args));
    }

    protected void note(String format, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(format, args));
    }

    protected boolean isSameTypeMapping(TypeMapping typeMapping1, TypeMapping typeMapping2) {
        if (!typeMapping1.cxx.equals(typeMapping2.cxx)) {
            return false;
        }
        return typeUtils().isSameType(typeMapping1.java, typeMapping2.java);
    }

    protected Elements elementUtils() {
        return processingEnv.getElementUtils();
    }

    protected Types typeUtils() {
        return processingEnv.getTypeUtils();
    }

    protected boolean isFFIMirror(TypeElement typeElement) {
        return isFFIPointer(typeElement.asType()) && typeElement.getAnnotation(FFIMirror.class) != null;
    }

    /**
     * We use the literal name of a TypeVariable as its type name without considering its bounds.
     * @param typeMirror
     * @return
     */
    protected String getTypeName(TypeMirror typeMirror) {
        if (isDeclaredType(typeMirror)) {
            return ((TypeElement) typeUtils().asElement(typeMirror)).getQualifiedName().toString();
        }
        if (isTypeVariable(typeMirror)) {
            return typeUtils().asElement(typeMirror).getSimpleName().toString();
        }
        if (isPrimitiveType(typeMirror)) {
            return typeMirror.toString();
        }
        if (isArrayType(typeMirror)) {
            ArrayType arrayType = (ArrayType) typeMirror;
            return getTypeName(arrayType.getComponentType()) + "[]";
        }
        if (typeMirror.getKind() == TypeKind.VOID) {
            return "void";
        }
        throw new IllegalStateException("TODO: getTypeName of type " + typeMirror + " is not supported yet");
    }

    static <T> T[] merge(T[] a1, T[] a2) {
        if (a2.length == 0) {
            return a1;
        }
        if (a1.length == 0) {
            return a2;
        }
        T[] newArray = Arrays.copyOf(a1, a2.length + a1.length);
        System.arraycopy(a2, 0, newArray, a1.length, a2.length);
        return newArray;
    }

    public CXXTemplate[] getAdditionalFunctionTemplates(ExecutableElement executableElement) {
        TypeDef ownerDef = getOwnerTemplateTypeDef(executableElement);
        FFIFunGen[] ffiFunGens = ownerDef.getFFIFunctionTemplates();
        if (ffiFunGens.length > 0) {
            for (FFIFunGen funGen : ffiFunGens) {
                if (match(funGen, executableElement)) {
                    return funGen.templates();
                }
            }
        }
        return new CXXTemplate[0];
    }

    private TypeDef getOwnerTemplateTypeDef(ExecutableElement executableElement) {
        TypeElement ownerElement = (TypeElement) executableElement.getEnclosingElement();
        String typeName = ownerElement.getQualifiedName().toString();
        TypeDef typeDef = registry.getFFITemplateTypeDef(typeName);
        if (typeDef == null) {
            throw new IllegalStateException("" + AnnotationProcessorUtils.format(executableElement) + " must have a FFITemplate");
        }
        return typeDef;
    }

    private boolean match(FFIFunGen funGen, ExecutableElement executableElement) {
        if (!funGen.name().equals(executableElement.getSimpleName().toString())) {
            return false;
        }
        List<? extends VariableElement> parameters = executableElement.getParameters();
        String[] parameterTypes = funGen.parameterTypes();
        if (parameterTypes.length != parameters.size()) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!getTypeName(erasure(parameters.get(i).asType())).equals(parameterTypes[i])) {
                return false;
            }
        }
        return getTypeName(erasure(executableElement.getReturnType())).equals(funGen.returnType());
    }

    protected boolean isJavaLangVoid(TypeMirror type) {
        if (isDeclaredType(type)) {
            return isJavaLangVoid((DeclaredType) type);
        }
        return false;
    }

    protected boolean isJavaLangVoid(DeclaredType type) {
        return isJavaClass(type, "java.lang.Void");
    }

    protected boolean isCXXEnum(TypeMirror type) {
        if (isDeclaredType(type)) {
            return isCXXEnum((DeclaredType) type);
        }
        return false;
    }

    protected boolean isCXXEnum(DeclaredType type) {
        return isSubType(type, CXXEnum.class);
    }

    protected boolean isFFIPointerElement(TypeElement element) {
        return isFFIPointer(element.asType());
    }

    protected boolean isFFILibraryElement(TypeElement element) {
        return element.getAnnotation(FFILibrary.class) != null;
    }

    protected boolean isFFIPointer(TypeMirror type) {
        if (isDeclaredType(type)) {
            return isFFIPointer((DeclaredType) type);
        }
        return false;
    }

    protected boolean isFFIPointer(DeclaredType type) {
        return isSubType(type, FFIPointer.class);
    }

    protected String getTypeRefiner(TypeMirror type) {
        Element typeElement = typeUtils().asElement(type);
        FFITypeRefiner refiner = typeElement.getAnnotation(FFITypeRefiner.class);
        return refiner == null ? "" : refiner.value();
    }

    protected boolean isFFIJava(DeclaredType type) {
        return isSubType(type, com.alibaba.fastffi.FFIJava.class);
    }

    protected boolean isFFIBuiltinType(TypeMirror typeMirror) {
        if (isDeclaredType(typeMirror)) {
            return isSubType(typeMirror, FFIBuiltinType.class);
        }
        return false;
    }

    protected boolean isFFIByteString(TypeMirror typeMirror) {
        if (isDeclaredType(typeMirror)) {
            return isSubType(typeMirror, FFIByteString.class);
        }
        return false;
    }

    protected boolean isFFIVector(TypeMirror typeMirror) {
        if (isDeclaredType(typeMirror)) {
            return isSubType(typeMirror, FFIVector.class);
        }
        return false;
    }

    protected boolean hasTypeArguments(TypeMirror typeMirror) {
        if (isDeclaredType(typeMirror)) {
            return !((DeclaredType) typeMirror).getTypeArguments().isEmpty();
        }
        return false;
    }

    protected void sanityCheckFFIMirror(TypeMirror typeMirror) {
        if (hasTypeArguments(typeMirror)) {
            throw new IllegalStateException("TODO: no type arguments supported for FFIMirror");
        }
    }


    protected VRP getParameterVRP(ExecutableElement executableElement, int index) {
        return getVariableVRP(executableElement.getParameters().get(index));
    }

    protected VRP getVariableVRP(VariableElement variableElement) {
        List<? extends AnnotationMirror> mirrors = elementUtils().getAllAnnotationMirrors(variableElement);
        return getVRP(variableElement, mirrors);
    }

    protected VRP getReturnVRP(ExecutableElement executableElement) {
        List<? extends AnnotationMirror> mirrors = elementUtils().getAllAnnotationMirrors(executableElement);
        return getVRP(executableElement, mirrors);
    }

    protected VRP getVRP(Element element, List<? extends AnnotationMirror> mirrors) {
        boolean hasCXXValue = false;
        boolean hasCXXRef = false;
        for (AnnotationMirror mirror : mirrors) {
            if (isCXXValue(mirror.getAnnotationType())) {
                hasCXXValue = true;
            } else if (isCXXReference(mirror.getAnnotationType())) {
                hasCXXRef = true;
            }
        }
        if (hasCXXRef && hasCXXValue) {
            throw new IllegalStateException("Cannot be both CXXValue and CXXReference: " + element);
        }
        if (hasCXXValue) {
            return VRP.Value;
        }
        if (hasCXXRef) {
            return VRP.Reference;
        }
        return VRP.Pointer;
    }

    protected boolean isFFIMirror(TypeMirror typeMirror) {
        if (isFFIPointer(typeMirror)) {
            TypeElement typeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
            FFIMirror ffiMirror = typeElement.getAnnotation(FFIMirror.class);
            return ffiMirror != null;
        }
        return false;
    }

    protected TypeElement getTypeElement(Class<?> clazz) {
        return getTypeElement(clazz.getName());
    }

    protected TypeElement getTypeElement(String fullName) {
        return AnnotationProcessorUtils.getTypeElement(processingEnv, fullName);
    }

    protected TypeMirror getTypeMirror(Class<?> clazz) {
        return getTypeElement(clazz).asType();
    }

    /**
     * All type parameters are erased.
     * @param superType
     * @param subType
     * @return
     */
    protected boolean isSubType(TypeMirror subType, Class<?> superType) {
        return isSubType(subType, getTypeMirror(superType));
    }

    /**
     * Input must be declared type
     * @param superType
     * @param subType
     * @return
     */
    protected boolean isSubType(TypeMirror subType, TypeMirror superType) {
        return isDeclaredType(subType) && isDeclaredType(superType) &&
                typeUtils().isSubtype(
                        typeUtils().erasure(subType),
                        typeUtils().erasure(superType)
                );
    }

    protected boolean isJavaArray(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.ARRAY;
    }

    protected boolean isSameType(TypeMirror typeMirror, Class<?> clazz) {
        return typeUtils().isSameType(typeMirror, getTypeMirror(clazz));
    }

    protected boolean isJavaLangString(TypeMirror type) {
        if (isDeclaredType(type)) {
            return isJavaLangString((DeclaredType) type);
        }
        return false;
    }

    protected boolean isJavaClass(DeclaredType type, String className) {
        return ((TypeElement) type.asElement()).getQualifiedName().toString().equals(className);
    }

    protected boolean isJavaClass(DeclaredType type, Class<?> cls) {
        return isJavaClass(type, cls.getName());
    }

    protected boolean isCXXValue(DeclaredType annotationType) {
        return isJavaClass(annotationType, CXXValue.class);
    }

    protected boolean isCXXReference(DeclaredType annotationType) {
        return isJavaClass(annotationType, CXXReference.class);
    }

    protected boolean isJavaLangString(DeclaredType type) {
        return isJavaClass(type, "java.lang.String");
    }

    protected boolean isCXXTemplate(AnnotationMirror mirror) {
        return isJavaClass(mirror.getAnnotationType(), CXXTemplate.class);
    }

    protected boolean isFFITypeAlias(AnnotationMirror mirror) {
        return isJavaClass(mirror.getAnnotationType(), FFITypeAlias.class);
    }

    protected boolean isCXXTemplates(AnnotationMirror mirror) {
        return isJavaClass(mirror.getAnnotationType(), CXXTemplates.class);
    }

    protected String getTemplateParameters(ExecutableElement executableElement) {
        CXXTemplate cxxTemplate = executableElement.getAnnotation(CXXTemplate.class);
        if (cxxTemplate == null) {
            return "";
        }
        if (!cxxTemplate.cxxFull().isEmpty()) {
            return "<" + cxxTemplate.cxxFull() + ">";
        }
        List<? extends TypeParameterElement> typeParameters = executableElement.getTypeParameters();
        if (!typeParameters.isEmpty()) {
            throw new IllegalArgumentException("A method " + AnnotationProcessorUtils.format(executableElement) + " has non-empty type parameters.");
        }
        return "<" + String.join(",", cxxTemplate.cxx()) + ">";
    }

    protected boolean skipTypeParameter(TypeParameterElement typeParameterElement) {
        FFISkip skip = typeParameterElement.getAnnotation(FFISkip.class);
        return skip != null;
    }

    protected boolean skipParameter(VariableElement variableElement) {
        FFISkip ffiSkip = variableElement.getAnnotation(FFISkip.class);
        return ffiSkip != null;
    }

    protected String getDelegateNativeRawMethodName(ExecutableElement executableElement) {
        FFINameAlias nameAlias = executableElement.getAnnotation(FFINameAlias.class);
        if (nameAlias != null) {
            return nameAlias.value();
        }
        return executableElement.getSimpleName().toString();
    }

    protected String getDelegateNativeMethodName(ExecutableElement executableElement) {
        String cxxTplParams = getTemplateParameters(executableElement);
        FFINameAlias nameAlias = executableElement.getAnnotation(FFINameAlias.class);
        if (nameAlias != null) {
            return nameAlias.value() + cxxTplParams;
        }
        return executableElement.getSimpleName().toString() + cxxTplParams;
    }

    /**
     * Conversion for types that should will be used as parameter types
     * or return types of a native method.
     * @param typeMirror
     * @return
     */
    protected TypeMirror usedInNative(TypeMirror typeMirror) {
        if (isFFIPointer(typeMirror)) {
            return typeUtils().getPrimitiveType(TypeKind.LONG);
        }
        if (isCXXEnum(typeMirror)) {
            return typeUtils().getPrimitiveType(TypeKind.INT);
        }
        return tryUnboxing(typeMirror);
    }

    protected TypeMirror tryUnboxing(TypeMirror typeMirror) {
        if (isJavaBoxedPrimitive(typeMirror)) {
            return typeUtils().unboxedType(typeMirror);
        }
        return typeMirror;
    }

    protected String nativeType(TypeMirror javaType) {
        TypeKind kind = javaType.getKind();
        switch (kind) {
            case VOID:
                return "void";
            case BOOLEAN:
                return "jboolean";
            case BYTE:
                return "jbyte";
            case SHORT:
                return "jshort";
            case CHAR:
                return "jchar";
            case INT:
                return "jint";
            case FLOAT:
                return "jfloat";
            case LONG:
                return "jlong";
            case DOUBLE:
                return "jdouble";
            case ARRAY:
                ArrayType arrayType = (ArrayType) javaType;
                TypeMirror componentType = arrayType.getComponentType();
                if (isJavaPrimitive(componentType)) {
                    return nativeType(componentType) + "Array";
                } else {
                    return "jobjectArray";
                }
            case DECLARED:
                TypeElement typeElement = (TypeElement) typeUtils().asElement(javaType);
                String typeName = typeElement.getQualifiedName().toString();
                if (typeName.equals("java.lang.String")) {
                    //return "jstring";
                    throw new IllegalStateException("TODO: Not supported now");
                } else if (typeName.equals("java.lang.Class")) {
                    return "jclass";
                }
                return "jobject";
            default:
                throw new IllegalArgumentException("Unsupported type: " + javaType);
        }
    }

    protected String nativeType(TypeMirror javaType, Element element) {
        if (element != null) {
            FFITypeAlias typeAlias = element.getAnnotation(FFITypeAlias.class);
            if (typeAlias != null) {
                return typeAlias.value();
            }
        }
        return nativeType(javaType);
    }

    protected boolean requireCreatingTypeMapping(TypeMirror typeMirror) {
        if (isDeclaredType(typeMirror)) {
            return requireCreatingTypeMapping((DeclaredType) typeMirror);
        }
        return false;
    }

    protected boolean requireCreatingTypeMapping(DeclaredType declaredType) {
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        // 1. Must be a foreign type.
        if (isFFIPointer(declaredType) || isCXXEnum(declaredType)) {
            // 2. Must have a foreign type name
            if (isFFIBuiltinType(declaredType)) {
                return true;
            }
            if (typeElement.getAnnotation(FFITypeAlias.class) == null) {
                throw new IllegalStateException("A foreign type " + declaredType + " does not have FFITypeAlias");
            }
            return true;
        }
        return false;
    }


    protected boolean requireCreatingTypeMapping(ExecutableType executableType) {
        if (requireCreatingTypeMapping(executableType.getReturnType())) {
            return true;
        }
        for (TypeMirror typeMirror : executableType.getParameterTypes()) {
            if (requireCreatingTypeMapping(typeMirror)) {
                return true;
            }
        }
        return false;
    }

    protected boolean requireManualBoxing(TypeMirror typeMirror) {
        if (!(isDeclaredType(typeMirror))) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        String name = typeElement.getQualifiedName().toString();
        switch (name) {
            // All bytes are cached
            case "java.lang.Character":
            case "java.lang.Short":
            case "java.lang.Integer":
            case "java.lang.Long":
                return true;
            default:
                break;
        }
        return false;
    }

    protected boolean isJavaBoxedPrimitive(TypeMirror typeMirror) {
        if (!(isDeclaredType(typeMirror))) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        String name = typeElement.getQualifiedName().toString();
        switch (name) {
            // no unboxing of java.lang.Void since void cannot be used as a type of a Java variable/field
            case "java.lang.Byte":
            case "java.lang.Boolean":
            case "java.lang.Character":
            case "java.lang.Short":
            case "java.lang.Integer":
            case "java.lang.Float":
            case "java.lang.Long":
            case "java.lang.Double":
                return true;
            default:
                break;
        }
        return false;
    }

    protected boolean isExpandedTemplate(TypeElement theTypeElement) {
        // We always add a suffix to the original name no matter whether there is a conflict.
        if (!theTypeElement.getSimpleName().toString().endsWith("Gen")) {
            return false;
        }

        FFISynthetic ann = theTypeElement.getAnnotation(FFISynthetic.class);
        if (ann == null) {
            return false;
        }

        // Various patterns:
        // Pattern #1: The generated interface has only one super interface, which is the template interface.
        List<? extends TypeMirror> interfaces = theTypeElement.getInterfaces();
        if (interfaces.size() != 1) {
            return false;
        }

        // Pattern #2: The name of the only super interface must match what we have recorded via FFISynthetic
        TypeElement interfaceElement = (TypeElement) typeUtils().asElement(interfaces.get(0));
        String superTypeName = interfaceElement.getQualifiedName().toString();
        return ann.value().equals(superTypeName);
    }

    protected Set<String> getTypeVariableNames(TypeElement typeElement) {
        return typeElement.getTypeParameters().stream().map(p -> p.getSimpleName().toString()).collect(Collectors.toSet());
    }

    protected Set<String> getMethodTypeVariableNames(ExecutableElement executableElement) {
        return executableElement.getTypeParameters().stream().map(p -> p.getSimpleName().toString()).collect(Collectors.toSet());
    }

    protected TypeMirror erasure(TypeMirror typeMirror) {
        if (isDeclaredType(typeMirror)) {
            return erasure((DeclaredType) typeMirror);
        }
        if (isArrayType(typeMirror)) {
            ArrayType arrayType = (ArrayType) typeMirror;
            return typeUtils().getArrayType(erasure(arrayType.getComponentType()));
        }
        return typeMirror;
    }

    protected DeclaredType erasure(DeclaredType javaType) {
        if (!javaType.getTypeArguments().isEmpty()) {
            return (DeclaredType) typeUtils().erasure(javaType);
        }
        return javaType;
    }

    protected Map<String, TypeMapping> createTopLevelTypeMapping(CXXTemplate template, List<? extends TypeParameterElement> typeParameterElements, TypeElement context) {
        if (template != null) {
            Map<String, TypeMapping> nameToMapping = new HashMap<>();
            String[] cxx = template.cxx();
            String[] java = template.java();
            if (cxx.length != typeParameterElements.size()) {
                throw new IllegalStateException("the amount of type parameter mismatched");
            }
            if (java.length != cxx.length) {
                throw new IllegalStateException("CXXTemplate does not match method type variable length: " + template);
            }
            for (int i = 0; i < cxx.length; i++) {
                TypeMapping typeMapping = new TypeMapping(cxx[i], typeNameToDeclaredType(processingEnv, java[i], context));
                nameToMapping.put(typeParameterElements.get(i).getSimpleName().toString(), typeMapping);
            }
            return nameToMapping;
        }
        return Collections.emptyMap();
    }

    protected TypeMapping createTypeMapping(Map<String, TypeMapping> enclosingTypeMapping, FFITypeAlias typeAlias, TypeMirror typeMirror,
                                            Set<String> unboundTypeVariables) {
        return createTypeMapping(enclosingTypeMapping, typeAlias.value(), typeMirror, unboundTypeVariables);
    }

    protected String checkAndReplace(String cxx, Map<String, TypeMapping> enclosingTypeMapping, Set<String> unboundTypeVariables) {
        if (!cxx.startsWith("#")) {
            return cxx;
        }
        String variableName = cxx.substring(1);
        TypeMapping typeMapping = enclosingTypeMapping.get(variableName);
        if (typeMapping != null) {
            if (isUnboundType(typeMapping.java)) {
                throw new IllegalStateException("Oops: enclosing type mapping is unbound: " + typeMapping);
            }
            return typeMapping.cxx;
        } else {
            if (unboundTypeVariables.contains(variableName)) {
                return cxx;
            }
        }
        throw new IllegalStateException("Oops: cannot substitute cxx type " + cxx + ", env=" + enclosingTypeMapping + ", free=" + unboundTypeVariables);
    }

    protected TypeMapping createTypeMapping(Map<String, TypeMapping> enclosingTypeMapping, String cxx, TypeMirror typeMirror,
                                            Set<String> unboundTypeVariables) {
        if (!isUnboundType(typeMirror)) {
            return new TypeMapping(cxx, typeMirror);
        }

        if (!isDeclaredType(typeMirror)) {
            // must be error type: return anything?
            return new TypeMapping(cxx, typeMirror);
        }

        int begin = 0;
        int i = 0;
        final int length = cxx.length();
        Stack<List<String>> typeMirrorStack = new Stack<>();
        List<String> currentList = new ArrayList<>();
        while (i < length) {
            char c = cxx.charAt(i);
            if (c == '<' || c == ',' || c == '>') {
                String part = cxx.substring(begin, i).trim();
                part = checkAndReplace(part, enclosingTypeMapping, unboundTypeVariables);
                begin = i + 1;
                if (c == '<') {
                    currentList.add(part);
                    typeMirrorStack.push(currentList);
                    currentList = new ArrayList<>();
                } else if (c == '>') {
                    if (!part.isEmpty()) {
                        currentList.add(part);
                    } // else {} already set
                    if (typeMirrorStack.isEmpty()) {
                        throw new IllegalStateException("Mis-matching <> in type name " + cxx);
                    }
                    List<String> lastList = typeMirrorStack.pop();
                    if (lastList.isEmpty()) {
                        throw new IllegalStateException();
                    }
                    int lastIndex = lastList.size() - 1;
                    String last = lastList.get(lastIndex);
                    last = String.format("%s<%s>", last, String.join(",", currentList));
                    lastList.set(lastIndex, last);
                    currentList = lastList;
                } else { // must be ','
                    currentList.add(part);
                }
            }
            i++;
        }
        if (!typeMirrorStack.isEmpty()) {
            throw new IllegalStateException();
        }
        if (currentList.size() != 1) {
            throw new IllegalArgumentException("Current list is not singleton: " + currentList + "for " + cxx);
        }
        return new TypeMapping(currentList.get(0), substitute((DeclaredType) typeMirror, enclosingTypeMapping, unboundTypeVariables));
    }

    /**
     *
     * @param enclosingTypeMapping
     * @param typeMirror
     * @param unboundTypeVariables: TODO: we cannot support unbound CXX templates here.
     * @return
     */
    protected TypeMapping createTypeMapping(Map<String, TypeMapping> enclosingTypeMapping, TypeMirror typeMirror,
                                          Set<String> unboundTypeVariables) {
        if (isDeclaredType(typeMirror)) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            if (!requireCreatingTypeMapping(declaredType)) {
                return null;
            }
            TypeMapping typeMapping =
                    substitute(enclosingTypeMapping, (TypeElement) declaredType.asElement(), declaredType, unboundTypeVariables);
            if (typeMapping == null) {
                if (unboundTypeVariables.isEmpty()) {
                    // We must successful if there is no unbound type variables.
                    throw new IllegalArgumentException("Oops, cannot determine TypeMapping for " + typeMirror);
                }
                // We may receive a null type mapping here because
                // we cannot create a type mapping for types that have unbound type variables.
                return null;
            }
            return typeMapping;
        } else if (isTypeVariable(typeMirror)) {
            TypeVariable typeVariable = (TypeVariable) typeMirror;
            String typeVariableName = typeVariable.asElement().getSimpleName().toString();
            TypeMapping typeMapping = enclosingTypeMapping.get(typeVariableName);
            if (typeMapping == null) {
                if (unboundTypeVariables.contains(typeVariableName)) {
                    // We cannot create a type mapping for types that have unbound type variables.
                    return null;
                }
                throw new IllegalStateException("No type mapping for " + typeVariableName);
            }
            return typeMapping;
        } else if (isPrimitiveType(typeMirror)) {
            return null;
        } else if (typeMirror.getKind() == TypeKind.VOID) {
            return null;
        }  // array, etc.

        throw new IllegalArgumentException("Unsupported type: " + typeMirror);
    }

    protected boolean isUnboundType(TypeMirror typeMirror) {
        if (isTypeVariable(typeMirror)) {
            return true;
        }
        if (isDeclaredType(typeMirror)) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            return declaredType.getTypeArguments().stream().anyMatch(AnnotationProcessorUtils::isTypeVariable);
        }
        return false;
    }

    DeclaredType getFFIBuiltinImplType(DeclaredType javaType) {
        if (isFFIBuiltinType(javaType)) {
            if (isFFIByteString(javaType)) {
                return (DeclaredType) getTypeMirror(CXXStdString.class);
            }
            if (isFFIVector(javaType)) {
                List<? extends TypeMirror> typeArguments = javaType.getTypeArguments();
                TypeMirror[] newTypeArguments = typeArguments.stream().map(t -> getFFIBuiltinImplType((DeclaredType) t)).toArray(TypeMirror[]::new);
                TypeElement baseElement = getTypeElement(CXXStdVector.class);
                return typeUtils().getDeclaredType(baseElement, newTypeArguments);
            }
        }
        if (isJavaBoxedPrimitive(javaType)) {
            TypeMirror unboxed = tryUnboxing(javaType);
            switch (unboxed.getKind()) {
                case BYTE:
                case BOOLEAN:
                case CHAR:
                case SHORT:
                case INT:
                case LONG:
                case FLOAT:
                case DOUBLE:
                    return javaType;
                default:
                    throw new IllegalStateException("TODO: unsupported boxed primitive type: " + javaType);
            }
        }
        if (isFFIMirror(javaType)) {
            if (!javaType.getTypeArguments().isEmpty()) {
                throw new IllegalStateException("TODO: parameterized FFIMirror is not implemented yet.");
            }
            return javaType;
        }
        // TODO: builtin type must be work with FFIMirror
        throw new IllegalStateException("TODO: unsupported type: " + javaType);
    }

    protected TypeElement getDeclaredTypeElement(TypeElement element) {
        FFISynthetic synthetic = element.getAnnotation(FFISynthetic.class);
        if (synthetic == null) {
            return element;
        }
        return elementUtils().getTypeElement(synthetic.value());
    }

    protected boolean genIfMissing(DeclaredType javaType) {
        return isFFIBuiltinType(javaType);
    }

    protected TypeDef getTypeDefByForeignName(String foreignType, DeclaredType javaType) {
        return registry.getTypeDefByForeignName(foreignType, javaType, getDeclaredTypeElement((TypeElement) javaType.asElement()), genIfMissing(javaType));
    }

    private TypeMirror substitute(DeclaredType theType, Map<String, TypeMapping> boundVariables, Set<String> freeVariables) {
        if (!isDeclaredType(theType)) {
            // must be an error type
            return theType;
        }
        List<? extends TypeMirror> typeArguments = theType.getTypeArguments();
        TypeMirror[] newArguments = new TypeMirror[typeArguments.size()];
        for (int i = 0; i < typeArguments.size(); i++) {
            TypeMirror typeArg = typeArguments.get(i);
            if (isTypeVariable(typeArg)) {
                TypeVariable typeVariable = (TypeVariable) typeArg;
                String typeVariableName = typeVariable.asElement().getSimpleName().toString();
                TypeMapping typeMapping = boundVariables.get(typeVariableName);
                if (typeMapping == null) {
                    if (freeVariables.contains(typeVariableName)) {
                        // Use # as a placeholder since we have no idea what the exact type variable in C++ is.
                        // We have non-empty unboundTypeVariables
                        newArguments[i] = typeArg;
                    } else {
                        throw new IllegalStateException("No mapping for TypeVariable "
                                + typeVariableName + " in " + theType);
                    }
                } else {
                    newArguments[i] = typeMapping.java;
                }
            } else if (isDeclaredType(typeArg)) {
                DeclaredType declaredType = (DeclaredType) typeArg;
                newArguments[i] = substitute(declaredType, boundVariables, freeVariables);
            } else {
                throw new IllegalStateException("Unsupported type: " + typeArg);
            }
        }
        return typeUtils().getDeclaredType((TypeElement) theType.asElement(), newArguments);
    }

    /**
     *
     * @param enclosingTypeMapping: type environment used to lookup mapping for type parameter
     * @param typeElement:
     * @param theType: with unbound type variable
     * @param unboundTypeVariables: only variables in this set are allowed to be unbound
     * @return
     */
    private TypeMapping substitute(Map<String, TypeMapping> enclosingTypeMapping, TypeElement typeElement, DeclaredType theType,
                                   Set<String> unboundTypeVariables) {
        if (!requireCreatingTypeMapping(theType)) {
            return null;
        }
        String ffiRawType;
        boolean isBuiltinType = isFFIBuiltinType(theType);
        {
            FFITypeAlias typeAlias = typeElement.getAnnotation(FFITypeAlias.class);
            if (typeAlias == null) {
                if (!isBuiltinType) {
                    throw new IllegalStateException("A foreign type " + typeElement + " must have a FFITypeAlias");
                } else {
                    if (theType.getTypeArguments().isEmpty()) {
                        // no type variable is allowed in builtin type use.
                        ffiRawType = getFFITypeNameForBuiltinType(theType);
                        return new TypeMapping(ffiRawType, theType);
                    } else {
                        ffiRawType = getFFITypeNameForBuiltinType((DeclaredType) typeUtils().erasure(theType));
                    }
                }
            } else {
                ffiRawType = getForeignTypeByTypeAlias(theType);
            }
        }
        List<? extends TypeMirror> typeArguments = theType.getTypeArguments();
        if (typeArguments.isEmpty()) {
            // No need to generate any data
            TypeDef internalType = getTypeDefByForeignName(ffiRawType, theType);

            if (internalType != null) {
                if (!typeUtils().isAssignable(internalType.getDeclaredTypeMirror(processingEnv), theType)) {
                    throw new IllegalStateException("Mismatching internal type for " + ffiRawType
                            + " find " + internalType.getDeclaredTypeMirror(processingEnv) + ", expected " + theType);
                }
                return new TypeMapping(ffiRawType, internalType.getDeclaredTypeMirror(processingEnv));
            } else /* if (internalType == null) */ {
                if (typeElement.getAnnotation(FFIGen.class) != null) {
                    throw new IllegalStateException("Cannot find an internal generated type for " + ffiRawType);
                }
            }
            return new TypeMapping(ffiRawType, theType);
        }
        TypeMapping[] newTypeMapping = new TypeMapping[typeArguments.size()];
        for (int i = 0; i < typeArguments.size(); i++) {
            TypeMirror typeArg = typeArguments.get(i);
            if (isTypeVariable(typeArg)) {
                TypeVariable typeVariable = (TypeVariable) typeArg;
                String typeVariableName = typeVariable.asElement().getSimpleName().toString();
                TypeMapping typeMapping = enclosingTypeMapping.get(typeVariableName);
                if (typeMapping == null) {
                    if (unboundTypeVariables.contains(typeVariableName)) {
                        // Use # as a placeholder since we have no idea what the exact type variable in C++ is.
                        // We have non-empty unboundTypeVariables
                        typeMapping = new TypeMapping("#" + typeVariableName, typeArg);
                    } else {
                        throw new IllegalStateException("No mapping for TypeVariable "
                                + typeVariableName + " in " + typeElement + "/" + theType);
                    }
                }
                newTypeMapping[i] = typeMapping;
            } else if (isDeclaredType(typeArg)) {
                DeclaredType declaredType = (DeclaredType) typeArg;
                newTypeMapping[i] = substitute(enclosingTypeMapping, (TypeElement) declaredType.asElement(), declaredType, unboundTypeVariables);
                if (newTypeMapping[i] == null) {
                    if (isBuiltinType) {
                        String foreignType = getFFITypeNameForBuiltinType(declaredType);
                        newTypeMapping[i] = new TypeMapping(foreignType, declaredType);
                    } else {
                        return null;
                    }
                }
            } else {
                throw new IllegalStateException("Unsupported type: " + typeArg);
            }
        }
        String cxx = null;
        String[] parameters = Arrays.stream(newTypeMapping).map(typeMapping -> typeMapping.cxx).toArray(String[]::new);
        if (ffiRawType.contains("%")) {
            try {
                cxx = String.format(ffiRawType, Arrays.stream(parameters).toArray());
            } catch (IllegalFormatException e) {
                throw new IllegalArgumentException("Cannot format cxx typename with \n" +
                        "\tformat: '" + ffiRawType + "'\n" +
                        "\targuments: (" + String.join(", ", parameters) + ")\n" +
                        "with error: " + e);
            }
        } else {
            cxx = ffiRawType + "<" + String.join(",", parameters) + ">";
        }
        TypeMirror java = typeUtils().getDeclaredType(typeElement, Arrays.stream(newTypeMapping).map(typeMapping -> typeMapping.java).toArray(TypeMirror[]::new));
        return new TypeMapping(cxx, java);
    }

    protected String getFFITypeNameForFFIMirrorType(TypeMirror theType, boolean isJava) {
        if (isPrimitiveType(theType)) {
            String javaTypeName = AnnotationProcessorUtils.typeToTypeName(theType);
            if (isJava) {
                return javaTypeName;
            }
            switch (theType.getKind()) {
                case BYTE:
                case BOOLEAN:
                case CHAR:
                case SHORT:
                case INT:
                case LONG:
                case FLOAT:
                case DOUBLE:
                    return "j" + javaTypeName;
            }
        }
        if (isDeclaredType(theType)) {
            return getFFITypeNameForBuiltinType((DeclaredType) theType, isJava);
        }
        throw new IllegalStateException("TODO: unsupported Java type " + theType);
    }

    protected String getFFITypeNameForBuiltinType(DeclaredType theType) {
        return getFFITypeNameForBuiltinType(theType, false);
    }

    protected String getFFITypeNameForBuiltinType(DeclaredType theType, boolean isJava) {
        if (!isDeclaredType(theType)) {
            throw new IllegalArgumentException("Not a valid declare type: " + theType);
        }
        if (isFFIBuiltinType(theType)) {
            if (isFFIByteString(theType)) {
                return isJava ? FFIByteString.class.getName() : "std::string";
            }
            if (isFFIVector(typeUtils().erasure(theType))) {
                String base = isJava ? FFIVector.class.getName() : "std::vector";
                List<? extends TypeMirror> typeArguments = theType.getTypeArguments();
                if (typeArguments.isEmpty()) {
                    return base;
                }
                return base + "<" +
                        typeArguments.stream().map(t -> getFFITypeNameForBuiltinType((DeclaredType) t, isJava)).collect(Collectors.joining(","))
                        + ">";
            }
            throw new IllegalStateException("TODO: unsupported builtin type: " + theType);
        }
        if (isJavaBoxedPrimitive(theType)) {
            if (isJava) {
                return getTypeName(theType);
            }
            TypeMirror unboxed = tryUnboxing(theType);
            switch (unboxed.getKind()) {
                case BYTE:
                case BOOLEAN:
                case CHAR:
                case SHORT:
                case INT:
                case LONG:
                case FLOAT:
                case DOUBLE:
                    return "j" + unboxed;
                default:
                    throw new IllegalStateException("TODO: unsupported boxed primitive type: " + theType);
            }
        }
        if (isFFIMirror(theType)) {
            if (!theType.getTypeArguments().isEmpty()) {
                throw new IllegalStateException("TODO: parameterized FFIMirror is not implemented yet.");
            }
            if (isJava) {
                return getTypeName(theType);
            }
            return getForeignTypeByTypeAlias(theType);
        }
        throw new IllegalStateException("TODO: unsupported type: " + theType);
    }

    private String getForeignTypeByTypeAlias(DeclaredType theType) {
        TypeElement typeElement = (TypeElement) theType.asElement();
        FFITypeAlias typeAlias = typeElement.getAnnotation(FFITypeAlias.class);

        if (typeAlias == null) {
            throw new IllegalStateException("Type " + theType + " does not have a FFITypeAlias.");
        }
        String ffiType = typeAlias.value();
        if (ffiType.isEmpty()) {
            throw new IllegalStateException("Type " + theType + " has an empty FFITypeAlias.");
        }
        FFINameSpace nameSpace = typeElement.getAnnotation(FFINameSpace.class);
        if (nameSpace != null) {
            // TODO: here is hard coded C++.
            ffiType = nameSpace.value() + "::" + ffiType;
        }
        return ffiType;
    }



    /**
     *
     * @param theTypeElement: the type element that we want to start from
     * @param topLevelNameToMapping: created from CXXTemplate if we want to insantiate a template or empty if we do not want.
     * @return
     */
    protected Map<ExecutableElement, ExecutableTypeMapping> collectForeign(TypeElement theTypeElement, Map<String, TypeMapping> topLevelNameToMapping) {
        Map<ExecutableElement, ExecutableTypeMapping> executableElementExecutableTypeMappingMap = new HashMap<>();
        Map<String, TypeMapping> nameToMapping = new HashMap<>(topLevelNameToMapping);
        collectForeign(executableElementExecutableTypeMappingMap, nameToMapping, theTypeElement, (DeclaredType) theTypeElement.asType());
        return executableElementExecutableTypeMappingMap;
    }

    protected boolean isStatic(ExecutableElement executableElement) {
        return executableElement.getModifiers().contains(Modifier.STATIC);
    }

    protected boolean isAbstractInterface(ExecutableElement executableElement) {
        if (executableElement.isDefault()) {
            return false;
        }
        Set<Modifier> modifiers = executableElement.getModifiers();
        return modifiers.contains(Modifier.ABSTRACT) && !modifiers.contains(Modifier.STATIC);
    }

    /**
     *
     * @param results: mapping from executable element to executable type mapping
     * @param enclosingTypeMapping: mapping from type variables to Java types
     * @param typeElement: the declaration element
     * @param typeMirror: the declared (raw) type of the type element.
     */
    protected void collectForeign(Map<ExecutableElement, ExecutableTypeMapping> results,
                                Map<String, TypeMapping> enclosingTypeMapping,
                                TypeElement typeElement, DeclaredType typeMirror) {
        // build a mapping from executable element
        typeElement.getEnclosedElements().stream().filter( e -> e instanceof ExecutableElement).forEach(
                e -> {
                    ExecutableElement ee = (ExecutableElement) e;

                    if (isAbstractInterface(ee)) {
                        ExecutableType executableType = (ExecutableType) typeUtils().asMemberOf(typeMirror, ee);
                        Set<String> unboundTypeVariables;
                        if (ee.getTypeParameters().isEmpty()) {
                            unboundTypeVariables = Collections.emptySet();
                        } else {
                            unboundTypeVariables = getMethodTypeVariableNames(ee);
                        }
                        TypeMapping returnTypeMapping;
                        {
                            TypeMirror returnType = executableType.getReturnType();
                            FFITypeAlias typeAlias = ee.getAnnotation(FFITypeAlias.class);
                            if (typeAlias == null) {
                                returnTypeMapping = createTypeMapping(enclosingTypeMapping, returnType, unboundTypeVariables);
                            } else {
                                returnTypeMapping = createTypeMapping(enclosingTypeMapping, typeAlias, returnType, unboundTypeVariables);
                            }
                        }
                        List<? extends VariableElement> parameterElements = ee.getParameters();
                        List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
                        if (parameterElements.size() != parameterTypes.size()) {
                            throw new IllegalArgumentException();
                        }
                        TypeMapping[] parametersTypeMapping = new TypeMapping[parameterElements.size()];
                        for (int i = 0; i < parameterElements.size(); i++) {
                            VariableElement variableElement = parameterElements.get(i);
                            FFITypeAlias typeAlias = variableElement.getAnnotation(FFITypeAlias.class);
                            if (typeAlias == null) {
                                parametersTypeMapping[i] = createTypeMapping(enclosingTypeMapping, parameterTypes.get(i), unboundTypeVariables);
                            } else {
                                parametersTypeMapping[i] = createTypeMapping(enclosingTypeMapping, typeAlias, parameterTypes.get(i), unboundTypeVariables);
                            }
                        }
                        ExecutableTypeMapping executableTypeMapping = new ExecutableTypeMapping(returnTypeMapping, parametersTypeMapping);
                        // TODO: may be visited twice if the interface has been inherited by different super classes.
                        results.put(ee, executableTypeMapping);
                    }
                }
        );

        List<? extends TypeMirror> directSuperTypes = typeUtils().directSupertypes(typeMirror);
        if (directSuperTypes.size() > 1) {
            for (int i = 1; i < directSuperTypes.size(); i++) {
                DeclaredType interfaceMirror = (DeclaredType) directSuperTypes.get(i);
                TypeElement interfaceElement = (TypeElement) interfaceMirror.asElement();
                Map<String, TypeMapping> interfaceNameToMapping =
                    computeInterfaceTypeMapping(typeElement, interfaceMirror, enclosingTypeMapping);
                collectForeign(results, interfaceNameToMapping, interfaceElement,
                               (DeclaredType) interfaceElement.asType());
            }
        }
    }

    protected Map<String, TypeMapping> computeInterfaceTypeMapping(TypeElement typeElement, DeclaredType interfaceMirror,
                                                                   Map<String, TypeMapping> enclosingTypeMapping) {
        // interface type arguments are types already defined in the instantiated interface mirror
        List<? extends TypeMirror> interfaceTypeArguments = interfaceMirror.getTypeArguments();

        TypeElement interfaceElement = (TypeElement) interfaceMirror.asElement();
        // interface type parameters are formal type parameters defined in the interface type element
        List<? extends TypeParameterElement> interfaceTypeParameterElements = interfaceElement.getTypeParameters();

        // superTypeMapping is a mapping between type variable of the super interface
        Map<String, TypeMapping> superTypeMapping = getSuperTypeMapping(typeElement, interfaceElement);
        // interfaceNameToMapping is a mapping between type variables of the interface type mirror
        // and the instantiated types of the type variable.
        // Note that the instantiated type may also be a type variable of the enclosing environment.
        // In this case, superTypeMapping does not have a type mapping for the type parameter.
        Map<String, TypeMapping> interfaceNameToMapping = new HashMap<>();

        for (int j = 0; j < interfaceTypeArguments.size(); j++) {
            TypeParameterElement interfaceTypeParameterElement = interfaceTypeParameterElements.get(j);
            String variableName = interfaceTypeParameterElement.getSimpleName().toString();
            TypeMapping typeMapping = superTypeMapping.get(variableName);
            TypeMirror typeArg = interfaceTypeArguments.get(j);
            if (typeMapping != null) {
                if (isTypeVariable(typeArg)) {
                    throw new IllegalStateException("TypeArg should not have a configured type mapping");
                }
                // find a configured type mapping and use it directly
                interfaceNameToMapping.put(variableName, typeMapping);
                continue;
            }
            // Now we need to compute the actual type mapping of each type parameters.
            if (isTypeVariable(typeArg)) {
                TypeVariable typeVariable = (TypeVariable) typeArg;
                String typeVariableName = typeVariable.asElement().getSimpleName().toString();
                typeMapping = enclosingTypeMapping.get(typeVariableName);
                if (typeMapping == null) {
                    // we did not track the argument for a type variable
                    // and assume it would be an Integer or something that does not need an explicit foreign type.
                    // Otherwise, we need to create a TypeMapping for DefaultMapping
                    continue;
                }
                interfaceNameToMapping.put(variableName, typeMapping);
            } else if (isDeclaredType(typeArg)) {
                DeclaredType declaredType = (DeclaredType) typeArg;
                typeMapping = substitute(enclosingTypeMapping, (TypeElement) declaredType.asElement(), declaredType, Collections.emptySet());
                if (typeMapping == null) {
                    if (requireCreatingTypeMapping(declaredType)) {
                        throw new IllegalStateException("The type argument of a super type must be bound to a foreign type.");
                    }
                    continue;
                }
                interfaceNameToMapping.put(variableName, typeMapping);
            } else {
                // e.g., Array
                throw new IllegalStateException("Unsupported type: " + typeArg);
            }
        }
        return interfaceNameToMapping;
    }

    /**
     *
     * @param typeMirror
     * @return
     */
    protected String getCXXEnumType(TypeMirror typeMirror) {
        DeclaredType declaredType = (DeclaredType) typeMirror;
        if (!declaredType.getTypeArguments().isEmpty()) {
            throw new IllegalStateException("TODO: Do not know how to support template in enum");
        }
        return getForeignTypeByTypeAlias(declaredType);
    }


    protected void instantiateSuperTemplates(CXXSuperTemplate superTemplate) {
        TypeElement typeElement = getTypeElement(superTemplate.type());
        registry.processType(processingEnv, typeElement, superTemplate.template(), true);
    }


    TypeMapping[] getTypeArgumentsTypeMapping(List<? extends TypeMirror> typeArguments,
                                              Map<String, TypeMapping> enclosingTypeMapping) {
        TypeMapping[] parameterTypeMapping = new TypeMapping[typeArguments.size()];
        for (int i = 0; i < typeArguments.size(); i++) {
            TypeMirror typeArg = typeArguments.get(i);
            if (isTypeVariable(typeArg)) {
                TypeVariable typeVariable = (TypeVariable) typeArg;
                String typeVariableName = typeVariable.asElement().getSimpleName().toString();
                TypeMapping typeMapping = enclosingTypeMapping.get(typeVariableName);
                if (typeMapping == null) {
                    return null;
                }
                parameterTypeMapping[i] = typeMapping;
            } else if (isDeclaredType(typeArg)) {
                DeclaredType declaredType = (DeclaredType) typeArg;
                TypeMapping typeMapping = substitute(enclosingTypeMapping, (TypeElement) declaredType.asElement(), declaredType, Collections.emptySet());
                if (typeMapping == null) {
                    return null;
                }
                parameterTypeMapping[i] = typeMapping;
            } else {
                throw new IllegalStateException("Unsupported type: " + typeArg);
            }
        }
        return parameterTypeMapping;
    }

    protected Map<String, TypeMapping> getSuperTypeMapping(TypeElement typeElement, TypeElement interfaceElement) {
        List<? extends TypeParameterElement> typeParameters = interfaceElement.getTypeParameters();
        if (typeParameters.isEmpty()) {
            return Collections.emptyMap();
        }
        CXXSuperTemplate superTemplate = getSuperTemplate(typeElement, interfaceElement);
        if (superTemplate == null) {
            return Collections.emptyMap();
        }
        if (typeParameters.size() != superTemplate.template().cxx().length) {
            throw new IllegalStateException("Inconsistent number between type parameters and super template.");
        }
        instantiateSuperTemplates(superTemplate);
        int size = typeParameters.size();
        String[] cxx = superTemplate.template().cxx();
        String[] java = superTemplate.template().java();
        Map<String, TypeMapping> variableNameToTypeMapping = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String cxxType = cxx[i];
            if (cxxType.isEmpty()) {
                continue;
            }
            String typeVariableName = typeParameters.get(i).getSimpleName().toString();
            TypeMirror javaType = typeNameToDeclaredType(processingEnv, java[i], typeElement);
            variableNameToTypeMapping.put(typeVariableName, new TypeMapping(cxxType, javaType));
        }
        return variableNameToTypeMapping;
    }

    protected CXXSuperTemplate getSuperTemplate(TypeElement typeElement, TypeElement interfaceElement) {
        CXXSuperTemplate[] superTemplates = typeElement.getAnnotationsByType(CXXSuperTemplate.class);
        if (superTemplates == null) {
            return null;
        }
        String name = interfaceElement.getQualifiedName().toString();
        for (CXXSuperTemplate superTemplate : superTemplates) {
            if (superTemplate.type().equals(name)) {
                return superTemplate;
            }
        }
        return null;
    }

    protected TypeGenType probeGenType(TypeElement theTypeElement) {
        List<ExecutableElement> executableElements = collectExecutableElements(theTypeElement);

        for (ExecutableElement executableElement : executableElements) {
            if (isAbstractInterface(executableElement)) {
                // generateFFITemplate must add a default methods to override the original declaration.
                if (!executableElement.getTypeParameters().isEmpty()) {
                    return TypeGenType.FFITemplate;
                }
            }
        }

        boolean isFFILibraryElement = isFFILibraryElement(theTypeElement);
        boolean isFFIPointerElement = isFFIPointerElement(theTypeElement);

        if (isFFILibraryElement) {
            if (isFFIPointerElement) {
                throw new IllegalStateException("A TypeElement " + theTypeElement + " cannot be both FFIPointer and FFILibrary");
            }
            return TypeGenType.FFILibrary;
        } else {
            if (isFFIPointerElement) {
                return TypeGenType.FFIPointer;
            }
            throw new IllegalStateException("A TypeElement " + theTypeElement + " must be either FFIPointer or FFILibrary");
        }
    }

    protected List<ExecutableElement> collectExecutableElements(TypeElement typeElement) {
        LinkedList<TypeElement> queue = new LinkedList<>();
        List<ExecutableElement> result = new ArrayList<>();
        Map<Name, List<ExecutableElement>> mapping = new HashMap<>();
        Set<Name> visited = new HashSet<>();
        queue.addLast(typeElement);
        while (!queue.isEmpty()) {
            TypeElement current = queue.removeFirst();
            visited.add(current.getQualifiedName());
            for (Element ee : current.getEnclosedElements()) {
                if (ee instanceof ExecutableElement) {
                    ExecutableElement executableElement = (ExecutableElement) ee;
                    if (isStatic(executableElement)) {
                        continue;
                    }
                    if (isHiddenOrOverridenByElement(mapping, executableElement)) {
                        continue;
                    }
                    result.add(executableElement);
                    addToMapList(mapping, executableElement.getSimpleName(), executableElement);
                }
            }
            TypeMirror typeMirror = current.asType();
            List<? extends TypeMirror> directSuperTypes = typeUtils().directSupertypes(typeMirror);
            if (directSuperTypes.size() > 1) {
                for (int i = 1; i < directSuperTypes.size(); i++) {
                    DeclaredType interfaceMirror = (DeclaredType) directSuperTypes.get(i);
                    TypeElement interfaceElement = (TypeElement) interfaceMirror.asElement();
                    Name FQN = interfaceElement.getQualifiedName();
                    if (visited.contains(FQN)) {
                        continue;
                    }
                    queue.addLast(interfaceElement);
                }
            }
        }
        return result;
    }

    protected TypeElement asElement(TypeMirror typeMirror) {
        if (isDeclaredType(typeMirror)) {
            return (TypeElement) ((DeclaredType) typeMirror).asElement();
        }
        return null;
    }

    protected boolean isNonnull(ExecutableElement executableElement) {
        return executableElement.getAnnotation(Nonnull.class) != null;
    }

    protected boolean isNullable(ExecutableElement executableElement) {
        return executableElement.getAnnotation(Nullable.class) != null;
    }

    /**
     * the JNI library name for the FFI type.
     * @see System#loadLibrary(String)
     * @param typeElement
     * @return
     */
    protected String getLibraryName(TypeElement typeElement) {
        FFIGen gen = typeElement.getAnnotation(FFIGen.class);
        if (gen != null && !gen.library().isEmpty()) {
            return gen.library();
        }
        if (isFFIBuiltinType(typeElement.asType())) {
            return null; // builtin type has no loadLibrary
        }
        PackageElement packageElement = getPackageElement(typeElement);
        if (packageElement != null) {
            while (true) {
                FFIApplication ffiApplication = packageElement.getAnnotation(FFIApplication.class);
                if (ffiApplication != null && !ffiApplication.jniLibrary().isEmpty()) {
                    return ffiApplication.jniLibrary();
                }
                String packageName = packageElement.getQualifiedName().toString();
                int index = packageName.lastIndexOf('.');
                if (index == -1) {
                    break;
                }
                packageElement = elementUtils().getPackageElement(packageName.substring(0, index));
                if (packageElement == null) { // JDK 11: package name may be null
                    break;
                }
            }
        }
        return null;
    }

    public TypeMapping[] getTypeParameterTypeMapping(CXXTemplate template, ExecutableElement executableElement) {
        return getTypeParameterTypeMapping(template, (TypeElement) executableElement.getEnclosingElement());
    }

    public TypeMapping[] getTypeParameterTypeMapping(CXXTemplate template, TypeElement context) {
        if (template == null) {
            return new TypeMapping[0];
        }
        String[] cxx = template.cxx();
        String[] java = template.java();
        TypeMapping[] results = new TypeMapping[cxx.length];
        for (int i = 0; i < cxx.length; i++) {
            TypeMapping typeMapping = new TypeMapping(cxx[i],
                    typeNameToDeclaredType(processingEnv, java[i], context));
            results[i] = typeMapping;
        }
        return results;
    }

    protected boolean isHiddenOrOverridenByElement(Map<Name, List<ExecutableElement>> nameToExecutables, ExecutableElement ee) {
        Name name = ee.getSimpleName();
        List<ExecutableElement> executableDefs = nameToExecutables.get(name);
        if (executableDefs == null) {
            return false;
        }
        Elements elementUtils = processingEnv.getElementUtils();
        for (ExecutableElement existing : executableDefs) {
            if (elementUtils.hides(existing, ee)) {
                return true;
            }
            if (elementUtils.overrides(existing, ee, (TypeElement) existing.getEnclosingElement())) {
                return true;
            }
        }
        return false;
    }

    protected boolean isHiddenOrOverriden(Map<Name, List<ExecutableDef>> nameToExecutables, ExecutableElement ee) {
        Name name = ee.getSimpleName();
        List<ExecutableDef> executableDefs = nameToExecutables.get(name);
        if (executableDefs == null) {
            return false;
        }
        Elements elementUtils = processingEnv.getElementUtils();
        for (ExecutableDef def : executableDefs) {
            ExecutableElement existing = def.getExecutableElement();
            if (elementUtils.hides(existing, ee)) {
                return true;
            }
            if (elementUtils.overrides(existing, ee,def.getEnclosingElement())) {
                return true;
            }
            if (typeUtils().isSameType(ee.asType(), existing.asType())) {
                return true;
            }
        }
        return false;
    }
}
