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

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.FFIFunGen;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFIMirror;
import com.alibaba.fastffi.FFINameSpace;
import com.alibaba.fastffi.FFISynthetic;
import com.alibaba.fastffi.FFITypeAlias;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.Arrays;

import static com.alibaba.fastffi.annotation.AnnotationProcessorUtils.extractPackageName;
import static com.alibaba.fastffi.annotation.AnnotationProcessorUtils.typeNameToDeclaredType;

public class TypeDef {

    /**
     * This may be a generated type element, e.g., due to function
     * template expansion.
     */
    private final String typeElementName;
    /**
     * See FFIGen.type()
     */
    private final String declaredTypeElementName; // the original type element used.

    /**
     * If template is not null, this TypeDef must represent a parameterized type.
     */
    private final CXXTemplate template;
    private String cxxFullTypeName;
    private final String packageName;
    private final String simpleClassName;
    private String fullClassName;

    private final FFIGen ffiGen;
    private final FFIFunGen[] ffiFunGens;
    private final FFITypeAlias ffiTypeAlias;
    private final FFINameSpace ffiNamespace;
    private final FFIMirror ffiMirror;
    private final FFILibrary ffiLibrary;

    public TypeDef(TypeElement theTypeElement, FFIGen ffiGen, CXXTemplate template) {
        this(theTypeElement, ffiGen, template, theTypeElement.getAnnotation(FFITypeAlias.class),
                theTypeElement.getAnnotation(FFINameSpace.class),
                theTypeElement.getAnnotation(FFILibrary.class),
                theTypeElement.getAnnotation(FFIMirror.class));
    }

    public TypeDef(TypeElement theTypeElement, FFIGen ffiGen, CXXTemplate template, FFITypeAlias ffiTypeAlias,
                        FFINameSpace ffiNamespace, FFILibrary ffiLibrary, FFIMirror ffiMirror) {
        this.typeElementName = theTypeElement.getQualifiedName().toString();
        this.declaredTypeElementName = getDeclaredTypeElementName(theTypeElement);
        this.ffiGen = ffiGen;
        this.packageName = extractPackageName(theTypeElement);
        this.ffiFunGens = ffiGen.functionTemplates();
        this.ffiLibrary = ffiLibrary;
        this.ffiMirror = ffiMirror;
        this.ffiTypeAlias = ffiTypeAlias;
        this.ffiNamespace = ffiNamespace;
        this.template = template;
        this.simpleClassName = getSimpleName(theTypeElement) + getSuffixForGeneratedName();
        this.fullClassName = getGeneratedJavaClassName();
    }

    private String getDeclaredTypeElementName(TypeElement theTypeElement) {
        FFISynthetic synthetic = theTypeElement.getAnnotation(FFISynthetic.class);
        if (synthetic != null) {
            return synthetic.value();
        }
        return theTypeElement.getQualifiedName().toString();
    }

    @Override
    public String toString() {
        return "TypeDef{" +
                "typeElementName='" + typeElementName + '\'' +
                ", declaredTypeElementName='" + declaredTypeElementName + '\'' +
                ", template=" + template +
                ", cxxFullTypeName='" + cxxFullTypeName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", simpleClassName='" + simpleClassName + '\'' +
                ", fullClassName='" + fullClassName + '\'' +
                ", ffiGen=" + ffiGen +
                ", ffiFunGens=" + Arrays.toString(ffiFunGens) +
                ", ffiTypeAlias=" + ffiTypeAlias +
                ", ffiNamespace=" + ffiNamespace +
                ", ffiMirror=" + ffiMirror +
                ", ffiLibrary=" + ffiLibrary +
                '}';
    }

    public TypeElement getTypeElement(ProcessingEnvironment processingEnvironment) {
        return processingEnvironment.getElementUtils().getTypeElement(typeElementName);
    }

    public DeclaredType getTypeMirror(ProcessingEnvironment processingEnvironment) {
        return getTypeMirror(processingEnvironment, getTypeElement(processingEnvironment));
    }

