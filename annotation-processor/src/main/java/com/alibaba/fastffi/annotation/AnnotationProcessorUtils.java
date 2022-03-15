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

import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.FFIFunGen;
import com.alibaba.fastffi.FFIGen;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class AnnotationProcessorUtils {

    public static String format(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString();
    }

    public static String format(ExecutableElement executableElement) {
        return String.format("%s %s::%s", executableElement.getReturnType(), executableElement.getEnclosingElement(), executableElement);
    }

    public static String format(ExecutableElement executableElement, ExecutableType executableType) {
        return String.format("%s %s::%s", executableType.getReturnType(), executableElement.getEnclosingElement(), executableElement.getSimpleName(),
                executableType.getParameterTypes().stream().map(TypeMirror::toString).collect(Collectors.joining(", ")));
    }

    public static String extractPackageName(Element element) {
        if (element instanceof PackageElement) {
            PackageElement packageElement = (PackageElement) element;
            return packageElement.getQualifiedName().toString();
        }
        if (element instanceof TypeElement) {
            if (element.getEnclosingElement() != null) {
                return extractPackageName(element.getEnclosingElement());
            }
            return "";
        }
        throw new IllegalStateException("Should not reach here!" + element);
    }

    /**
     *
     * @param header
     * @return "foo/bar/test.h" -> FOO_BAR_TEST_H
     */
    public static String toHeaderGuard(String header) {
        StringBuilder sb = new StringBuilder();
        sb.append("_");
        for (int i = 0; i < header.length(); i++) {
            char c = header.charAt(i);
            if (Character.isAlphabetic(c)) {
                sb.append(Character.toUpperCase(c));
            } else if (Character.isDigit(c)) {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    /**
     * Get the package element of the given type element.
     * @param typeElement
     * @return
     */
    public static PackageElement getPackageElement(TypeElement typeElement) {
        if (typeElement == null) {
            return null;
        }
        Element element = typeElement.getEnclosingElement();
        do {
            if (element instanceof PackageElement) {
                return (PackageElement) element;
            }
            if (element == null) {
                return null;
            }
            element = element.getEnclosingElement();
        } while (true);
    }

    /**
     *
     * @param processingEnv
     * @param typeName
     * @param context
     * @return
     */
    static DeclaredType getDeclaredTypeInContext(ProcessingEnvironment processingEnv, String typeName, TypeElement context) {
        Elements elementUtils = processingEnv.getElementUtils();
        TypeElement typeElement = elementUtils.getTypeElement(typeName);
        if (typeElement == null && context != null) {
            // 1. try to resolve the type in the package of the given context
            PackageElement packageElement = getPackageElement(context);
            if (packageElement != null) {
                typeElement = elementUtils.getTypeElement(packageElement.getQualifiedName().toString() + "." + typeName);
            }
        }
        if (typeElement == null) {
            // 2. try java.lang
            typeElement = elementUtils.getTypeElement("java.lang." + typeName);
        }
        if (typeElement == null) {
            throw new IllegalArgumentException("Cannot find type '" + typeName + "' in the context of " +
                    (context == null ? context : format(context)));
        }
        TypeMirror typeMirror = typeElement.asType();
        if (!isDeclaredType(typeMirror)) {
            throw new IllegalArgumentException("'" + typeName + "' is not a DeclaredType.");
        }
        return (DeclaredType) typeMirror;
    }

    public static DeclaredType typeNameToDeclaredType(ProcessingEnvironment processingEnv, String typeName, ExecutableElement context) {
        if (context == null) {
            throw new IllegalArgumentException("Context should not be null");
        }
        return typeNameToDeclaredType(processingEnv, typeName, (TypeElement) context.getEnclosingElement());
    }

    public static TypeElement getTypeElement(ProcessingEnvironment processingEnv, String name) {
        TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(name);
        if (typeElement == null) {
            throw new FFIIllegalStateException("Cannot find type element via '" + name + "'", typeElement);
        }
        return typeElement;
    }

    /**
     * Java canonical name nested by `<>' and separated by `,'
     * @param typeName: must be composed of declared types. No primitive types and array types are allowed.
     * @return
     */
    public static DeclaredType typeNameToDeclaredType(ProcessingEnvironment processingEnv, String typeName, TypeElement context) {
        if (typeName.indexOf('>') == -1) {
            return getDeclaredTypeInContext(processingEnv, typeName, context);
        }
        Types typeUtils = processingEnv.getTypeUtils();
        int begin = 0;
        int i = 0;
        final int length = typeName.length();
        Stack<List<DeclaredType>> typeMirrorStack = new Stack<>();
        List<DeclaredType> currentList = new ArrayList<>();
        while (i < length) {
            char c = typeName.charAt(i);
            if (c == '<' || c == ',' || c == '>') {
                String part = typeName.substring(begin, i).trim();
                begin = i + 1;
                if (c == '<') {
                    DeclaredType declaredType = getDeclaredTypeInContext(processingEnv, part, context);
                    currentList.add(declaredType);
                    typeMirrorStack.push(currentList);
                    currentList = new ArrayList<>();
                } else if (c == '>') {
                    if (!part.isEmpty()) {
                        DeclaredType declaredType = getDeclaredTypeInContext(processingEnv, part, context);
                        currentList.add(declaredType);
                    } // else {} already set
                    if (typeMirrorStack.isEmpty()) {
                        throw new IllegalStateException("Mis-matching <> in type name '" + typeName + "'");
                    }
                    List<DeclaredType> lastList = typeMirrorStack.pop();
                    if (lastList.isEmpty()) {
                        throw new IllegalStateException();
                    }
                    int lastIndex = lastList.size() - 1;
                    DeclaredType last = lastList.get(lastIndex);
                    List<? extends TypeMirror> typeArguments = last.getTypeArguments();
                    if (typeArguments.size() != currentList.size()) {
                        throw new IllegalStateException("Cannot parse '" + typeName + "', ");
                    }
                    if (last.getEnclosingType().getKind() != TypeKind.NONE) {
                        throw new IllegalArgumentException("Nested types are not supported: '" + typeName + "' has enclosing type " + last.getEnclosingType());
                    }
                    last = typeUtils.getDeclaredType((TypeElement) last.asElement(), currentList.stream().toArray(TypeMirror[]::new));
                    lastList.set(lastIndex, last);
                    currentList = lastList;
                } else { // must be ','
                    DeclaredType declaredType = getDeclaredTypeInContext(processingEnv, part, context);
                    currentList.add(declaredType);
                }
            }
            i++;
        }
        if (!typeMirrorStack.isEmpty()) {
            throw new IllegalStateException();
        }
        if (currentList.size() != 1) {
            throw new IllegalArgumentException("Current list is not singleton: '" + currentList + "' for '" + typeName + "'");
        }
        return currentList.get(0);
    }

    public static boolean isTypeVariable(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.TYPEVAR && typeMirror instanceof TypeVariable;
    }

    public static boolean isDeclaredType(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.DECLARED && typeMirror instanceof DeclaredType;
    }

    public static boolean isArrayType(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.ARRAY && typeMirror instanceof ArrayType;
    }

    public static boolean isPrimitiveType(TypeMirror typeMirror) {
        if (!(typeMirror instanceof PrimitiveType)) {
            return false;
        }
        switch (typeMirror.getKind()) {
            case BYTE:
            case BOOLEAN:
            case SHORT:
            case CHAR:
            case INT:
            case FLOAT:
            case LONG:
            case DOUBLE:
                return true;
            default:
                return false;
        }
    }

    static boolean getBoolean(Map<String, String> options, String key, boolean defaultValue) {
        String value = options.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    static String getString(Map<String, String> options, String key, String defaultValue) {
        String value = options.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    static JavaFileManager.Location getLocation(Map<String, String> options, String key, JavaFileManager.Location defaultValue) {
        String value = options.get(key);
        if (value == null) {
            return defaultValue;
        }
        return StandardLocation.locationFor(value);
    }

    /**
     * Convert a TypeMirror into a string
     * @param typeMirror
     * @return
     */
    public static String typeToTypeName(TypeMirror typeMirror) {
        if (isDeclaredType(typeMirror)) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            String baseName = ((TypeElement) declaredType.asElement()).getQualifiedName().toString();
            if (typeArguments.isEmpty()) {
                return baseName;
            }
            return baseName
                    + "<" +
                    typeArguments.stream().map(AnnotationProcessorUtils::typeToTypeName).collect(Collectors.joining(","))
                    + ">";
        }
        if (isArrayType(typeMirror)) {
            throw new IllegalArgumentException("Unsupported type mirror: " + typeMirror);
        }
        if (isPrimitiveType(typeMirror)) {
            switch (typeMirror.getKind()) {
                case BYTE:
                    return "byte";
                case BOOLEAN:
                    return "boolean";
                case SHORT:
                    return "short";
                case CHAR:
                    return "char";
                case INT:
                    return "int";
                case FLOAT:
                    return "float";
                case LONG:
                    return "long";
                case DOUBLE:
                    return "double";
                default:
                    throw new IllegalArgumentException("Unsupported type mirror: " + typeMirror);
            }
        }
        if (typeMirror.getKind() == TypeKind.VOID) {
            return "void";
        }
        if (isTypeVariable(typeMirror)) {
            return ((TypeVariable) typeMirror).asElement().getSimpleName().toString();
        }
        throw new IllegalArgumentException("Unsupported type mirror: " + typeMirror);
    }

    public static boolean isSameFFIGen(FFIGen gen1, FFIGen gen2) {
        if (!gen1.type().equals(gen2.type())) {
            return false;
        }
        if (!gen1.library().equals(gen2.library())) {
            return false;
        }
        if (!isSameCXXTemplates(gen1.templates(), gen2.templates())) {
            return false;
        }
        return isSameFFIFunGens(gen1.functionTemplates(), gen2.functionTemplates());
    }

    public static boolean isSameFFIFunGens(FFIFunGen[] templates1, FFIFunGen[] templates2) {
        if (templates1.length != templates2.length) {
            return false;
        }
        for (int i = 0; i < templates1.length; i++) {
            if (!isSameFFIFunGen(templates1[i], templates2[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSameFFIFunGen(FFIFunGen gen1, FFIFunGen gen2) {
        if (!gen1.name().equals(gen2.name())) {
            return false;
        }
        if (!gen1.returnType().equals(gen2.returnType())) {
            return false;
        }
        if (!Arrays.equals(gen1.parameterTypes(), gen2.parameterTypes())) {
            return false;
        }
        return isSameCXXTemplates(gen1.templates(), gen2.templates());
    }

    public static boolean isSameCXXTemplates(CXXTemplate[] templates1, CXXTemplate[] templates2) {
        if (templates1.length != templates2.length) {
            return false;
        }
        for (int i = 0; i < templates1.length; i++) {
            if (!isSameCXXTemplate(templates1[i], templates2[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSameCXXTemplate(CXXTemplate template1, CXXTemplate template2) {
        if (!template1.cxxFull().equals(template2.cxxFull())) {
            return false;
        }
        if (!Arrays.equals(template1.java(), template2.java())) {
            return false;
        }
        return Arrays.equals(template1.cxx(), template2.cxx());
    }
}