    public DeclaredType getTypeMirror(ProcessingEnvironment processingEnv, TypeElement typeElement) {
        if (!typeElement.getQualifiedName().toString().equals(typeElementName)) {
            throw new IllegalArgumentException("Expected " + typeElementName + ", got " + typeElement);
        }
        if (template == null) {
            return (DeclaredType) typeElement.asType();
        }
        String[] javaTypeNames = template.java();
        DeclaredType[] javaTypes = new DeclaredType[javaTypeNames.length];
        for (int i = 0; i < javaTypeNames.length; i++) {
            javaTypes[i] = typeNameToDeclaredType(processingEnv, javaTypeNames[i], typeElement);
        }
        return processingEnv.getTypeUtils().getDeclaredType(typeElement, javaTypes);
    }

    public boolean isFFILibrary() {
        return ffiLibrary != null;
    }

    /**
     * An FFIMirror must be an FFIPointer.
     * @return
     */
    public boolean isFFIMirror() {
        return ffiMirror != null;
    }

    public CXXHead[] getAdditionalInclude() {
        if (template == null) {
            return new CXXHead[0];
        }
        return template.include();
    }

    public String getCxxRawTypeName() {
        if (ffiTypeAlias != null) {
            if (ffiNamespace != null) {
                return ffiNamespace.value() + "::" + ffiTypeAlias.value();
            }
            return ffiTypeAlias.value();
        }
        throw new IllegalStateException("Cannot call getCXXRawTypeName on a TypeDef that is not a FFIPointer");
    }

    public String getCxxRawSimpleTypeName() {
        if (ffiTypeAlias != null) {
            return ffiTypeAlias.value();
        }
        throw new IllegalStateException("Cannot call getCXXRawTypeName on a TypeDef that is not a FFIPointer");
    }

    /**
     * Type Registry ID is the cxx full type name for FFIPointer
     * and the FFILibrary ID for FFILibrary.
     * @return
     */
    public String getTypeRegistryId() {
        if (ffiLibrary != null) {
            String id = ffiLibrary.value();
            if (!id.isEmpty()) {
                return id;
            }
            id = ffiLibrary.namespace();
            if (!id.isEmpty()) {
                return id;
            }
            throw new IllegalStateException("An FFILibrary must have a valid type registry id");
        }
        return getCxxFullTypeName();
    }

    public String getCxxFullTypeName() {
        if (isFFILibrary()) {
            throw new IllegalStateException("An FFILibrary does not have a type name");
        }
        if (cxxFullTypeName == null) {
            cxxFullTypeName = computeCxxFullName();
        }
        return cxxFullTypeName;
    }

    public String getFFILibraryNamespace() {
        if (ffiLibrary == null) {
            throw new IllegalStateException("Cannot call getFFILibraryNamespace on a TypeDef that is not a FFILibrary");
        }
        return ffiLibrary.namespace();
    }

    private String computeCxxFullName() {
        if (template == null) {
            return getCxxRawTypeName();
        }
        if (!template.cxxFull().isEmpty()) {
            return getCxxRawTypeName() + "<" + template.cxxFull() + ">";
        }
        return getCxxRawTypeName() + "<" + String.join(",", template.cxx()) + ">";
    }

    public String getSuffixForGeneratedName() {
        return String.format("_cxx_0x%x", getTypeRegistryId().hashCode()); // JavaUtils.encode(getCxxFullTypeName());
    }

    public String getGeneratedJavaClassName() {
        if (fullClassName == null) {
            String packageName = getPackageName();
            if (packageName.isEmpty()) {
                fullClassName = getGeneratedJavaSimpleClassName();
            } else {
                fullClassName = getPackageName() + "." + getGeneratedJavaSimpleClassName();
            }
        }
        return fullClassName;
    }

    private String getSimpleName(Element element) {
        Element enclosing = element.getEnclosingElement();
        if (enclosing instanceof TypeElement) {
            return getSimpleName(enclosing) + "_" + element.getSimpleName();
        }
        return element.getSimpleName().toString();
    }

    public final String getGeneratedJavaSimpleClassName() {
        return simpleClassName;
    }

    public static boolean isSafeJavaName(String name) {
        return !name.contains("_cxx_");
    }

    public String getPackageName() {
        return packageName;
    }

    public String getTypeElementName() {
        return typeElementName;
    }

    public String getDeclaredTypeElementName() {
        return declaredTypeElementName;
    }

    public FFIFunGen[] getFFIFunctionTemplates() {
        return ffiFunGens;
    }

    public FFIGen getFFIGen() {
        return ffiGen;
    }

    public CXXTemplate getCXXTemplate() {
        return template;
    }
}
