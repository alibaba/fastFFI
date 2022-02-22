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
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointerRangeElement;
import com.alibaba.fastffi.CXXSuperTemplate;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIConst;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIForeignType;
import com.alibaba.fastffi.FFIFunGen;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFIIntrinsic;
import com.alibaba.fastffi.FFIJava;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFIMirrorDefinition;
import com.alibaba.fastffi.FFIMirrorFieldDefinition;
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFINameSpace;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFIPointerImpl;
import com.alibaba.fastffi.FFIProperty;
import com.alibaba.fastffi.FFISettablePointer;
import com.alibaba.fastffi.FFISetter;
import com.alibaba.fastffi.FFISynthetic;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.alibaba.fastffi.FFIUtils.addToMapList;
import static com.alibaba.fastffi.FFIUtils.encodeNativeMethodName;

public class TypeDefGenerator extends TypeEnv {

    enum Status {
        CREATED,
        GENERATED,
        FAILED
    }

    private TypeElement theTypeElement;
    private DeclaredType theTypeMirror;
    private final TypeDef typeDef;

    // for reportError
    private ExecutableElement theExecutableElement;
    private ExecutableType theExecutableType;

    private final CXXTemplate template;

    // used to keep order they are identified from element api
    private final List<ExecutableDef> generationList = new ArrayList<>();
    // used to detect overloading
    private final Map<Name, List<ExecutableDef>> nameToExecutables = new HashMap<>();
    private final Map<Name, DeclaredType> nameToSupertype = new HashMap<>();

    private Map<ExecutableElement, ExecutableTypeMapping> executableElementExecutableTypeMappingMap;
    private TypeSpec.Builder classBuilder;
    Map<String, TypeMapping> topLevelNameToMapping;

    // Now use a simple string builder to generate CXX code
    private final StringBuilder cxxWriter = new StringBuilder();
    private final StringBuilder hxxWriter = new StringBuilder();
    private boolean writeCxxFile = false;
    private boolean hasFactory;

    private final TypeGenType genType;
    private Status status;

    public TypeDefGenerator(TypeDefRegistry registry, ProcessingEnvironment processingEnv, TypeElement theTypeElement, TypeDef def, CXXTemplate template) {
        super(registry, processingEnv);
        this.template = template;
        this.typeDef = def;
        this.genType = probeGenType(theTypeElement);
        this.status = Status.CREATED;
        this.theTypeElement = theTypeElement;
    }

    public String toString() {
        return typeDef.toString();
    }

    public TypeDef getTypeDef() {
        return typeDef;
    }

    public boolean isCreated() {
        return this.status == Status.CREATED;
    }

    public boolean isGenFFITemplate() {
        return this.genType == TypeGenType.FFITemplate;
    }

    public boolean isGenFFIPointer() {
        return this.genType == TypeGenType.FFIPointer;
    }

    public boolean isGenFFILibrary() {
        return this.genType == TypeGenType.FFILibrary;
    }

    private int checkOverloading(CXXTemplate[] templates, ExecutableElement executableElement) {
        if (templates == null || templates.length == 0) {
            throw new IllegalStateException("Templates must not be empty.");
        }
        List<? extends VariableElement> parameters = executableElement.getParameters();
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            VariableElement param = parameters.get(i);
            TypeMirror paramType = param.asType();
            if (AnnotationProcessorUtils.isTypeVariable(paramType) && getTypeVariableIndex(executableElement, paramType) != -1) {
                // usually we can only do valid overloading via substituting simple type variable.
                TypeVariable variable = (TypeVariable) paramType;
                String variableName = variable.asElement().getSimpleName().toString();
                if (checkOverloading(variableName, templates, executableElement)) {
                    return i;
                }
            }
        }
        return -1;
   }

    private boolean checkOverloading(String variableName, CXXTemplate[] templates, ExecutableElement executableElement) {
        List<? extends TypeParameterElement> methodTypeParameters = executableElement.getTypeParameters();
        for (int i = 0; i < methodTypeParameters.size(); i++) {
            TypeParameterElement methodTypeParameter = methodTypeParameters.get(i);
            if (methodTypeParameter.getSimpleName().toString().equals(variableName)) {
                if (checkOverloading(i, templates, executableElement)) {
                    return true;
                }
            }
        }
        return false;
    }

    private RuntimeException reportError(String format, Object... args) {
        return reportError(theExecutableElement, theExecutableType, format, args);
    }

    private RuntimeException reportError(ExecutableElement executableElement, String format, Object ...args) {
        return reportError(executableElement, null, format, args);
    }

    private RuntimeException reportError(ExecutableElement executableElement, ExecutableType executableType, String format, Object ...args) {
        return reportError(typeDef, executableElement, executableType, format, args);
    }

    private RuntimeException reportError(TypeDef typeDef, ExecutableElement executableElement, ExecutableType executableType, String format, Object ...args) {
        FFICodeGenerationException exception = new FFICodeGenerationException(String.format(format, args));
        exception.typeDef = typeDef;
        exception.executableElement = executableElement;
        exception.executableType = executableType;
        return exception;
    }

    protected void assertAtMostOneNonNull(Object... args) {
        int nonNull = 0;
        for (Object obj : args) {
            nonNull += obj == null ? 0 : 1;
        }
        if (nonNull > 1) {
            throw reportError("At most one non-null in " + Arrays.asList(args));
        }
    }

    private boolean checkOverloading(int argIndex, CXXTemplate[] templates, ExecutableElement executableElement) {
        Set<String> typeNames = new HashSet<>();
        for (CXXTemplate template : templates) {
            if (argIndex >= template.cxx().length) {
                throw reportError(executableElement, "Missing cxx type in " + template + ", require " + (argIndex + 1) + ", got " + template.cxx().length);
            }
            if (argIndex >= template.java().length) {
                throw reportError(executableElement, "Missing java type in " + template + ", require " + (argIndex + 1) + ", got " + template.java().length);
            }
            DeclaredType javaType = AnnotationProcessorUtils.typeNameToDeclaredType(processingEnv, template.java()[argIndex], executableElement);
            javaType = (DeclaredType) typeUtils().erasure(javaType);
            if (typeNames.add(((TypeElement) typeUtils().asElement(javaType)).getQualifiedName().toString())) {
                continue;
            }
            return false;
        }
        return true;
    }

    int getTypeVariableIndex(ExecutableElement executableElement, TypeMirror type) {
        if (!AnnotationProcessorUtils.isTypeVariable(type)) {
            throw reportError(executableElement, "Expected type variable, got " + type);
        }
        List<? extends TypeParameterElement> typeParameters = executableElement.getTypeParameters();
        for (int i = 0; i < typeParameters.size(); i++) {
            if (typeParameters.get(i).getSimpleName().toString().equals(type.toString())) {
                return i;
            }
        }
        return -1;
    }

    public void generate() {
        try {
            if (isGenFFITemplate()) {
                generateFFITemplate();
            } else {
                generateFFIPointerOrFFILibrary();
            }
            status = Status.GENERATED;
        } catch (FFICodeGenerationException e) {
            if (e.typeDef == null) {
                e.typeDef = getTypeDef();
            }
            e.printStackTrace();
            throw e;
        } catch (RuntimeException e) {
            status = Status.FAILED;
            e.printStackTrace();
            throw new FFICodeGenerationException(e.getMessage(), e, getTypeDef());
        }
    }

    private void generateFFITemplate() {
        if (!isGenFFITemplate()) {
            throw new IllegalStateException();
        }

        note("Generate FFITempalte for %s", typeDef);

        theTypeElement = typeDef.getTypeElement(processingEnv);

        String className = this.theTypeElement.getSimpleName().toString() + "Gen";

        // Add current type as the super interface
        this.classBuilder = TypeSpec.interfaceBuilder(className).addSuperinterface(theTypeElement.asType());
        for (TypeMirror interfaceMirror : theTypeElement.getInterfaces()) {
            // add Generated interface as the super interface
            TypeElement interfaceElement = (TypeElement) typeUtils().asElement(interfaceMirror);
            if (isJavaClass((DeclaredType) interfaceElement.asType(), FFIPointer.class)) {
                continue;
            }
            if (isFFIPointerElement(interfaceElement) || isFFILibraryElement(interfaceElement)) {
                TypeGenType typeGenType = probeGenType(interfaceElement);
                if (typeGenType == TypeGenType.FFITemplate) {
                    FFIGen ffiGen = interfaceElement.getAnnotation(FFIGen.class);
                    if (ffiGen == null) {
                        throw reportError("Missing @FFIGen in " + AnnotationProcessorUtils.format(interfaceElement));
                    }
                    TypeName oldTypeName = TypeName.get(interfaceMirror);
                    TypeName genName;
                    if (oldTypeName instanceof ClassName) {
                        ClassName oldClassName = (ClassName) oldTypeName;
                        genName = ClassName.get(oldClassName.packageName(), oldClassName.simpleName() + "Gen");
                    } else if (oldTypeName instanceof ParameterizedTypeName) {
                        ParameterizedTypeName oldParameterizedName = (ParameterizedTypeName) oldTypeName;
                        ClassName oldClassName = oldParameterizedName.rawType;
                        genName = ParameterizedTypeName.get(ClassName.get(oldClassName.packageName(), oldClassName.simpleName() + "Gen"),
                                oldParameterizedName.typeArguments.toArray(new TypeName[0]));
                    } else {
                        throw new IllegalStateException("Should not reach here");
                    }
                    classBuilder.addSuperinterface(genName);
                }
            }
        }

        // add type variable, modifiers and annotations
        classBuilder.addModifiers(theTypeElement.getModifiers().toArray(new Modifier[0]));
        classBuilder.addTypeVariables(theTypeElement.getTypeParameters().stream().map(TypeVariableName::get).collect(Collectors.toList()));
        classBuilder.addAnnotations(theTypeElement.getAnnotationMirrors().stream()
                                                  .filter( a -> !isJavaClass(a.getAnnotationType(), FFIGen.class))
                                                  .map(AnnotationSpec::get).collect(Collectors.toList()));

        FFIGen ffiGen = typeDef.getFFIGen();
        classBuilder.addAnnotation(toAnnotationSpec(ffiGen));

        Set<String> enclosingTypeVariables = getTypeVariableNames(theTypeElement);

        for (ExecutableElement executableElement : collectExecutableElements(theTypeElement)) {
            if (!executableElement.getEnclosingElement().equals(theTypeElement)) {
                // the generated method is in the generated interface of the super interface
                continue;
            }
            if (executableElement.isDefault()) {
                continue;
            }
            if (executableElement.getTypeParameters().isEmpty()) {
                continue;
            }
            CXXTemplate[] cxxTemplates = executableElement.getAnnotationsByType(CXXTemplate.class);
            CXXTemplate[] additionalTemplates = getAdditionalFunctionTemplates(executableElement);
            cxxTemplates = merge(cxxTemplates, additionalTemplates);
            if (cxxTemplates == null) {
                throw reportError(executableElement, "A generic method " + AnnotationProcessorUtils.format(executableElement) + " must have CXXTemplate");
            }

            // generate methods of the generic method
            generateGenericMethod(className, executableElement, cxxTemplates, enclosingTypeVariables);
        }


        classBuilder.addAnnotation(AnnotationSpec.builder(FFISynthetic.class).addMember(
                "value", "$S", theTypeElement.getQualifiedName().toString()
        ).build());
        writeTypeSpec(classBuilder.build());
    }

    void generateGenericMethod(String className, ExecutableElement executableElement, CXXTemplate[] cxxTemplates, Set<String> enclosingTypeVariables) {
        // Append additional annotation to the expanded template
        for (CXXTemplate cxxTemplate : cxxTemplates) {
            // Add CXXHead
            for (CXXHead cxxHead : cxxTemplate.include()) {
                classBuilder.addAnnotation(AnnotationSpec.get(cxxHead));
            }
        }

        {
            MethodSpec.Builder builder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString());
            builder.returns(TypeName.get(executableElement.getReturnType()));
            executableElement.getParameters().forEach(p -> {
                ParameterSpec.Builder paramBuilder = ParameterSpec.builder(TypeName.get(p.asType()), p.getSimpleName().toString(),
                        p.getModifiers()
                                .toArray(new Modifier[0]));
                paramBuilder.addAnnotations(p.getAnnotationMirrors().stream().map(AnnotationSpec::get).collect(Collectors.toList()));
                builder.addParameter(paramBuilder.build());
            });
            builder.addModifiers(executableElement.getModifiers().stream().filter(m ->
                    (m != Modifier.ABSTRACT && m != Modifier.NATIVE)).toArray(Modifier[]::new));
            builder.addModifiers(Modifier.DEFAULT);
            builder.addTypeVariables(executableElement.getTypeParameters().stream().map(TypeVariableName::get).collect(Collectors.toList()));
            builder.addAnnotations(executableElement.getAnnotationMirrors().stream().map(AnnotationSpec::get).collect(Collectors.toList()));
            if (cxxTemplates.length > 0) {
                int nontrivialParamIndex = checkOverloading(cxxTemplates, executableElement);
                if (nontrivialParamIndex == -1) {
                    throw reportError(executableElement, "Use @FFISkip to support overloading");
                }
                VariableElement param = executableElement.getParameters().get(nontrivialParamIndex);
                builder.beginControlFlow("if ($L == null)", param.getSimpleName())
                        .addStatement("throw new NullPointerException(\"$L must not be null\")", param.getSimpleName())
                        .endControlFlow();
                int typeVariableIndex = getTypeVariableIndex(executableElement, param.asType());
                if (typeVariableIndex == -1) {
                    throw reportError(executableElement, "Oops: cannot find a type variable for parameter at index " + nontrivialParamIndex);
                }
                for (CXXTemplate cxxTemplate : cxxTemplates) {
                    String[] javaTypeNames = cxxTemplate.java();
                    DeclaredType javaType = erasure(AnnotationProcessorUtils.typeNameToDeclaredType(processingEnv, javaTypeNames[typeVariableIndex], executableElement));
                    String invokeStmt = getOverloadedCallStatement(cxxTemplate, executableElement);
                    builder.beginControlFlow("if ($L instanceof $T) ", param.getSimpleName(), javaType)
                            .addStatement(invokeStmt)
                            .endControlFlow();
                }
            }
            builder.addStatement("throw new RuntimeException(\"Cannot call $L.$L, no template instantiation for the type arguments.\")",
                    className, executableElement.getSimpleName().toString());
            classBuilder.addMethod(builder.build());
        }

        // Generate overloaded method for each CXXTemplate
        for (CXXTemplate cxxTemplate : cxxTemplates) {
            List<? extends TypeParameterElement> typeParameterElements = executableElement.getTypeParameters();
            Map<String, TypeMapping> nameToDeclaredType = createTopLevelTypeMapping(cxxTemplate, typeParameterElements, (TypeElement) executableElement.getEnclosingElement());
            {
                String methodName = executableElement.getSimpleName().toString();
                MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName);

                // See collectForeign
                {
                    TypeMirror originalReturnType = executableElement.getReturnType();
                    if (AnnotationProcessorUtils.isTypeVariable(originalReturnType) || requireCreatingTypeMapping(originalReturnType)) {
                        TypeMapping returnTypeMapping;
                        // TODO: do we support namespace in method?
                        FFITypeAlias typeAlias = executableElement.getAnnotation(FFITypeAlias.class);
                        if (typeAlias != null) {
                            returnTypeMapping = createTypeMapping(nameToDeclaredType, typeAlias, originalReturnType, enclosingTypeVariables);
                        } else {
                            returnTypeMapping = createTypeMapping(nameToDeclaredType, originalReturnType, enclosingTypeVariables);
                            if (returnTypeMapping == null) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                                        "Cannot get type mapping for " + originalReturnType, executableElement);
                            }
                        }
                        if (returnTypeMapping != null) {
                            builder.addAnnotation(AnnotationSpec.builder(FFITypeAlias.class).addMember(
                                    "value", "$S", returnTypeMapping.cxx
                            ).build());
                            builder.returns(TypeName.get(returnTypeMapping.java));
                        } else {
                            builder.returns(TypeName.get(originalReturnType));
                        }
                    } else {
                        builder.returns(TypeName.get(originalReturnType));
                    }
                }

                executableElement.getParameters().forEach(p -> {
                    TypeMirror originalParamType = p.asType();
                    ParameterSpec.Builder paramBuilder;

                    if (AnnotationProcessorUtils.isTypeVariable(originalParamType) || requireCreatingTypeMapping(originalParamType)) {
                        TypeMapping paramTypeMapping;
                        FFITypeAlias typeAlias = p.getAnnotation(FFITypeAlias.class);
                        if (typeAlias != null) {
                            paramTypeMapping = createTypeMapping(nameToDeclaredType, typeAlias, originalParamType, enclosingTypeVariables);
                        } else {
                            paramTypeMapping = createTypeMapping(nameToDeclaredType, originalParamType, enclosingTypeVariables);
                        }
                        if (paramTypeMapping != null) {
                            paramBuilder = ParameterSpec.builder(TypeName.get(paramTypeMapping.java), p.getSimpleName().toString(),
                                    p.getModifiers().toArray(new Modifier[0]));
                            paramBuilder.addAnnotation(AnnotationSpec.builder(FFITypeAlias.class).addMember(
                                    "value", "$S", paramTypeMapping.cxx
                            ).build());
                        } else {
                            // paramType mapping could be null if it has type variables from super
                            paramBuilder = ParameterSpec.builder(TypeName.get(originalParamType), p.getSimpleName().toString(),
                                    p.getModifiers().toArray(new Modifier[0]));
                        }
                    } else {
                        paramBuilder = ParameterSpec.builder(TypeName.get(originalParamType), p.getSimpleName().toString(),
                                p.getModifiers().toArray(new Modifier[0]));
                    }

                    paramBuilder.addAnnotations(p.getAnnotationMirrors().stream().filter(a -> !isFFITypeAlias(a))
                            .map(AnnotationSpec::get).collect(Collectors.toList()));
                    builder.addParameter(paramBuilder.build());
                });
                builder.addModifiers(executableElement.getModifiers().toArray(new Modifier[0]));

                // Add all annotations exception CXXTemplate and CXXTemplates
                builder.addAnnotations(executableElement.getAnnotationMirrors().stream().filter(a ->
                        !isCXXTemplate(a) && !isCXXTemplates(a) && !isFFITypeAlias(a)
                ).map(AnnotationSpec::get).collect(Collectors.toList()));
                // Add current CXXTemplate
                addCXXTemplate(builder, cxxTemplate, executableElement);
                // Add FFINameAlias if needed. TOOD: add a name alias no matter whether we have added a suffix.
                FFINameAlias ffiNameAlias = executableElement.getAnnotation(FFINameAlias.class);
                if (ffiNameAlias == null) {
                    builder.addAnnotation(AnnotationSpec.builder(FFINameAlias.class).addMember(
                            "value", "$S", executableElement.getSimpleName().toString()
                    ).build());
                }
                classBuilder.addMethod(builder.build());
            }
        }
    }

    private AnnotationSpec toAnnotationSpec(FFIGen ffiGen) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(FFIGen.class);
        if (!ffiGen.type().isEmpty()) {
            builder.addMember("type", "$S", ffiGen.type());
        }
        if (!ffiGen.library().isEmpty()) {
            builder.addMember("library", "$S", ffiGen.library());
        }
        if (ffiGen.templates().length != 0) {
            CXXTemplate[] templates = ffiGen.templates();
            for (CXXTemplate template : templates) {
                builder.addMember("templates", "$L", toAnnotationSpec(template));
            }
        }
        if (ffiGen.functionTemplates().length != 0) {
            FFIFunGen[] gens = ffiGen.functionTemplates();
            for (FFIFunGen gen : gens) {
                builder.addMember("functionTemplates", "$L", toAnnotationSpec(gen));
            }
        }
        return builder.build();
    }

    private AnnotationSpec toAnnotationSpec(FFIFunGen ffiFunGen) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(FFIFunGen.class);
        if (!ffiFunGen.name().isEmpty()) {
            builder.addMember("name", "$S", ffiFunGen.name());
        }
        if (!ffiFunGen.returnType().isEmpty()) {
            builder.addMember("returnType", "$S", ffiFunGen.returnType());
        }
        for (String s : ffiFunGen.parameterTypes()) {
            builder.addMember("parameterTypes", "$S", s);
        }
        for (CXXTemplate template : ffiFunGen.templates()) {
            builder.addMember("templates", "$L", toAnnotationSpec(template));
        }
        return builder.build();
    }

    private AnnotationSpec toAnnotationSpec(CXXTemplate template) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(CXXTemplate.class);
        if (!template.cxxFull().isEmpty()) {
            builder.addMember("cxxFull", "$S", template.cxxFull());
        }
        String[] java = template.java();
        for (String s : java) {
            builder.addMember("java", "$S", s);
        }
        String[] cxx = template.cxx();
        for (String s : cxx) {
            builder.addMember("cxx", "$S", s);
        }
        CXXHead[] include = template.include();
        for (CXXHead h : include) {
            builder.addMember("include", "$L", toAnnotationSpec(h));
        }
        return builder.build();
    }

    private AnnotationSpec toAnnotationSpec(CXXHead head) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(CXXHead.class);
        String[] value = head.value();
        for (String s : value) {
            builder.addMember("value", "$S", s);
        }
        String[] system = head.system();
        for (String s : system) {
            builder.addMember("system", "$S", s);
        }
        return builder.build();
    }

    private String getTypeCast(TypeMirror typeMirror, Map<String, TypeMapping> nameToDeclaredType) {
        if (AnnotationProcessorUtils.isDeclaredType(typeMirror)) {
            return "(" + getTypeName(typeMirror) + ") ";
        } else if (AnnotationProcessorUtils.isTypeVariable(typeMirror)) {
            String typeName = getTypeName(typeMirror);
            TypeMapping typeMapping = nameToDeclaredType.get(typeName);
            if (typeMapping == null) {
                return "";
            }
            return "(" + typeMapping.java + ") ";
        }
        return "";
    }

    private String getOverloadedCallStatement(CXXTemplate template, ExecutableElement executableElement) {
        Map<String, TypeMapping> nameToDeclaredType = createTopLevelTypeMapping(template, executableElement.getTypeParameters(), (TypeElement) executableElement.getEnclosingElement());
        StringBuilder sb = new StringBuilder();
        TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            sb.append("return (").append(AnnotationProcessorUtils.typeToTypeName(erasure(returnType))).append(") ");
        }
        List<? extends VariableElement> parameters = executableElement.getParameters();
        sb.append(executableElement.getSimpleName()).append('(');
        sb.append(parameters.stream().map(param -> getTypeCast(param.asType(), nameToDeclaredType) + param.getSimpleName().toString())
                .collect(Collectors.joining(", ")));
        sb.append(')');
        if (returnType.getKind() == TypeKind.VOID) {
            sb.append("; return");
        }
        return sb.toString();
    }

    protected void addCXXTemplate(MethodSpec.Builder builder, CXXTemplate cxxTemplate, ExecutableElement executableElement) {
        List<? extends TypeParameterElement> typeParameterElements = executableElement.getTypeParameters();
        String[] cxx = cxxTemplate.cxx();
        String[] java = cxxTemplate.java();
        if (cxx.length != java.length || cxx.length != typeParameterElements.size()) {
            throw reportError(executableElement, "Malformed CXXTemplate: " + cxxTemplate);
        }
        AnnotationSpec.Builder annBuilder = AnnotationSpec.builder(CXXTemplate.class);
        for (int i = 0; i < cxx.length; i++) {
            if (skipTypeParameter(typeParameterElements.get(i))) {
                continue;
            }
            annBuilder.addMember("cxx", "$S", cxx[i]);
            annBuilder.addMember("java", "$S", java[i]);
        }
        String cxxFull = cxxTemplate.cxxFull();
        if (!cxxFull.isEmpty()) {
            annBuilder.addMember("cxxFull", "$S", cxxFull);
        }
        CXXHead[] heads = cxxTemplate.include();
        for (CXXHead head : heads) {
            annBuilder.addMember("include", "$L", AnnotationSpec.get(head));
        }
        builder.addAnnotation(annBuilder.build());
    }


    private void writeTypeSpec(TypeSpec typeSpec) {
        JavaFile javaFile = JavaFile.builder(getPackageName(), typeSpec).build();
        try {
            Filer filter = processingEnv.getFiler();
            javaFile.writeTo(filter);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write Java file " + javaFile.typeSpec.name + ". Please clean the build first.", e);
        }
    }

    private Map<String, TypeMapping> createTopLevelTypeMapping() {
        return createTopLevelTypeMapping(template, theTypeElement.getTypeParameters(), theTypeElement);
    }

    private void writeCXXHead(StringBuilder writer, String v) {
        writer.append("#include ").append(v).append("\n");
    }

    /**
     * All TypeElement marked with FFIGen in this project must be prepared all together
     * since we cannot distinguish whether a TypeElement with FFIGen from another project
     * needs to be generated except FFIBuiltinType.
     */
    protected void prepare() {
        theTypeElement = typeDef.getTypeElement(processingEnv);
        if (theTypeElement == null) {
            throw reportError("Cannot get the type element for " + typeDef.getTypeElementName());
        }
        theTypeMirror = typeDef.getTypeMirror(processingEnv, theTypeElement);
        if (isGenFFILibrary() || isGenFFIPointer()) {
            collectForeignForFFIPointerOrFFILibrary();
            collectMethods();
        }
    }

    /**
     * NOTE: Why we need another recursive visit to collect foreign types?
     * This is because we need to construct CXX templates types along with the Java generic types.
     * The annotation processor already provides TypeUtils.asMember for Java types.
     * But this method cannot construct CXX template.
     * Since TypeUtils.asMember is much more reliable than what we have implemented here,
     * we keep using it and re-implement a new recursive procedure in collectForeign.
     * In the future, we could use our implementation to replace the asMember version
     * if we fill confident about our code, which heavily relies on the understanding of
     * Java language specification.
     */
    private void collectForeignForFFIPointerOrFFILibrary() {
        if (!isGenFFIPointer() && !isGenFFILibrary()) {
            throw reportError("Must be FFIPointer or FFILibrary, got " + genType);
        }
        executableElementExecutableTypeMappingMap = collectForeign(theTypeElement, getTopLevelNameToMapping());
    }

    protected Map<String, TypeMapping> getTopLevelNameToMapping() {
        if (topLevelNameToMapping == null) {
            topLevelNameToMapping = createTopLevelTypeMapping();
        }
        return topLevelNameToMapping;
    }

    protected void instantiateSuperTemplates(CXXTemplate template, TypeElement typeElement) {
        CXXHead[] heads = typeElement.getAnnotationsByType(CXXHead.class);
        if (template != null) {
            heads = merge(template.include(), heads);
        }
        CXXSuperTemplate[] superTemplates = typeElement.getAnnotationsByType(CXXSuperTemplate.class);
        Set<String> processed = new HashSet<>();
        if (superTemplates == null) {
            for (CXXSuperTemplate superTemplate : superTemplates) {
                instantiateSuperTemplates(superTemplate);
                processed.add(superTemplate.type());
            }
        }
        Map<String, TypeMapping> topLevelNameToMapping = createTopLevelTypeMapping(template, typeElement.getTypeParameters(), typeElement);
        List<? extends TypeMirror> superTypes = typeUtils().directSupertypes(typeElement.asType());
        // i = 1: skip super class and start from interface
        outer: for (int i = 1; i < superTypes.size(); i++) {
            DeclaredType superType = (DeclaredType) superTypes.get(i);
            if (!isFFIPointer(superType)) {
                continue;
            }
            FFITypeAlias ffiTypeAlias = asElement(superType).getAnnotation(FFITypeAlias.class);
            if (ffiTypeAlias == null) {
                continue; // ignore fake/phantom/helper FFIPointer
            }
            List<? extends TypeMirror> typeArguments = superType.getTypeArguments();
            if (typeArguments.isEmpty()) {
                continue;
            }
            TypeElement superElement = (TypeElement) superType.asElement();
            String superBaseName = superElement.getQualifiedName().toString();
            if (processed.contains(superBaseName)) {
                continue; // instantiated
            }
            TypeMapping[] parameterMapping = getTypeArgumentsTypeMapping(typeArguments, topLevelNameToMapping);
            if (parameterMapping == null) {
                // we cannot automatically instantiate it
                continue;
            }
            registry.processType(processingEnv, superElement, getCXXTemplate(parameterMapping, heads), true);
            processed.add(superBaseName);
        }
    }

    void instantiateSuperTemplates() {
        instantiateSuperTemplates(typeDef.getCXXTemplate(), typeDef.getTypeElement(processingEnv));
    }

    private void generateFFIPointerOrFFILibrary() {
        if (isGenFFITemplate()) {
            throw new IllegalStateException("Must be FFIPointer or FFILibrary, got " + genType);
        }
        try {
            // do preparation to collect methods
            prepare();

            // generate heads
            generateIncludedCXXHeads();
            beginCxxBody();

            generateCommonStubs();

            this.classBuilder = TypeSpec.classBuilder(getGeneratedJavaSimpleClassName()).addModifiers(Modifier.PUBLIC).addSuperinterface(theTypeMirror);

            String libraryName = getLibraryName();
            if (libraryName != null) {
                // this.classBuilder.addStaticBlock(CodeBlock.builder().addStatement("System.loadLibrary($S)", libraryName).build());
                this.classBuilder.addStaticBlock(CodeBlock.builder()
                        .beginControlFlow("try")
                        .addStatement("System.loadLibrary($S)", libraryName)
                        .nextControlFlow("catch ($T e)", UnsatisfiedLinkError.class)
                        .addStatement("System.load($T.findNativeLibrary($L.class, $S))", FFITypeFactory.class, getGeneratedJavaSimpleClassName(), libraryName)
                        .endControlFlow()
                        .build());
            }

            if (isGenFFIPointer()) {
                generateFFIPointer();
            } else if (isGenFFILibrary()) {
                generateFFILibrary();
            } else {
                throw new IllegalStateException("TODO: unknown GenType: " + this.genType);
            }

            if (isGenFFIPointer()) {
                if (hasFactory) {
                    classBuilder.addAnnotation(
                            AnnotationSpec.builder(FFIForeignType.class).
                                    addMember("value", "$S", getCxxFullTypeName()).
                                    addMember("factory", "$L.class", getGeneratedFactorySimpleClassName()).build()
                    );
                } else {
                    classBuilder.addAnnotation(
                            AnnotationSpec.builder(FFIForeignType.class).
                                    addMember("value", "$S", getCxxFullTypeName()).build()
                    );
                }
            }

            // Mark
            classBuilder.addAnnotation(AnnotationSpec.builder(FFISynthetic.class).addMember(
                    "value", "$S", theTypeElement.getQualifiedName().toString()
            ).build());
            writeTypeSpec(classBuilder.build());

            if (writeCxxFile) {
                endCxxBody();
                String nativeFileName = getGeneratedCXXFileName();
                FileObject fileObject = processingEnv.getFiler().createResource(registry.processor.cxxOutputLocation, "", nativeFileName);
                try (Writer writer = fileObject.openWriter()) {
                    writer.write(cxxWriter.toString());
                    writer.flush();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Fail to generate class for " + AnnotationProcessorUtils.format(theTypeElement), e);
        }
    }

    private void generateCommonStubs() {
        cxxWriter.append("// Common Stubs\n");

        if (registry.processor.handleException) {
            // unknown exception
            cxxWriter.append("static jthrowable unknownException(JNIEnv* env) {\n");
            cxxWriter.append("    jclass clazz = env->FindClass(\"java/lang/RuntimeException\");\n");
            cxxWriter.append("    jmethodID mid = env->GetMethodID(clazz, \"<init>\", \"(Ljava/lang/String;)V\");\n");
            cxxWriter.append("    jstring msg = env->NewStringUTF(\"Unknown exception occurred.\");\n");
            cxxWriter.append("    return (jthrowable)env->NewObject(clazz, mid, msg);\n");
            cxxWriter.append("}\n");
        }

        cxxWriter.append("\n");
    }

    protected void generateFFILibrary() {
        {
            // add default constructor;
            classBuilder.addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .build());
        }
        {
            classBuilder.addField(TypeName.get(theTypeMirror), "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
            classBuilder.addStaticBlock(CodeBlock.builder()
                    .addStatement("INSTANCE = new $L()", getGeneratedJavaSimpleClassName())
                    .build());

        }

        generateFFIPropertyFields();

        for (ExecutableDef executableDef : generationList) {
            ExecutableElement executableElement = executableDef.getExecutableElement();
            if (executableElement.isDefault()) {
                continue;
            }
            try {
                saveTheExecutableDef(executableDef);
                generateFFIDelegateMethod(executableDef);
            } finally {
                clearTheExecutableDef();
            }
        }
    }

    private void saveTheExecutableDef(ExecutableDef executableDef) {
        this.theExecutableElement = executableDef.getExecutableElement();
        this.theExecutableType = executableDef.getExecutableType();
    }

    private void clearTheExecutableDef() {
        this.theExecutableElement = null;
        this.theExecutableType = null;
    }

    private void generateFFIPropertyFields() {
        List<FFIPropertyFieldDef> propertyFieldDefs = collectFFIPropertyFields();
        if (propertyFieldDefs.isEmpty()) {
            return;
        }
        for (FFIPropertyFieldDef fieldDef : propertyFieldDefs) {
            TypeMirror type = tryUnboxing(fieldDef.type);
            classBuilder.addField(TypeName.get(type), fieldDef.name, Modifier.PRIVATE);
        }
    }


    private String getFFILibraryNamespace() {
        return typeDef.getFFILibraryNamespace();
    }

    private String getCxxFullTypeName() {
        return typeDef.getCXXFullTypeName();
    }

    private String getPackageName() {
        return typeDef.getPackageName();
    }

    private String getGeneratedJavaClassName() {
        return typeDef.getGeneratedJavaClassName();
    }

    private String getGeneratedCXXHXXFileNameBase() {
        return "jni_" + getGeneratedJavaClassName().replace('.', '_').replace('$', '_');
    }

    private String getGeneratedCXXFileName() {
        return getGeneratedCXXHXXFileNameBase() + ".cc";
    }

    String getGeneratedHXXFileName() {
        return getGeneratedCXXHXXFileNameBase() + ".h";
    }

    private String getGeneratedJavaSimpleClassName() {
        return typeDef.getGeneratedJavaSimpleClassName();
    }

    String getLibraryName() {
        return getLibraryName(theTypeElement);
    }

    void collectHeads(CXXHead[] heads, List<String> results) {
        for (CXXHead head : heads) {
            if (head != null) {
                for (String v : head.value()) {
                    results.add(String.format("\"%s\"", v));
                }
                for (String v : head.system()) {
                    results.add(String.format("<%s>", v));
                }
            }
        }
    }

    void collectHeads(TypeElement typeElement, List<String> results) {
        CXXHead[] heads = typeElement.getAnnotationsByType(CXXHead.class);
        if (isFFIMirror(typeElement)) {
            results.add("\"" + getGeneratedHXXFileName() + "\"");
        }
        collectHeads(heads, results);
        for(TypeMirror intfs : typeElement.getInterfaces()) {
            TypeElement e = (TypeElement) typeUtils().asElement(intfs);
            collectHeads(e, results);
        }
    }

    private void beginHXX() {
        String guard = AnnotationProcessorUtils.toHeaderGuard(getGeneratedHXXFileName());
        hxxWriter.append("#ifndef ").append(guard).append("\n");
        hxxWriter.append("#define ").append(guard).append("\n");
    }

    private void endHXX() {
        String guard = AnnotationProcessorUtils.toHeaderGuard(getGeneratedHXXFileName());
        hxxWriter.append("#endif // ").append(guard).append("\n");
    }

    private void collectHeadsFromMirrorFields(Set<String> additional, List<FFIMirrorFieldDef> fields) {
        if (!fields.isEmpty()) {
            additional.add("<utility>");
        }
        fields.forEach(f -> collectHeadsFromTypeMirror(additional, f.type));
    }

    private void collectHeadsFromTypeMirror(Set<String> additional, TypeMirror typeMirror) {
        if (isFFIByteString(typeMirror)) {
            additional.add("<string>");
            return;
        }
        if (isFFIVector(typeMirror)) {
            additional.add("<vector>");
            DeclaredType declaredType = (DeclaredType) typeMirror;
            declaredType.getTypeArguments().forEach( t -> collectHeadsFromTypeMirror(additional, t));
            return;
        }
        if (isFFIMirror(typeMirror)) {
            sanityCheckFFIMirror(typeMirror);
            DeclaredType declaredType = (DeclaredType) typeMirror;
            String ffiTypeName = getFFITypeNameForBuiltinType(declaredType);
            TypeDef typeDef = getTypeDefByForeignName(ffiTypeName, declaredType);
            String header = registry.getFFIMirrorHeaderName(typeDef);
            additional.add("\"" + header + "\"");
            return;
        }
        if (AnnotationProcessorUtils.isDeclaredType(typeMirror)) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            declaredType.getTypeArguments().forEach( t -> collectHeadsFromTypeMirror(additional, t));
        }
    }

    /**
     * library-header includes all FFIMirror heads.
     * @throws IOException
     */
    private void generateIncludedCXXHeads() throws IOException {
        if (typeDef.isFFIMirror()) {
            String common = registry.getFFIMirrorLibraryHeaderName(this);
            writeCXXHead(cxxWriter, "\"" + common + "\"");
        } else {
            Set<String> additional = new HashSet<>();
            collectHeadsFromExecutable(additional);
            generateIncludedCXXHeads(false, additional);
        }
    }

    private void collectHeadsFromExecutable(Set<String> additional) {
        if (executableElementExecutableTypeMappingMap == null) {
            throw new IllegalStateException("Oops...");
        }
        for (ExecutableTypeMapping executableTypeMapping : executableElementExecutableTypeMappingMap.values()) {
            {
                TypeMapping typeMapping = executableTypeMapping.getReturnTypeMapping();
                if (typeMapping != null) {
                    collectHeadsFromTypeMirror(additional, typeMapping.java);
                }
            }
            {
                for (int i = 0; i < executableTypeMapping.getNumberOfParameterTypes(); i++) {
                    TypeMapping typeMapping = executableTypeMapping.getParameterTypeMapping(i);
                    if (typeMapping != null) {
                        collectHeadsFromTypeMirror(additional, typeMapping.java);
                    }
                }
            }
        }
    }

    void generateFFIMirrorHeader() {
        reset();
        prepare();
        List<FFIMirrorFieldDef> fields = collectFFIMirrorFields();
        Set<String> additional = new HashSet<>(5);
        collectHeadsFromMirrorFields(additional, fields);
        beginHXX();
        generateIncludedCXXHeads(true, additional);
        beginNamespace(hxxWriter);
        generateFFIMirrorDeclaration(fields);
        endNamespace(hxxWriter);
        endHXX();
        String nativeFileName = getGeneratedHXXFileName();
        try {
            FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", nativeFileName);
            try (Writer writer = fileObject.openWriter()) {
                writer.write(hxxWriter.toString());
                writer.flush();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot generate header for " + typeDef + " due to " + e.getMessage(), e);
        }
    }

    private void endNamespace(StringBuilder writer) {
        FFINameSpace nameSpace = theTypeElement.getAnnotation(FFINameSpace.class);
        if (nameSpace == null || nameSpace.value().isEmpty()) {
            return;
        }
        String[] namespaceList = nameSpace.value().split("::");
        for (int i = namespaceList.length - 1; i >= 0; i--) {
            writer.append("} // end namespace ").append(namespaceList[i]).append("\n");
        }
    }

    private void beginNamespace(StringBuilder writer) {
        FFINameSpace nameSpace = theTypeElement.getAnnotation(FFINameSpace.class);
        if (nameSpace == null || nameSpace.value().isEmpty()) {
            return;
        }
        String[] namespaceList = nameSpace.value().split("::");
        for (String s : namespaceList) {
            writer.append("namespace ").append(s).append(" {\n");
        }
    }

    // Generate default struct
    private void generateFFIMirrorDeclaration(List<FFIMirrorFieldDef> fields) {
        // TODO: No namespace is supported now;
        if (!theTypeElement.getTypeParameters().isEmpty()) {
            throw reportError("TODO: We have no support of parameterized types now");
        }
        hxxWriter.append("struct ").append(typeDef.getCXXBaseSimpleTypeName());
        generateFFIMirrorSuperDeclaration();
        hxxWriter.append(" {\n");
        generateFFIMirrorFieldsDeclaration(fields);
        generateFFIMirrorDefaultConstructor(fields);
        generateFFIMirrorDefaultCopyConstructor(fields);
        generateFFIMirrorDefaultMoveConstructor(fields);
        generateFFIMirrorDefaultCopyAssignment(fields);
        generateFFIMirrorDefaultMoveAssignment(fields);
        hxxWriter.append("}; // end of type declaration\n");
    }

    static boolean useCopyAndSwap = false;

    private void generateFFIMirrorDefaultCopyAssignment(List<FFIMirrorFieldDef> fields) {
        hxxWriter.append("\t")
                .append(typeDef.getCXXBaseTypeName())
                .append(" & operator = (");

        if (useCopyAndSwap) {
            hxxWriter.append(typeDef.getCXXBaseTypeName());
        } else {
            hxxWriter.append("const ")
                    .append(typeDef.getCXXBaseTypeName())
                    .append(" &");
        }
        hxxWriter.append(" from) {\n")
                .append("\t\tif (this == &from) return *this;\n");
        for (FFIMirrorFieldDef field : fields) {
            generateDefaultCopyAssignment(field, "from");
        }
        hxxWriter.append("\t\treturn *this;\n\t}\n");
    }

    private void generateDefaultCopyAssignment(FFIMirrorFieldDef fieldDef, String fromName) {
        if (useCopyAndSwap) {
            // copy-and-swap: see https://en.cppreference.com/w/cpp/language/operators#Assignment_operator
            hxxWriter.append("\t\tstd::swap(")
                    .append(fieldDef.name).append(", ").append(fromName).append(".").append(fieldDef.name).append(");\n");
            return;
        }
        hxxWriter.append("\t\t")
                .append(fieldDef.name).append(" = ").append(fromName).append(".").append(fieldDef.name).append(";\n");
    }

    private void generateFFIMirrorDefaultMoveAssignment(List<FFIMirrorFieldDef> fields) {
        hxxWriter.append("\t")
                .append(typeDef.getCXXBaseTypeName())
                .append(" & operator = (")
                .append(typeDef.getCXXBaseTypeName())
                .append(" && from) {\n")
                .append("\t\tif (this == &from) return *this;\n");
        for (FFIMirrorFieldDef field : fields) {
            generateDefaultMoveAssignment(field, "from");
        }
        hxxWriter.append("\t\treturn *this;\n\t}\n");
    }

    private void generateDefaultMoveAssignment(FFIMirrorFieldDef fieldDef, String fromName) {
        TypeMirror typeMirror = fieldDef.type;
        if (isFFIPointer(typeMirror)) {
            hxxWriter.append("\t\t").append(fieldDef.name).append(" = std::move(")
                    .append(fromName).append(".").append(fieldDef.name).append(");\n");
        } else {
            hxxWriter.append("\t\t").append(fieldDef.name).append(" = ")
                    .append(fromName).append(".").append(fieldDef.name).append(";\n");
        }
    }

    private void generateFFIMirrorDefaultConstructor(List<FFIMirrorFieldDef> fields) {
        // TODO:
        hxxWriter.append("\t").append(typeDef.getCXXBaseSimpleTypeName()).append("() ");
        int size = fields.size();
        if (size == 0) {
            hxxWriter.append(" {}\n");
        } else {
            hxxWriter.append(" : ");
            generateDefaultInitializer(fields.get(0));
            for (int i = 1; i < size; i++) {
                hxxWriter.append(", ");
                generateDefaultInitializer(fields.get(i));
            }
            hxxWriter.append(" {}\n");
        }
    }

    private void generateFFIMirrorDefaultCopyConstructor(List<FFIMirrorFieldDef> fields) {
        // TODO:
        hxxWriter.append("\t").append(typeDef.getCXXBaseSimpleTypeName())
                .append("(const ")
                .append(typeDef.getCXXBaseSimpleTypeName())
                .append(" &from) ");
        int size = fields.size();
        if (size == 0) {
            hxxWriter.append(" {}\n");
        } else {
            hxxWriter.append(" : ");
            generateDefaultCopyInitializer(fields.get(0), "from");
            for (int i = 1; i < size; i++) {
                hxxWriter.append(", ");
                generateDefaultCopyInitializer(fields.get(i), "from");
            }
            hxxWriter.append(" {}\n");
        }
    }

    private void generateDefaultCopyInitializer(FFIMirrorFieldDef fieldDef, String fromName) {
        hxxWriter.append(fieldDef.name).append("(").append(fromName).append(".").append(fieldDef.name).append(")");
    }

    private void generateDefaultMoveInitializer(FFIMirrorFieldDef fieldDef, String fromName) {
        TypeMirror typeMirror = fieldDef.type;
        if (isFFIPointer(typeMirror)) {
            hxxWriter.append(fieldDef.name).append("(std::move(").append(fromName).append(".").append(fieldDef.name).append("))");
        } else {
            hxxWriter.append(fieldDef.name).append("(").append(fromName).append(".").append(fieldDef.name).append(")");
        }
    }

    private void generateFFIMirrorDefaultMoveConstructor(List<FFIMirrorFieldDef> fields) {
        // TODO:
        hxxWriter.append("\t").append(typeDef.getCXXBaseSimpleTypeName())
                .append("(")
                .append(typeDef.getCXXBaseSimpleTypeName())
                .append(" &&from) ");
        int size = fields.size();
        if (size == 0) {
            hxxWriter.append(" {}\n");
        } else {
            hxxWriter.append(" : ");
            generateDefaultMoveInitializer(fields.get(0), "from");
            for (int i = 1; i < size; i++) {
                hxxWriter.append(", ");
                generateDefaultMoveInitializer(fields.get(i), "from");
            }
            hxxWriter.append(" {}\n");
        }
    }

    private void generateDefaultInitializer(FFIMirrorFieldDef fieldDef) {
        TypeMirror type = fieldDef.type;
        type = tryUnboxing(type);
        if (isJavaPrimitive(type)) {
            switch (type.getKind()) {
                case BOOLEAN:
                    hxxWriter.append(fieldDef.name).append("(false)");
                    return;
                case BYTE:
                case CHAR:
                case SHORT:
                case INT:
                case LONG:
                    hxxWriter.append(fieldDef.name).append("(0)");
                    return;
                case FLOAT:
                case DOUBLE:
                    hxxWriter.append(fieldDef.name).append("(0.0)");
                    return;
                default:
                    throw reportError("Oops, not an FFIMirror supported type: " + type);
            }
        } else if (isFFIByteString(type)) {
            hxxWriter.append(fieldDef.name).append("()");
        } else if (isFFIVector(type)) {
            hxxWriter.append(fieldDef.name).append("()");
        } else if (isFFIMirror(type)) {
            hxxWriter.append(fieldDef.name).append("()");
        } else {
            throw reportError("Oops, not an FFIMirror supported type: " + type);
        }
    }

    private void generateFFIMirrorFieldsDeclaration(List<FFIMirrorFieldDef> fields) {
        for (FFIMirrorFieldDef field : fields) {
            writeFFIMirrorField(hxxWriter, field);
        }
    }

    private void writeFFIMirrorField(StringBuilder writer, FFIMirrorFieldDef fieldDef) {
        TypeMapping typeMapping = fieldDef.typeMapping;
        TypeMirror javaType = fieldDef.type;
        String cxxType;
        if (typeMapping == null) {
            if (isJavaBoxedPrimitive(javaType)) {
                javaType = tryUnboxing(javaType);
            }
            if (!isJavaPrimitive(javaType)) {
                throw reportError("Missing type mapping for type " + fieldDef.type);
            }
            cxxType = nativeType(javaType);
            writer.append("\t").append(cxxType);
        } else {
            cxxType = typeMapping.cxx;
            writer.append("\t").append(cxxType);
            VRP vrp = fieldDef.vrp;
            if (vrp == VRP.Pointer) {
                writer.append("*");
            }
        }
        writer.append(" ").append(fieldDef.name).append(";\n\n");
    }

    private void addFFIMirrorField(/* ExecutableElement executableElement, ExecutableType executableType, TODO: add more information. */
            Map<String, FFIMirrorFieldDef> fields, String name, VRP vrp, TypeMirror type, TypeMapping typeMapping) {
        FFIMirrorFieldDef fieldDef = fields.get(name);
        if (fieldDef == null) {
            fields.put(name, new FFIMirrorFieldDef(name, type, vrp, typeMapping));
        } else {
            if (fieldDef.vrp != vrp) {
                throw reportError("Mismatched VRP for FFIMirror field " + name + ", " + fieldDef.vrp + " and " + vrp);
            }
            if (!typeUtils().isSameType(fieldDef.type, type)) {
                throw reportError("Mismatched type for FFIMirror field " + name + ", " + fieldDef.type + " and " + type);
            }
            if ((fieldDef.typeMapping == null && typeMapping != null)
                || (fieldDef.typeMapping != null && !isSameTypeMapping(fieldDef.typeMapping, typeMapping))) {
                throw reportError("Mismatched type mapping for FFIMirror field " + name + ", " + fieldDef.typeMapping + " and " + typeMapping);
            }
        }
    }

    private List<FFIMirrorFieldDef> collectFFIMirrorFields() {
        Map<String, FFIMirrorFieldDef> fields = new HashMap<>();
        for (ExecutableDef executableDef : generationList) {
            ExecutableElement executableElement = executableDef.getExecutableElement();
            ExecutableType executableType = executableDef.getExecutableType();
            ExecutableTypeMapping executableTypeMapping = getExecutableTypeMapping(executableElement);
            FFIGetter ffiGetter = executableElement.getAnnotation(FFIGetter.class);
            FFISetter ffiSetter = executableElement.getAnnotation(FFISetter.class);
            if (ffiGetter != null) {
                if (ffiSetter != null) {
                    throw reportError(executableElement, executableType,
                            "Malformed method: A method cannot be both FFISetter and FFIGetter");
                }
                if (!executableElement.getParameters().isEmpty()) {
                    throw reportError(executableElement, executableType,
                            "Malformed method: An FFIGetter cannot have any parameter.");
                }
                String name = getDelegateNativeRawMethodName(executableElement);
                VRP vrp = getReturnVRP(executableElement);
                TypeMirror type = executableType.getReturnType();
                TypeMapping typeMapping = executableTypeMapping == null ? null : executableTypeMapping.getReturnTypeMapping();
                addFFIMirrorField(fields, name, vrp, type, typeMapping);
            } else if (ffiSetter != null) {
                if (executableType.getReturnType().getKind() != TypeKind.VOID) {
                    throw reportError(executableElement, executableType,
                            "Malformed method: An FFISetter can only have void return type");
                }
                List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
                if (parameterTypes.size() != 1) {
                    throw reportError(executableElement, executableType,
                            "Malformed method: An FFISetter must have exactly one parameter.");
                }
                String name = getDelegateNativeRawMethodName(executableElement);
                VRP vrp = getParameterVRP(executableElement, 0);
                TypeMirror type = parameterTypes.get(0);
                TypeMapping typeMapping = executableTypeMapping == null ? null : executableTypeMapping.getParameterTypeMapping(0);
                addFFIMirrorField(fields, name, vrp, type, typeMapping);
            }
        }
        return new ArrayList<>(fields.values());
    }

    private void addFFIPropertyField(ExecutableElement executableElement, ExecutableType executableType,
                                     Map<String, FFIPropertyFieldDef> fields, String name, TypeMirror type, boolean getter) {
        FFIPropertyFieldDef fieldDef = fields.get(name);
        type = tryUnboxing(type); // all boxed primitives are converted into property
        if (fieldDef == null) {
            fieldDef = new FFIPropertyFieldDef(name, type);
            fields.put(name, fieldDef);
        } else {
            if (!typeUtils().isSameType(fieldDef.type, type)) {
                if (fieldDef.hasGetter) {
                    throw reportError(fieldDef.getterElement, fieldDef.getterType,
                            "Malformed method: Mismatched type for FFIMirror field " + name + ", " + fieldDef.type + " and " + type);
                } else if (fieldDef.hasSetter) {
                        throw reportError(fieldDef.setterElement, fieldDef.setterType,
                                "Mismatched type for FFIMirror field " + name + ", " + fieldDef.type + " and " + type);
                } else {
                    throw reportError("Mismatched type for FFIMirror field " + name + ", " + fieldDef.type + " and " + type);
                }
            }
        }
        if (getter) {
            if (fieldDef.hasGetter) {
                throw reportError(fieldDef.getterElement, fieldDef.getterType,
                        "Malformed method: An FFIProperty " + name + ", " + fieldDef.type + " already has a getter");
            }
            fieldDef.hasGetter = true;
            fieldDef.getterElement = executableElement;
            fieldDef.getterType = executableType;
        } else {
            if (fieldDef.hasSetter) {
                throw reportError(fieldDef.setterElement, fieldDef.setterType,
                        "Malformed method: An FFIProperty " + name + ", " + fieldDef.type + " already has a setter");
            }
            fieldDef.hasSetter = true;
            fieldDef.setterElement = executableElement;
            fieldDef.setterType = executableType;
        }
    }

    @SafeVarargs
    protected final void assertNoAnnotation(Element element, Class<? extends Annotation>... anns) {
        for (Class<? extends Annotation> cls : anns) {
            if (element.getAnnotation(cls) != null) {
                throw reportError("No annotation " + cls + " is allowed in " + element);
            }
        }
    }

    private List<FFIPropertyFieldDef> collectFFIPropertyFields() {
        Map<String, FFIPropertyFieldDef> fields = new HashMap<>();
        for (ExecutableDef executableDef : generationList) {
            ExecutableElement executableElement = executableDef.getExecutableElement();
            ExecutableType executableType = executableDef.getExecutableType();
            FFIProperty ffiProperty = executableElement.getAnnotation(FFIProperty.class);
            if (ffiProperty == null) {
                continue;
            }
            assertNoAnnotation(executableElement, FFISetter.class, FFIGetter.class, CXXOperator.class);
            TypeMirror returnType = executableType.getReturnType();
            if (returnType.getKind() == TypeKind.VOID) {
                if (executableType.getParameterTypes().size() != 1) {
                    throw reportError(executableElement, executableType,
                            "Malformed FFIProperty method: Must have exact one parameter when return type is void.");
                }
                String name = getDelegateNativeRawMethodName(executableElement);
                TypeMirror typeMirror = executableType.getParameterTypes().get(0);
                addFFIPropertyField(executableElement, executableType, fields, name, typeMirror, true);
            } else if (executableType.getParameterTypes().isEmpty()) {
                String name = getDelegateNativeRawMethodName(executableElement);
                TypeMirror typeMirror = executableType.getReturnType();
                addFFIPropertyField(executableElement, executableType, fields, name, typeMirror, false);
            } else {
                throw reportError(executableElement, executableType,
                        "Malformed method: An FFIProperty must have no parameter when return type is not void.");
            }
        }
        fields.values().forEach(f -> {
            if (!f.hasGetter) {
                assert f.hasSetter;
                throw reportError(f.setterElement, f.setterType,"An FFIProperty " + f.name + "/" + f.type
                        + " must have a setter and a getter");
            } else if (!f.hasSetter) {
                throw reportError(f.getterElement, f.getterType, "An FFIProperty " + f.name + "/" + f.type
                        + " must have a setter and a getter");
            }
        });
        return new ArrayList<>(fields.values());
    }

    private void generateFFIMirrorSuperDeclaration() {
        for (TypeMirror typeMirror : theTypeElement.getInterfaces()) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            TypeElement typeElement = (TypeElement) declaredType.asElement();
            FFITypeAlias ffiTypeAlias = typeElement.getAnnotation(FFITypeAlias.class);
            if (ffiTypeAlias != null) {
                throw reportError("TODO: cannot generate FFI super classes for FFIMirror");
            }
        }
    }

    protected String getInternalTypeName(TypeMapping typeMapping, ExecutableElement executableElement) {
        TypeDef internalTypeDef = getTypeDefByForeignName(typeMapping);
        if (internalTypeDef == null) {
            throw reportError(executableElement,
                    "Cannot find a TypeDef for \n\t" + typeMapping +
                            "\nduring generating\n\t" + AnnotationProcessorUtils.format(executableElement));
        }
        return internalTypeDef.getGeneratedJavaClassName();
    }

    protected TypeDef getTypeDefByForeignName(TypeMapping typeMapping) {
        DeclaredType javaType = (DeclaredType) typeMapping.java;
        if (isFFIBuiltinType(javaType)) {
            javaType = getFFIBuiltinImplType(javaType);
        }
        TypeDef typeDef = getTypeDefByForeignName(typeMapping.cxx, javaType);
        if (typeDef == null) {
            List<? extends TypeMirror> typeArguments = javaType.getTypeArguments();
            if (!typeArguments.isEmpty()) {
                // let's try to instantiate a template;
                TypeMapping[] argumentsTypeMapping = getTypeArgumentsTypeMapping(typeArguments, Collections.emptyMap());
                if (argumentsTypeMapping == null) {
                    return null;
                }
                registry.processType(processingEnv, (TypeElement) javaType.asElement(), getCXXTemplate(argumentsTypeMapping, getCXXHeads()), true);
                return getTypeDefByForeignName(typeMapping.cxx, javaType);
            }
        }
        return typeDef;
    }

    private CXXHead[] getCXXHeads() {
        CXXHead[] heads = theTypeElement.getAnnotationsByType(CXXHead.class);
        CXXHead[] extra = typeDef.getAdditionalInclude();
        return merge(heads, extra);
    }

    private void generateIncludedCXXHeads(boolean isFFIMirrorHead, Collection<String> additional) {
        StringBuilder writer = isFFIMirrorHead ? hxxWriter : cxxWriter;

        List<String> results = new ArrayList<>(additional);
        results.add("<jni.h>");
        results.add("<new>");

        if (registry.processor.traceJNICalls) {
            results.add("<stdio.h>");
        }

        collectHeads(theTypeElement, results);
        collectHeads(typeDef.getAdditionalInclude(), results);

        if (typeDef.isFFIMirror()) {
            String mirrorHeadName = "\"" + getGeneratedHXXFileName() + "\"";
            results.stream().filter(s -> !s.equals(mirrorHeadName)).forEach(s -> writeCXXHead(writer, s));
        } else {
            results.forEach(s -> writeCXXHead(writer, s));
        }
    }

    private void beginCxxBody() {
        cxxWriter.append("\n#ifdef __cplusplus\n" +
                         "extern \"C\" {\n" +
                         "#endif\n\n");
        beginNamespace(cxxWriter);
    }

    private void endCxxBody() {
        endNamespace(cxxWriter);
        cxxWriter.append("#ifdef __cplusplus\n" +
                "}\n" +
                "#endif\n");
    }

    /**
     * Factory must share the same CXXTemplate as super
     * @param factoryTypeElement
     * @return
     */
    private DeclaredType getFactoryTypeMirror(TypeElement factoryTypeElement) {
        if (theTypeMirror.getTypeArguments().isEmpty()) {
            return (DeclaredType) factoryTypeElement.asType();
        }
        return typeUtils().getDeclaredType(factoryTypeElement,
                                           theTypeMirror.getTypeArguments().toArray(new TypeMirror[0]));
    }

    private void generateFFIFactoryForFFIPointer() {
        int i = 0;
        TypeElement factoryHolderElement;
        TypeMirror factoryObjectType;
        if (isExpandedTemplate(theTypeElement)) {
            factoryObjectType = typeUtils().directSupertypes(theTypeMirror).get(1);
            // the user-provided declared element
            factoryHolderElement = (TypeElement) typeUtils().asElement(theTypeElement.getInterfaces().get(0));
        } else {
            factoryObjectType = theTypeMirror;
            factoryHolderElement = theTypeElement;
        }
        TypeElement factoryTypeElement = null;
        for (Element element : factoryHolderElement.getEnclosedElements()) {
            if (element instanceof TypeElement) {
                if (element.getKind() != ElementKind.INTERFACE) {
                    continue;
                }
                FFIFactory ffiFactory = element.getAnnotation(FFIFactory.class);
                if (ffiFactory != null) {
                    if (hasFactory) {
                        throw reportError("An FFIPointer class can only have at most one FFIFactory");
                    }
                    hasFactory = true;
                    factoryTypeElement = (TypeElement) element;
                }
            }
        }

        if (factoryTypeElement == null) {
            return;
        }

        DeclaredType factoryTypeMirror = getFactoryTypeMirror(factoryTypeElement);
        Map<ExecutableElement, ExecutableTypeMapping> factoryTypeMapping = collectForeign(factoryTypeElement, topLevelNameToMapping);

        String factoryClassSimpleName = getGeneratedFactorySimpleClassName();
        TypeSpec.Builder factoryClassBuilder = TypeSpec.classBuilder(factoryClassSimpleName).addModifiers(Modifier.PUBLIC);
        factoryClassBuilder.addSuperinterface(factoryTypeMirror);

        {
            // add default constructor;
            factoryClassBuilder.addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC).build());
        }
        {
            factoryClassBuilder.addField(TypeName.get(factoryTypeMirror), "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
            factoryClassBuilder.addStaticBlock(CodeBlock.builder()
                    .addStatement("INSTANCE = new $L()", factoryClassSimpleName).build());
        }

        for (Element e : factoryTypeElement.getEnclosedElements()) {
            if (e instanceof ExecutableElement) {
                ExecutableElement ee = (ExecutableElement) e;

                if (ee.isDefault()) {
                    // TODO: throw exception to forbid default methods
                    continue;
                }

                ExecutableType executableType = (ExecutableType) typeUtils().asMemberOf(factoryTypeMirror, ee);
                TypeMirror returnType = executableType.getReturnType();
                if (!typeUtils().isSameType(factoryObjectType, returnType)) {
                    throw reportError(ee, "All methods in an FFIFactory must return a proper type, " +
                            "expected " + factoryObjectType + ", got " + returnType);
                }

                String uniqueNativeMethodName = getNativeMethodName(ee.getSimpleName().toString()) + "Factory" + (i++);
                generateFFIFactoryMethod(factoryClassBuilder, factoryObjectType, uniqueNativeMethodName, ee, executableType, factoryTypeMapping.get(ee));
            }
        }

        writeTypeSpec(factoryClassBuilder.build());
    }

    private void passingArgumentsToJavaNative(StringBuilder sb, ExecutableElement executableElement,
                                              ExecutableType executableType, ExecutableTypeMapping executableTypeMapping) {
        List<? extends VariableElement> parameterElements = executableElement.getParameters();
        List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
        for (int i = 0; i < parameterElements.size(); i++) {
            VariableElement variableElement = parameterElements.get(i);
            if (skipParameter(variableElement)) {
                continue;
            }
            TypeMirror parameterType = parameterTypes.get(i);
            if (i > 0) {
                sb.append(", ");
            }
            if (isFFIPointer(parameterType)) {
                if (registry.processor.strictTypeCheck) {
                    TypeMapping parameterTypeMapping = executableTypeMapping.getParameterTypeMapping(i);
                    TypeDef parameterTypeDef = getTypeDefByForeignName(parameterTypeMapping);
                    if (parameterTypeDef == null) {
                        throw reportError(executableElement, executableType, "Cannot get TypeDef for " + parameterTypeMapping);
                    }
                    sb.append(String.format("((%s) %s).address",
                            parameterTypeDef.getGeneratedJavaClassName(),
                            variableElement.getSimpleName()));
                } else {
                    sb.append(String.format("((%s) %s).address",
                            FFIPointerImpl.class.getName(),
                            variableElement.getSimpleName()));
                }
            } else if (isCXXEnum(parameterType)) {
                sb.append(variableElement.getSimpleName()).append(".getValue()");
            } else if (isJavaPrimitive(tryUnboxing(parameterType))) {
                sb.append(variableElement.getSimpleName());
            } else {
                throw reportError(executableElement, executableType, "Unsupported parameter type: " + parameterType);
            }
        }
    }

    private void generateFFIFactoryMethod(TypeSpec.Builder factoryClassBuilder,
                                          TypeMirror factoryObjectType,
                                          String uniqueNativeMethodName,
                                          ExecutableElement executableElement, ExecutableType executableType, ExecutableTypeMapping executableTypeMapping) {
        String methodName = executableElement.getSimpleName().toString();
        boolean requireTypeMapping = requireCreatingTypeMapping(executableType);

        if (!requireTypeMapping || executableTypeMapping == null) {
            // the return type must be an FFIPointer so a type mapping is required.
            throw reportError(executableElement, "Missing type mapping for executable type: " + executableType);
        }

        List<? extends VariableElement> parameterElements = executableElement.getParameters();
        List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
        if (parameterElements.size() != parameterTypes.size()) {
            throw reportError("Oops: must be a bug.");
        }
        TypeMirror returnType = executableType.getReturnType();
        boolean isReturnFFIPointer = isFFIPointer(returnType);
        if (!isReturnFFIPointer) {
            throw reportError(executableElement, executableType, "Malformed method: A Factory method must return a FFIPointer, but got " + returnType);
        }

        VRP returnVRP = getReturnVRP(executableElement);
        boolean needCXXValueOpto = needCXXValueOptimization(executableElement, executableType);
        {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC);
            {
                // generate signature
                methodBuilder.returns(TypeName.get(returnType));
                for (int i = 0; i < parameterElements.size(); i++) {
                    VariableElement variableElement = parameterElements.get(i);
                    TypeMirror parameterType = parameterTypes.get(i);
                    methodBuilder.addParameter(TypeName.get(parameterType), variableElement.getSimpleName().toString());
                }
            }
            {
                // generate statement;
                StringBuilder sb = new StringBuilder();
                {
                    TypeMapping returnTypeMapping = executableTypeMapping.getReturnTypeMapping();
                    if (returnTypeMapping == null) {
                        throw reportError(executableElement, "Missing type mapping for return type: " + returnType);
                    }
                    if (!typeUtils().isAssignable(returnTypeMapping.java, returnType)) {
                        throw new IllegalStateException(String.format("%s != %s", returnType, returnTypeMapping.java));
                    }
                    if (!typeUtils().isAssignable(factoryObjectType, returnType)) {
                        throw new IllegalStateException(String.format("%s != %s", returnType, factoryObjectType));
                    }
                    String javaSimpleName = getGeneratedJavaSimpleClassName();
                    sb.append("return new ").append(javaSimpleName).append("(").append(javaSimpleName).append(".").append(
                        uniqueNativeMethodName).append("(");
                }

                if (needCXXValueOpto) {
                    String returnTypeInternalTypeName = getInternalTypeName(executableTypeMapping.getReturnTypeMapping(),
                            executableElement);
                    String tmp = com.alibaba.fastffi.CXXValueScope.class.getName() + ".allocate("
                            + returnTypeInternalTypeName + ".SIZE)";
                    boolean hasCopyToNativeParameters = parameterElements.size() > 0;
                    if (!hasCopyToNativeParameters) {
                        sb.append(tmp);
                    } else {
                        sb.append(tmp + ", ");
                    }
                }

                passingArgumentsToJavaNative(sb, executableElement, executableType, executableTypeMapping);

                sb.append(")").append(")"); // additional bracket for constructor
                methodBuilder.addStatement(sb.toString());
            }

            // Add to the factory class;
            factoryClassBuilder.addMethod(methodBuilder.build());
        }

        {
            // generate the native method in the enclosing class;
            {
                cxxWriter.append("JNIEXPORT\n");
                // generate Java native method
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(uniqueNativeMethodName)
                        .addModifiers(Modifier.PUBLIC, Modifier.NATIVE, Modifier.STATIC)
                        .returns(TypeName.LONG);
                cxxWriter.append("jlong").append(" ").append("JNICALL ");
                cxxWriter.append(encodeNativeMethodName(getGeneratedJavaClassName(), uniqueNativeMethodName, null));
                cxxWriter.append("(JNIEnv*, jclass");
                if (needCXXValueOpto) {
                    cxxWriter.append(", jlong rv_base");
                    methodBuilder.addParameter(TypeName.LONG, "rv_base");
                }
                for (int i = 0; i < parameterElements.size(); i++) {
                    cxxWriter.append(", ");
                    VariableElement variableElement = parameterElements.get(i);

                    TypeMirror parameterType = parameterTypes.get(i);
                    TypeMirror nativeParameterType = usedInNative(parameterType);
                    String paramName = variableElement.getSimpleName().toString() + i; // add a suffix to avoid conflict
                    methodBuilder.addParameter(TypeName.get(nativeParameterType), paramName);
                    cxxWriter.append(nativeType(nativeParameterType)).append(" ").append(String.format("arg%d /* %s */", i, paramName)); // do not use paramName
                }
                cxxWriter.append(") {\n");
                if (registry.processor.traceJNICalls) {
                    cxxWriter.append("\tprintf(\"fastffi: [%s][%d]: %s\\n\", __FILE__, __LINE__, __func__);\n");
                }
                if (returnVRP == VRP.Pointer) {
                    String cxxFullTypeName = getCxxFullTypeName();
                    String expr = String.format("new %s(%%s)", cxxFullTypeName);
                    generateCallNative(executableElement, executableType, executableTypeMapping, expr);
                } else if (returnVRP == VRP.Value && needCXXValueOpto) {
                    // FIXME
                    String expr = String.format("%%s");
                    generateCallNative(executableElement, executableType, executableTypeMapping, expr);
                }
                cxxWriter.append("}\n\n");
                writeCxxFile = true;
                classBuilder.addMethod(methodBuilder.build());
            }
        }
    }

    private String getGeneratedFactorySimpleClassName() {
        return getGeneratedJavaSimpleClassName() + "Factory";
    }

    private String getFFIMirrorSimpleName() {
        FFITypeAlias typeAlias = theTypeElement.getAnnotation(FFITypeAlias.class);
        if (typeAlias == null || typeAlias.value().isEmpty()) {
            throw reportError("FFIMirror requires a simple name by FFITypeAlias: " + theTypeElement);
        }
        return typeAlias.value();
    }

    private String getFFIMirrorNameSpace() {
        FFINameSpace nameSpace = theTypeElement.getAnnotation(FFINameSpace.class);
        if (nameSpace == null || nameSpace.value().isEmpty()) {
            return "";
        }
        return nameSpace.value();
    }

    private boolean requireOverrideToString() {
        if (!isFFIJava(theTypeMirror)) {
            return false;
        }
        List<ExecutableDef> methods = nameToExecutables.get(elementUtils().getName("toJavaString"));
        if (methods == null || methods.isEmpty()) {
            return false;
        }
        ExecutableDef theMethod = null;
        for (ExecutableDef executableDef : methods) {
            ExecutableType executableType = executableDef.getExecutableType();
            if (!isJavaLangString(executableType.getReturnType())) {
                continue;
            }
            if (!executableType.getParameterTypes().isEmpty()) {
                continue;
            }
            theMethod = executableDef;
            break;
        }
        if (theMethod == null) {
            return false;
        }
        ExecutableElement executableElement = theMethod.getExecutableElement();
        // the default toJavaString does nothing but throws an exception.
        return !isDeclaredIn(executableElement, FFIJava.class);
    }

    private boolean requireOverrideHashCode() {
        if (!isFFIJava(theTypeMirror)) {
            return false;
        }
        List<ExecutableDef> methods = nameToExecutables.get(elementUtils().getName("javaHashCode"));
        if (methods == null || methods.isEmpty()) {
            return false;
        }
        ExecutableDef theMethod = null;
        for (ExecutableDef executableDef : methods) {
            ExecutableType executableType = executableDef.getExecutableType();
            if (executableType.getReturnType().getKind() != TypeKind.INT) {
                continue;
            }
            if (!executableType.getParameterTypes().isEmpty()) {
                continue;
            }
            theMethod = executableDef;
            break;
        }
        if (theMethod == null) {
            return false;
        }
        ExecutableElement executableElement = theMethod.getExecutableElement();
        // the default javaHashCode does nothing but throws an exception.
        return !isDeclaredIn(executableElement, FFIJava.class);
        // A non-trivial javaHashCode method
    }

    private boolean requireOverrideEquals() {
        if (!isFFIJava(theTypeMirror)) {
            return false;
        }
        List<ExecutableDef> methods = nameToExecutables.get(elementUtils().getName("javaEquals"));
        if (methods == null || methods.isEmpty()) {
            return false;
        }
        ExecutableDef theMethod = null;
        for (ExecutableDef executableDef : methods) {
            ExecutableType executableType = executableDef.getExecutableType();
            if (executableType.getReturnType().getKind() != TypeKind.BOOLEAN) {
                continue;
            }
            if (executableType.getParameterTypes().size() != 1) {
                continue;
            }
            if (!isSameType(executableType.getParameterTypes().get(0), Object.class)) {
                continue;
            }
            theMethod = executableDef;
            break;
        }
        if (theMethod == null) {
            return false;
        }
        ExecutableElement executableElement = theMethod.getExecutableElement();
        // the default javaEquals does nothing but throws an exception.
        return !isDeclaredIn(executableElement, FFIJava.class);
        // A non-trivial javaEquals method
    }

    private boolean isDeclaredIn(ExecutableElement executableElement, Class<?> clazz) {
        TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
        return isSameType(typeElement.asType(), clazz);
    }

    private void generateFFIPointer() {
        {
            TypeMapping superTypeMapping = getUniqueSuperTypeMapping();
            if (superTypeMapping == null) {
                classBuilder.superclass(FFIPointerImpl.class);
            } else {
                TypeDef superDef = getTypeDefByForeignName(superTypeMapping);
                if (superDef == null) {
                    throw reportError("Cannot find a TypeDef for " + superTypeMapping);
                }
                classBuilder.superclass(ClassName.get(superDef.getPackageName(), superDef.getGeneratedJavaSimpleClassName()));
            }
            // add the constructor that takes a pointer (long) as parameter.
            MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);
            builder.addParameter(TypeName.LONG, "address", Modifier.FINAL).
                    addStatement("super($N)", "address");
            classBuilder.addMethod(builder.build());
        }
        {
            classBuilder.addField(TypeName.INT, "SIZE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
            classBuilder.addField(TypeName.INT, "HASH_SHIFT", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
            this.classBuilder.addStaticBlock(CodeBlock.builder()
                    .addStatement("SIZE = _elementSize$$$$$$()")
                    .addStatement("assert SIZE > 0")
                    .addStatement("HASH_SHIFT = 31 - Integer.numberOfLeadingZeros(1 + SIZE)")
                    .addStatement("assert HASH_SHIFT > 0")
                    .build());
            MethodSpec.Builder builder = MethodSpec.methodBuilder("_elementSize$$$")
                                                   .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL,
                                                                 Modifier.NATIVE)
                                                   .returns(TypeName.INT);
            classBuilder.addMethod(builder.build());
        }
        if (isFFIMirror(theTypeMirror)) {
            AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(FFIMirrorDefinition.class);
            annotationBuilder.addMember("header", "$S", getGeneratedHXXFileName());
            annotationBuilder.addMember("name", "$S", getFFIMirrorSimpleName());
            annotationBuilder.addMember("namespace", "$S", getFFIMirrorNameSpace());
            List<FFIMirrorFieldDef> fields = collectFFIMirrorFields();
            fields.sort(Comparator.comparing(o -> o.name));
            for (FFIMirrorFieldDef fieldDef : fields) {
                AnnotationSpec.Builder fieldDefBuilder = AnnotationSpec.builder(FFIMirrorFieldDefinition.class);
                fieldDefBuilder.addMember("name", "$S", fieldDef.name);
                String cxx = getFFITypeNameForFFIMirrorType(fieldDef.type, false);
                if (isFFIPointer(fieldDef.type) && fieldDef.vrp == VRP.Pointer) {
                    cxx = cxx + "*";
                }
                fieldDefBuilder.addMember("foreignType", "$S", cxx);
                fieldDefBuilder.addMember("javaType", "$S", getFFITypeNameForFFIMirrorType(fieldDef.type, true));
                annotationBuilder.addMember("fields", "$L", fieldDefBuilder.build());
            }
            classBuilder.addAnnotation(annotationBuilder.build());
        }
        {
            // add default equals and hashCode
            {
                MethodSpec.Builder builder = MethodSpec.methodBuilder("equals")
                        .addModifiers(Modifier.PUBLIC).returns(TypeName.BOOLEAN);
                if (requireOverrideEquals()) {
                    builder.addParameter(TypeName.OBJECT, "o")
                            .addStatement("return javaEquals(o)");
                } else {
                    builder.addParameter(TypeName.OBJECT, "o").
                            addStatement("if (this == o) return true").
                            addStatement("if (o == null || getClass() != o.getClass()) return false").
                            addStatement("$L that = ($L) o", getGeneratedJavaSimpleClassName(), getGeneratedJavaSimpleClassName()).
                            addStatement("return this.address == that.address");
                }
                classBuilder.addMethod(builder.build());
            }
            {
                MethodSpec.Builder builder = MethodSpec.methodBuilder("hashCode")
                        .addModifiers(Modifier.PUBLIC).returns(TypeName.INT);
                if (requireOverrideHashCode()) {
                    builder.addStatement("return javaHashCode()");
                } else {
                    builder.addStatement("return (int) (address >> HASH_SHIFT)");
                }
                classBuilder.addMethod(builder.build());
            }
            {

                MethodSpec.Builder builder = MethodSpec.methodBuilder("toString")
                        .addModifiers(Modifier.PUBLIC).returns(ClassName.get(String.class));
                if (requireOverrideToString()) {
                    builder.addStatement("return toJavaString()");
                } else {
                    builder.addStatement("return getClass().getName() + \"@\" + Long.toHexString(address)");
                }
                classBuilder.addMethod(builder.build());
            }
        }


        {
            // generate SIZE
            cxxWriter.append("JNIEXPORT\n")
                     .append(
                         "jint JNICALL " + encodeNativeMethodName(getGeneratedJavaClassName(), "_elementSize$$$", null))
                     .append("(JNIEnv*, jclass) {\n")
                     .append(registry.processor.traceJNICalls ? "    printf(\"fastffi: [%s][%d]: %s\\n\", __FILE__, __LINE__, __func__);\n" : "")
                     .append("    return (jint)sizeof(" + getCxxFullTypeName() + ");\n")
                     .append("}");
            writeCxxFile = true; // TODO: every FFIPointer now have a _elementSize$$$.
        }

        cxxWriter.append("\n\n");

        generateFFIPropertyFields();

        for (ExecutableDef executableDef : generationList) {
            ExecutableElement executableElement = executableDef.getExecutableElement();
            if (executableElement.isDefault()) {
                continue;
            }
            try {
                saveTheExecutableDef(executableDef);
                generateFFIDelegateMethod(executableDef);
            } finally {
                clearTheExecutableDef();
            }
        }

        generateFFIFactoryForFFIPointer();
    }

    private TypeMapping getUniqueSuperTypeMapping() {
        TypeMirror ffiPointerType = getTypeMirror(FFIPointer.class);
        TypeElement typeElement = getDeclaredTypeElement(theTypeElement);
        TypeMirror rawTheTypeMirror = typeElement.asType();
        List<? extends TypeMirror> superTypes = typeUtils().directSupertypes(rawTheTypeMirror);
        // i = 1: skip super class
        DeclaredType rawTheSuperType = null;
        for (int i = 1; i < superTypes.size(); i++) {
            TypeMirror superType = superTypes.get(i);
            if (typeUtils().isSameType(ffiPointerType, superType)) {
                continue;
            }
            if (!isSubType(superType, ffiPointerType)) {
                continue;
            }
            FFITypeAlias ffiTypeAlias = asElement(superType).getAnnotation(FFITypeAlias.class);
            if (ffiTypeAlias == null) {
                continue; // ignore fake/phantom/helper FFIPointer
            }
            if (rawTheSuperType == null) {
                rawTheSuperType = (DeclaredType) superType;
            } else {
                // conflict: multi-inheritance
                return null;
            }
        }
        if (rawTheSuperType == null) {
            return null;
        }
        Map<String, TypeMapping> topLevelNameToMapping = getTopLevelNameToMapping();
        Map<String, TypeMapping> superInterfaceMapping = computeInterfaceTypeMapping(typeElement, rawTheSuperType, topLevelNameToMapping);
        TypeMapping typeMapping = createTypeMapping(superInterfaceMapping, asElement(rawTheSuperType).asType(), Collections.emptySet());
        if (typeMapping == null) {
            throw reportError("Cannot get a TypeMapping for " + rawTheSuperType);
        }
        return typeMapping;
    }

    private String getNativeMethodName(String methodName) {
        if (Character.isLowerCase(methodName.charAt(0))) {
            return "native" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
        }
        return "native_" + methodName;
    }

    RuntimeException reportMalformedIntrinsic(ExecutableElement executableElement, ExecutableType executableType, FFIIntrinsic intrinsic) {
        return reportError(executableElement, executableType, "Malformed method: Invalid method declaration for FFIIntrinsic: " + intrinsic);
    }

    private boolean generateFFIPointerIntrinsic(ExecutableDef executableDef) {
        ExecutableElement executableElement = executableDef.getExecutableElement();
        String methodName = executableElement.getSimpleName().toString();
        TypeElement declaringElement = (TypeElement) executableElement.getEnclosingElement();
        String declaringClassName = declaringElement.getQualifiedName().toString();
        ExecutableType executableType = executableDef.getExecutableType();

        FFIIntrinsic intrinsic = executableElement.getAnnotation(FFIIntrinsic.class);
        FFIGetter getter = executableElement.getAnnotation(FFIGetter.class);
        FFISetter setter = executableElement.getAnnotation(FFISetter.class);
        CXXOperator operator = executableElement.getAnnotation(CXXOperator.class);
        FFIFactory factory = executableElement.getAnnotation(FFIFactory.class);
        FFILibrary library = executableElement.getAnnotation(FFILibrary.class);
        assertAtMostOneNonNull(intrinsic, getter, setter, operator, factory, library);

        if (intrinsic != null) {
            String name = intrinsic.value();
            if (name.equals("FFIPointer.getAddress") && methodName.equals("getAddress")) {
                if (!declaringClassName.equals(FFIPointer.class.getName())
                        || executableType.getReturnType().getKind() != TypeKind.LONG
                        || !executableType.getParameterTypes().isEmpty()) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }
                // Now, we use FFIPointerImpl to provide getAddress so that we can inline getAddress.
                return true;
            } else if (name.equals("FFISettablePointer.setAddress") && methodName.equals("setAddress")) {
                if (!declaringClassName.equals(FFISettablePointer.class.getName())
                    || executableType.getReturnType().getKind() != TypeKind.VOID
                    || executableType.getParameterTypes().isEmpty() || executableType.getParameterTypes().size() != 1) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }
                String paramName = executableElement.getParameters().get(0).getSimpleName().toString();
                classBuilder.addMethod(MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                                                 .addParameter(TypeName.LONG, paramName)
                                                 .addModifiers(Modifier.PUBLIC).returns(TypeName.VOID)
                                                 .addStatement("this.address = $L", paramName).build());

                return  true;
            } else if (name.equals("CXXPointerRangeElement.add") && methodName.equals("add")) {
                if (!declaringClassName.equals(CXXPointerRangeElement.class.getName())
                        || !typeUtils().isSameType(executableType.getReturnType(), theTypeMirror)) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }
                List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
                if (parameterTypes.size() != 1 || parameterTypes.get(0).getKind() != TypeKind.LONG) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }

                String paramName = executableElement.getParameters().get(0).getSimpleName().toString();
                classBuilder.addMethod(MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                        .addParameter(TypeName.LONG, paramName)
                        .addModifiers(Modifier.PUBLIC).returns(TypeName.get(executableType.getReturnType())).
                                addStatement("return new $L(this.address + $L)", getGeneratedJavaSimpleClassName(), paramName).build());
                return true;
            } else if (name.equals("CXXPointerRangeElement.moveTo") && methodName.equals("moveTo")) {
                if (!declaringClassName.equals(CXXPointerRangeElement.class.getName())
                        || !typeUtils().isSameType(executableType.getReturnType(), theTypeMirror)) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }
                List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
                if (parameterTypes.size() != 1 || parameterTypes.get(0).getKind() != TypeKind.LONG) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }

                String paramName = executableElement.getParameters().get(0).getSimpleName().toString();
                classBuilder.addMethod(MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                        .addParameter(TypeName.LONG, paramName)
                        .addModifiers(Modifier.PUBLIC).returns(TypeName.get(executableType.getReturnType())).
                                addStatement("return new $L($L)", getGeneratedJavaSimpleClassName(), paramName).build());
                return true;
            } else if (name.equals("CXXPointerRangeElement.addV") && methodName.equals("addV")) {
                if (!declaringClassName.equals(CXXPointerRangeElement.class.getName())
                    || executableType.getReturnType().getKind() != TypeKind.VOID) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }
                List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
                if (parameterTypes.size() != 1 || parameterTypes.get(0).getKind() != TypeKind.LONG) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }

                String paramName = executableElement.getParameters().get(0).getSimpleName().toString();
                classBuilder.addMethod(MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                                                 .addParameter(TypeName.LONG, paramName)
                                                 .addModifiers(Modifier.PUBLIC)
                                                 .returns(TypeName.VOID).
                                                     addStatement("this.address += $L", paramName)
                                                 .build());
                return true;
            } else if (name.equals("CXXPointerRangeElement.moveToV") && methodName.equals("moveToV")) {
                if (!declaringClassName.equals(CXXPointerRangeElement.class.getName())
                        || executableType.getReturnType().getKind() != TypeKind.VOID) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }
                List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
                if (parameterTypes.size() != 1 || parameterTypes.get(0).getKind() != TypeKind.LONG) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }

                String paramName = executableElement.getParameters().get(0).getSimpleName().toString();
                classBuilder.addMethod(MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                        .addParameter(TypeName.LONG, paramName)
                        .addModifiers(Modifier.PUBLIC).returns(TypeName.get(executableType.getReturnType())).
                                addStatement("this.address = $L", paramName).build());
                return true;
            } else if (name.equals("CXXPointerRangeElement.elementSize") && methodName.equals("elementSize")) {
                if (!declaringClassName.equals(CXXPointerRangeElement.class.getName())
                        || executableType.getReturnType().getKind() != TypeKind.LONG) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }
                List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
                if (parameterTypes.size() != 0) {
                    throw reportMalformedIntrinsic(executableElement, executableType, intrinsic);
                }

                classBuilder.addMethod(MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.LONG).
                                addStatement("return SIZE")
                        .build());
                return true;
            } else {
                throw reportError(executableElement, executableType, "Unknown FFIIntrinsic: " + intrinsic);
            }
        } else if (factory != null) {
            classBuilder.addMethod(MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC).returns(TypeName.get(executableType.getReturnType())).
                            addStatement("return $L.INSTANCE", getGeneratedFactorySimpleClassName()).build());
            return true;
        }
        return false;
    }


    /**
     * Put the condition check of CXXValueOptimization here
     * @param executableElement
     * @return true if the return type is an FFIPointer annotated with CXXValue
     */
    protected boolean needCXXValueOptimization(ExecutableElement executableElement, ExecutableType executableType) {
        CXXValue cxxValue = executableElement.getAnnotation(CXXValue.class);
        if (cxxValue == null) {
            return false;
        }
        return isFFIPointer(executableType.getReturnType());
    }

    /**
     * Generate code that should be called in native
     * @param executableDef
     */
    private void generateFFIDelegateMethod(ExecutableDef executableDef) {
        ExecutableElement executableElement = executableDef.getExecutableElement();

        if (executableElement.isDefault()) {
            return; // already have code.
        }
        if (executableElement.isVarArgs()) {
            throw new IllegalStateException("TODO: no support of varargs in " + AnnotationProcessorUtils.format(executableElement));
        }

        if (generateFFIProperty(executableDef)) {
            return;
        }

        boolean isGenFFIPointer = isGenFFIPointer();
        boolean isGenFFILibrary = isGenFFILibrary();

        if (isGenFFIPointer && generateFFIPointerIntrinsic(executableDef)) {
            return;
        }

        String methodName = executableElement.getSimpleName().toString();
        ExecutableType executableType = executableDef.getExecutableType();
        boolean requireTypeMapping = requireCreatingTypeMapping(executableType);
        ExecutableTypeMapping executableTypeMapping = null;
        if (requireTypeMapping) {
            // get the internal type
            executableTypeMapping = getExecutableTypeMapping(executableElement);
            if (executableTypeMapping == null) {
                throw reportError(executableElement, executableType, "Missing type mapping");
            }
        }

        List<? extends VariableElement> parameterElements = executableElement.getParameters();
        boolean hasCopyToNativeParameters = parameterElements.stream().anyMatch(p -> !skipParameter(p));
        List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
        if (parameterElements.size() != parameterTypes.size()) {
            throw new IllegalStateException();
        }
        TypeMirror returnType = executableType.getReturnType();
        boolean needCXXValueOpto = needCXXValueOptimization(executableElement, executableType);
        boolean isReturnVoid = returnType.getKind() == TypeKind.VOID;
        boolean isReturnJavaLangVoid = isJavaLangVoid(returnType); // the method body can only be return null
        boolean isReturnFFIPointer = isFFIPointer(returnType);
        boolean isReturnCXXEnum = isCXXEnum(returnType);
        boolean doManualBoxing = registry.processor.manualBoxing && requireManualBoxing(returnType);
        boolean isReturnJavaPrimitive = isJavaPrimitive(tryUnboxing(returnType));
        String nativeMethodName = createUniqueNativeMethodName(methodName, executableElement);

        {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC);
            {
                // generate signature
                methodBuilder.returns(TypeName.get(returnType));
                // the method annotation is used to annotate return type
                methodBuilder.addAnnotations(executableElement.getAnnotationMirrors().stream().map(AnnotationSpec::get).collect(Collectors.toList()));
                for (int i = 0; i < parameterElements.size(); i++) {
                    VariableElement variableElement = parameterElements.get(i);
                    TypeMirror parameterType = parameterTypes.get(i);
                    if (isJavaArray(parameterType)) {
                        throw reportError(executableElement, executableType, "TODO: Java array is not supported");
                    }
                    ParameterSpec.Builder paramBuilder = ParameterSpec.builder(TypeName.get(parameterType),
                                                                               variableElement.getSimpleName()
                                                                                              .toString(),
                                                                               variableElement.getModifiers()
                                                                                              .toArray(
                                                                                                  new Modifier[0]));
                    // copy parameter annotation for llvm4jni optimizations.
                    paramBuilder.addAnnotations(variableElement.getAnnotationMirrors().stream().map(AnnotationSpec::get).collect(Collectors.toList()));
                    methodBuilder.addParameter(paramBuilder.build());
                }
            }
            if (isReturnJavaLangVoid) {
                methodBuilder.addStatement("return null");
            } else {
                // generate statement;
                StringBuilder sb = new StringBuilder();
                String returnTypeInternalTypeName = null;
                if (isReturnVoid) {
                    sb.append(nativeMethodName).append("(");
                } else if (isReturnFFIPointer) {
                    TypeMapping returnTypeMapping = executableTypeMapping.getReturnTypeMapping();
                    if (returnTypeMapping == null) {
                        throw reportError(executableElement, executableType, "Missing type mapping for return type: " + returnType);
                    }
                    if (!typeUtils().isSameType(returnType, returnTypeMapping.java)) {
                        throw reportError(executableElement, executableType, String.format("%s != %s", returnType, returnTypeMapping.java));
                    }
                    returnTypeInternalTypeName = getInternalTypeName(returnTypeMapping, executableElement);
                    // sb.append("return new ").append(returnTypeInternalTypeName).append("(").append(nativeMethodName).append("(");
                    sb.append("long ret$$ = ").append(nativeMethodName).append("(");
                } else if (isReturnCXXEnum) {
                    String refiner = getTypeRefiner(returnType);
                    if (refiner.isEmpty()) {
                        throw reportError(executableElement, executableType, "A CXXEnum needs a type refiner to convert int to Java enum.");
                    }
                    sb.append("return ").append(refiner).append("(").append(nativeMethodName).append("(");
                } else if (isReturnJavaPrimitive) {
                    if (doManualBoxing) {
                        sb.append("return new ").append(getTypeName(returnType)).append("(").append(nativeMethodName).append("(");
                    } else {
                        sb.append("return ").append(nativeMethodName).append("(");
                    }
                } else {
                    throw reportError(executableElement, executableType, "Unsupported return type: " + returnType);
                }
                if (isGenFFIPointer) {
                    if (needCXXValueOpto) {
                        String tmp = com.alibaba.fastffi.CXXValueScope.class.getName() + ".allocate("
                            + returnTypeInternalTypeName + ".SIZE)";
                        if (!hasCopyToNativeParameters) {
                            sb.append("address, ").append(tmp);
                        } else {
                            sb.append("address, ").append(tmp).append(", ");
                        }
                    } else {
                        if (!hasCopyToNativeParameters) {
                            sb.append("address");
                        } else {
                            sb.append("address, ");
                        }
                    }
                } else if (isGenFFILibrary) {
                    if (needCXXValueOpto) {
                        String tmp = com.alibaba.fastffi.CXXValueScope.class.getName() + ".allocate("
                                + returnTypeInternalTypeName + ".SIZE)";
                        if (!hasCopyToNativeParameters) {
                            sb.append(tmp);
                        } else {
                            sb.append(tmp).append(", ");
                        }
                    }
                } else {
                    throw reportError(executableElement, executableType, "Not a supported GenType: " + this.genType);
                }
                passingArgumentsToJavaNative(sb, executableElement, executableType, executableTypeMapping);
                sb.append(")");
                if (isReturnJavaLangVoid) {
                    methodBuilder.addStatement("return null");
                } else if (isReturnFFIPointer) {
                    // sb.append(")"); // additional bracket for constructor
                    String typeRefiner = getTypeRefiner(returnType);
                    VRP returnVRP = getReturnVRP(executableElement);
                    boolean nullCheck;
                    if (returnVRP == VRP.Pointer ) {
                        if (registry.processor.nullReturnValueCheck) {
                            // we must insert null check by default
                            if (isNonnull(executableElement)) {
                                // but we need to skip Nonnull return value;
                                nullCheck = false;
                            } else {
                                nullCheck = true;
                            }
                        } else {
                            // we are not necessarily to check null pointer
                            if (isNullable(executableElement)) {
                                // but we need to check on Nnullable return value;
                                nullCheck = true;
                            } else {
                                nullCheck = false;
                            }
                        }
                    } else {
                        nullCheck = false;
                    }
                    if (nullCheck) {
                        sb.append(String.format("; return %s(ret$$ == 0L ? null : new %s(ret$$))", typeRefiner, returnTypeInternalTypeName));
                    } else {
                        sb.append(String.format("; return %s(new %s(ret$$))", typeRefiner, returnTypeInternalTypeName));
                    }
                    methodBuilder.addStatement(sb.toString());
                } else if (isReturnCXXEnum) {
                    sb.append(")"); // additional bracket for refiner that converts int to Java enum
                    methodBuilder.addStatement(sb.toString());
                } else {
                    if (!isReturnJavaPrimitive) {
                        throw reportError(executableElement, executableType, "Expected primitive return type, got " + returnType);
                    }
                    if (doManualBoxing) {
                        sb.append(")"); // additional bracket for manual boxing constructor
                    }
                    methodBuilder.addStatement(sb.toString());
                }
            }
            classBuilder.addMethod(methodBuilder.build());
        }

        if (!isReturnJavaLangVoid) {
            {
                cxxWriter.append("JNIEXPORT\n");
                // generate Java native method
                TypeMirror usedInNativeReturnType = usedInNative(returnType);
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(nativeMethodName)
                        .addModifiers(Modifier.PUBLIC, Modifier.NATIVE, Modifier.STATIC)
                        .returns(TypeName.get(usedInNativeReturnType));

                // the method annotation is used to annotate return type
                methodBuilder.addAnnotations(executableElement.getAnnotationMirrors().stream().map(AnnotationSpec::get).collect(Collectors.toList()));
                cxxWriter.append(nativeType(usedInNativeReturnType)).append(" ");
                cxxWriter.append("JNICALL ");
                cxxWriter.append(encodeNativeMethodName(getGeneratedJavaClassName(), nativeMethodName, null));

                if (isGenFFIPointer) {
                    cxxWriter.append("(JNIEnv*, jclass, jlong ptr"); // additional J for the FFIPointer address
                    methodBuilder.addParameter(TypeName.LONG, "ptr");
                    if (needCXXValueOpto) {
                        cxxWriter.append(", jlong rv_base");
                        methodBuilder.addParameter(TypeName.LONG, "rv_base");
                    }
                } else if (isGenFFILibrary) {
                    cxxWriter.append("(JNIEnv*, jclass");
                    if (needCXXValueOpto) {
                        cxxWriter.append(", jlong rv_base");
                        methodBuilder.addParameter(TypeName.LONG, "rv_base");
                    }
                } else {
                    throw reportError(executableElement, executableType, "Not a supported GenType: " + this.genType);
                }

                for (int i = 0; i < parameterElements.size(); i++) {
                    VariableElement variableElement = parameterElements.get(i);
                    if (skipParameter(variableElement)) {
                        continue;
                    }
                    cxxWriter.append(", ");
                    TypeMirror parameterType = parameterTypes.get(i);

                    TypeMirror nativeParameterType = usedInNative(parameterType);
                    String paramName = variableElement.getSimpleName().toString() + i; // add a suffix to avoid conflict with the first parameter `ptr`
                    ParameterSpec.Builder paramBuilder = ParameterSpec.builder(TypeName.get(nativeParameterType), paramName);
                    // copy parameter annotation for llvm4jni optimizations.
                    if (!nativeParameterType.getKind().isPrimitive()) {
                        paramBuilder.addAnnotations(
                            variableElement.getAnnotationMirrors().stream().map(AnnotationSpec::get)
                                           .collect(Collectors.toList()));
                    }
                    methodBuilder.addParameter(paramBuilder.build());
                    cxxWriter.append(nativeType(nativeParameterType)).append(" ").append(String.format("arg%d /* %s */", i, paramName)); // do not use paramName
                }
                cxxWriter.append(") {\n");
                if (registry.processor.traceJNICalls) {
                    cxxWriter.append("\tprintf(\"fastffi: [%s][%d]: %s\\n\", __FILE__, __LINE__, __func__);\n");
                }
                FFIGetter getter = executableElement.getAnnotation(FFIGetter.class);
                FFISetter setter = executableElement.getAnnotation(FFISetter.class);
                CXXOperator operator = executableElement.getAnnotation(CXXOperator.class);
                FFIExpr ffiExpr = executableElement.getAnnotation(FFIExpr.class);
                assertAtMostOneNonNull(getter, setter, operator, ffiExpr);
                if (isGenFFIPointer) {
                    String cxxFullTypeName = getCxxFullTypeName();
                    String cxxPointer = VRP.Pointer.inNativeFromPointer("ptr", cxxFullTypeName);
                    // generate C++ implementation
                    if (operator != null) {
                        if (operator.type() == CXXOperator.Type.Expr) {
                            switch (operator.value()) {
                                case "*&": {
                                    // FFI Builtin: used as type conversion
                                    if (!parameterElements.isEmpty()) {
                                        throw reportError(executableElement, executableType, "No parameter is allowed");
                                    }
                                    String callExpr;
                                    if (isReturnFFIPointer) {
                                        VRP returnVRP = getReturnVRP(executableElement);
                                        if (returnVRP != VRP.Value) {
                                            throw reportError(executableElement, executableType, "Return type must be CXXValue");
                                        }
                                        TypeMapping returnTypeMapping = executableTypeMapping.getReturnTypeMapping();
                                        if (returnTypeMapping == null) {
                                            throw reportError(executableElement, executableType, "Missing type mapping for return type: " + returnType);
                                        }
                                        if (typeUtils().isSameType(returnType, theTypeMirror)) {
                                            callExpr = String.format("*%s", cxxPointer);
                                        } else {
                                            callExpr = String.format("(%s)(*%s)", returnTypeMapping.cxx, cxxPointer);
                                        }
                                    } else {
                                        if (!isReturnJavaPrimitive) {
                                            throw reportError(executableElement, executableType, "Expected primitive return type, got " + returnType);
                                        }
                                        callExpr = String.format("*%s", cxxPointer);
                                    }

                                    generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    break;
                                }
                                case "&*": {
                                    // FFI Builtin: used as type conversion
                                    if (!parameterElements.isEmpty()) {
                                        throw reportError(executableElement, executableType, "No parameter is allowed");
                                    }
                                    String callExpr;
                                    if (isReturnFFIPointer) {
                                        VRP returnVRP = getReturnVRP(executableElement);
                                        if (returnVRP != VRP.Pointer && returnVRP != VRP.Reference) {
                                            throw reportError(executableElement, executableType, "Return type must be either CXXReference or CXXPointer");
                                        }
                                        TypeMapping returnTypeMapping = executableTypeMapping.getReturnTypeMapping();
                                        if (returnTypeMapping == null) {
                                            throw reportError(executableElement, executableType, "Missing type mapping for return type: " + returnType);
                                        }
                                        if (typeUtils().isSameType(returnType, theTypeMirror)) {
                                            callExpr = String.format("%s", cxxPointer);
                                        } else {
                                            callExpr = String.format("((%s*)%s)", returnTypeMapping.cxx, cxxPointer);
                                        }
                                    } else {
                                        if (!isReturnJavaPrimitive) {
                                            throw reportError(executableElement, executableType, "Expected primitive return type, got " + returnType);
                                        }
                                        if (returnType.getKind() == TypeKind.LONG) {
                                            callExpr = String.format("%s", cxxPointer);
                                        } else {
                                            callExpr = String.format("(reinterpret_cast<jlong>(%s))", cxxPointer);
                                        }
                                    }
                                    generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    break;
                                }
                                case "sizeof": {
                                    if (parameterElements.isEmpty()) {
                                        String callExpr = String.format("sizeof(%s)", cxxFullTypeName);
                                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    } else {
                                        throw reportError(executableElement, executableType, "Malformed method signature for operator: " + operator);
                                    }
                                    break;
                                }
                                case "delete": {
                                    if (parameterElements.isEmpty()) {
                                        String callExpr = String.format("delete %s", cxxPointer);
                                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    } else {
                                        throw reportError(executableElement, executableType, "Malformed method signature for this operator: " + operator);
                                    }
                                    break;
                                }
                                case "*": {
                                    if (parameterElements.isEmpty()) {
                                        String callExpr = String.format("*(*%s)", cxxPointer);
                                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    } else if (parameterElements.size() == 1) {
                                        String callExpr = String.format("(*%s) * %%s", cxxPointer);
                                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    } else {
                                        throw reportError(executableElement, executableType, "Malformed method signature for this operator: " + operator);
                                    }
                                    break;
                                }
                                case "--":
                                case "++": {
                                    if (parameterElements.isEmpty()) {
                                        String callExpr = String.format("%s(*%s)", operator.value(), cxxPointer);
                                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    } else if (parameterElements.size() == 1) {
                                        String callExpr = String.format("(*%s)%s", cxxPointer, operator.value());
                                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    } else {
                                        throw reportError(executableElement, executableType, "Malformed method signature for this operator: " + operator);
                                    }
                                    break;
                                }
                                case "=": {
                                    if (parameterElements.size() == 1) {
                                        String arg0 = copyArgumentToNative(executableElement, executableType, executableTypeMapping, "arg0", 0);
                                        String callExpr = String.format("(*%s) = %s", cxxPointer, arg0);
                                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                     } else {
                                        throw reportError(executableElement, executableType, "Malformed method signature for this operator: " + operator);
                                    }
                                    break;
                                }
                                case "[]": {
                                    if (parameterElements.size() == 1) {
                                        String callExpr = String.format("(*%s)[%%s]", cxxPointer);
                                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    } else if (parameterElements.size() == 2) {
                                        String arg0 = copyArgumentToNative(executableElement, executableType, executableTypeMapping, "arg0", 0);
                                        String arg1 = copyArgumentToNative(executableElement, executableType, executableTypeMapping, "arg1", 1);
                                        String callExpr = String.format("(*%s)[%s] = %s", cxxPointer, arg0, arg1);
                                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    } else {
                                        throw reportError(executableElement, executableType, "Malformed method signature for this operator: " + operator);
                                    }
                                    break;
                                }
                                case ">=":
                                case "<=":
                                case ">":
                                case "<":
                                case "!=":
                                case "==": {
                                    if (parameterElements.size() == 1) {
                                        String callExpr = String.format("(*%s) %s (%%s)", cxxPointer, operator.value());
                                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    } else {
                                        throw reportError(executableElement, executableType, "Malformed method signature for this operator: " + operator);
                                    }
                                    break;
                                }
                                default:
                                    throw reportError(executableElement, executableType, "Operator " + operator + " is not implemented yet.");
                            }
                        } else {
                            switch (operator.value()) {
                                case "*":
                                case "++":
                                case "[]":
                                case "==":
                                case "delete":{
                                    //String callExpr = String.format("*%s == %%s", cxxPointer);
                                    String callExpr;
                                    switch (operator.type()) {
                                        case MemberFunction:
                                            callExpr = String.format("%s->%s(%%s)", cxxPointer, "operator" + operator.value());
                                            break;
                                        case NonMemberFunction:
                                            if (parameterElements.isEmpty()) {
                                                callExpr = String.format("%s(%s%%s)", cxxPointer, "operator" + operator.value());
                                            } else {
                                                callExpr = String.format("%s(%s, %%s)", cxxPointer, "operator" + operator.value());
                                            }
                                            break;
                                        default:
                                            throw reportError(executableElement, executableType, "Unknown operator type: " + operator.type());
                                    }
                                    generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                                    break;
                                }
                                default:
                                    throw reportError(executableElement, executableType, "Operator " + operator + " is not implemented yet.");
                            }
                        }
                    } else if (getter != null) {
                        if (parameterTypes.size() != 0) {
                            throw reportError(executableElement, executableType, "Malformed method: FFIGetter cannot have parameters");
                        }
                        if (isReturnVoid) {
                            throw reportError(executableElement, executableType, "Malformed method: FFIGetter cannot return void");
                        }
                        String propName = getDelegateNativeMethodName(executableElement);
                        String expr = String.format("%s->%s", cxxPointer, propName);
                        generateCallNative(executableElement, executableType, executableTypeMapping, expr);
                    } else if (setter != null) {
                        if (!isReturnVoid) {
                            throw reportError(executableElement, executableType,"Malformed method: An FFISetter can only have void return type");
                        }
                        if (parameterTypes.size() != 1) {
                            throw reportError(executableElement, executableType,"Malformed method: An FFISetter must have exactly one parameter.");
                        }
                        String propName = getDelegateNativeMethodName(executableElement);
                        String arg = copyArgumentToNative(executableElement, executableType, executableTypeMapping, "arg0", 0);
                        cxxWriter.append(String.format("\t%s->%s = %s;\n", cxxPointer, propName, arg));
                    } else if (ffiExpr != null) {
                        generateFFIExpr(ffiExpr, executableElement, executableType, executableTypeMapping);
                    } else {
                        String callExpr = String.format("%s->%s(%%s)", cxxPointer, getDelegateNativeMethodName(executableElement));
                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                    }
                } else if (isGenFFILibrary) {
                    String cxxNamespace = getFFILibraryNamespace();
                    if (operator != null) {
                        throw reportError("TODO: non-member function operator is not implemented yet for " + AnnotationProcessorUtils.format(executableElement));
                    } else if (getter != null) {
                        if (parameterTypes.size() != 0) {
                            throw reportError(executableElement, executableType, "Malformed method: FFIGetter cannot have parameters");
                        }
                        if (isReturnVoid) {
                            throw reportError(executableElement, executableType, "Malformed method: FFIGetter cannot return void");
                        }
                        String propName = getDelegateNativeMethodName(executableElement);
                        String expr = String.format("%s::%s", cxxNamespace, propName);
                        generateCallNative(executableElement, executableType, executableTypeMapping, expr);
                    } else if (setter != null) {
                        if (parameterTypes.size() != 1) {
                            throw reportError(executableElement, executableType, "Malformed method: FFISetter must have exactly one parameter");
                        }
                        if (!isReturnVoid) {
                            throw reportError(executableElement, executableType, "Malformed method: FFIGetter must return void");
                        }
                        String propName = getDelegateNativeMethodName(executableElement);
                        String arg = copyArgumentToNative(executableElement, executableType, executableTypeMapping, "arg0", 0);
                        cxxWriter.append(String.format("\t%s->%s = %s;\n", cxxNamespace, propName, arg));
                    } else if (ffiExpr != null) {
                        generateFFIExpr(ffiExpr, executableElement, executableType, executableTypeMapping);
                    } else {
                        String callExpr = String.format("%s::%s(%%s)", cxxNamespace, getDelegateNativeMethodName(executableElement));
                        generateCallNative(executableElement, executableType, executableTypeMapping, callExpr);
                    }
                } else {
                    throw reportError(executableElement, executableType, "Unsupported method");
                }

                cxxWriter.append("}\n\n");
                writeCxxFile = true;
                classBuilder.addMethod(methodBuilder.build());
            }
        }
    }

    private boolean generateFFIProperty(ExecutableDef executableDef) {
        ExecutableElement executableElement = executableDef.getExecutableElement();
        FFIProperty ffiProperty = executableElement.getAnnotation(FFIProperty.class);
        if (ffiProperty == null) {
            return false;
        }
        ExecutableType executableType = executableDef.getExecutableType();
        if (executableType.getReturnType().getKind() == TypeKind.VOID) {
            if (executableType.getParameterTypes().size() != 1) {
                throw reportError(executableElement, executableType, "Malformed FFIProperty method: void return method must have exactly one parameter," +
                        "but we got " + executableType.getParameterTypes().size());
            }
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.get(executableType.getReturnType()))
                    .addParameter(TypeName.get(executableType.getParameterTypes().get(0)), "value");
            methodBuilder.addStatement("this.$L = value", executableElement.getSimpleName().toString());
            classBuilder.addMethod(methodBuilder.build());
            return true;
        } else if (executableType.getParameterTypes().isEmpty()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.get(executableType.getReturnType()));
            methodBuilder.addStatement("return this.$L", executableElement.getSimpleName().toString());
            classBuilder.addMethod(methodBuilder.build());
            return true;
        } else {
            throw reportError(executableElement, executableType, "Not a valid FFIProperty method");
        }
    }

    private void generateFFIExpr(FFIExpr ffiExpr, ExecutableElement executableElement, ExecutableType executableType, ExecutableTypeMapping executableTypeMapping) {
        String expr = ffiExpr.value();
        if (expr.isEmpty()) {
            throw reportError(executableElement, executableType, "An FFIExpr must not be empty.");
        }
        if (expr.contains("%s")) {
            throw reportError(executableElement, executableType, "TODO: An FFIExpr must not contain `%%s'.");
        }
        if (isGenFFIPointer()) {
            String cxxFullTypeName = getCxxFullTypeName();
            String cxxPointer = VRP.Pointer.inNativeFromPointer("ptr", cxxFullTypeName);
            expr = expr.replaceAll("\\{0}", cxxPointer);
        } else if (isGenFFILibrary()) {
            String cxxNamespace = getFFILibraryNamespace();
            expr = expr.replaceAll("\\{0}", cxxNamespace);
        } else {
            throw reportError(executableElement, executableType, "TODO: oops must be bug.");
        }
        {
            List<? extends TypeParameterElement> typeParameters = theTypeElement.getTypeParameters();
            CXXTemplate template = typeDef.getCXXTemplate();
            if (template != null && !typeParameters.isEmpty()) {
                TypeMapping[] typeParameterTypeMappings = getTypeParameterTypeMapping(template, theTypeElement);
                int total = typeParameters.size();
                if (total > ('Z' - 'A' + 1)) {
                    throw reportError(executableElement, executableType, "Too many type parameters: " + total);
                }
                for (int i = 0; i < total; i++) {
                    expr = expr.replaceAll("\\{" + ((char) (i + 'A')) + "}", typeParameterTypeMappings[i].cxx);
                }
            }
        }
        {
            List<? extends TypeParameterElement> typeParameters = executableElement.getTypeParameters();
            CXXTemplate template = executableElement.getAnnotation(CXXTemplate.class);
            if (template != null && !typeParameters.isEmpty()) {
                TypeMapping[] typeParameterTypeMappings = getTypeParameterTypeMapping(template, executableElement);
                int total = typeParameters.size();
                if (total > ('z' - 'a' + 1)) {
                    throw reportError(executableElement, executableType, "Too many type parameters: " + total);
                }
                for (int i = 0; i < total; i++) {
                    expr = expr.replaceAll("\\{" + ((char) (i + 'a')) + "}", typeParameterTypeMappings[i].cxx);
                }
            }
        }
        int total = executableElement.getParameters().size();
        for (int i = 0; i < total; i++) {
            String arg = copyArgumentToNative(executableElement, executableType, executableTypeMapping, "arg" + i, i);
            expr = expr.replaceAll("\\{" + (i + 1) + "}", arg);
        }
        generateCallNative(executableElement, executableType, executableTypeMapping, expr);
    }

    private String createUniqueNativeMethodName(String methodName, ExecutableElement executableElement) {
        List<ExecutableDef> defs = this.nameToExecutables.get(executableElement.getSimpleName());
        if (defs.size() == 1) {
            return getNativeMethodName(methodName);
        } else {
            int i = 0;
            for (ExecutableDef def : defs) {
                if (def.getExecutableElement().equals(executableElement)) {
                    break;
                }
                i++;
            }
            return getNativeMethodName(methodName) + i;
        }
    }

    String decorateConstness(String cxx, boolean isConst) {
        if (isConst) {
            return "const " + cxx;
        }
        return cxx;
    }

    private String copyArgumentToNative(ExecutableElement executableElement, ExecutableType executableType,
                                        ExecutableTypeMapping executableTypeMapping, String argName, int argIndex) {
        TypeMirror argType = executableType.getParameterTypes().get(argIndex);
        VRP argVRP = getParameterVRP(executableElement, argIndex);
        boolean isConst = executableElement.getParameters().get(argIndex).getAnnotation(FFIConst.class) != null;
        if (isFFIPointer(argType)) {
            TypeMapping argTypeMapping = executableTypeMapping.getParameterTypeMapping(argIndex);
            if (argTypeMapping == null) {
                throw reportError(executableElement, executableType, "Missing type mapping for arg " + argIndex + ".");
            }
            return argVRP.inNativeFromPointer(argName, decorateConstness(argTypeMapping.cxx, isConst));
        } else if (isCXXEnum(argType)) {
            if (argVRP != VRP.Value) {
                throw reportError(executableElement, executableType, "A CXXEnum must be annotated with @CXXValue.");
            }
            return String.format("static_cast<%s>(%s)",  decorateConstness(getCXXEnumType(argType), isConst), argName);
        } else if (isJavaPrimitive(tryUnboxing(argType))) {
            String nativeType = nativeType(tryUnboxing(argType));
            if (nativeType.equals("jboolean")) {
                return "(" + argName + " == JNI_TRUE ? true : false)";
            } else {
                String nativeType2 = nativeType(tryUnboxing(argType), executableElement.getParameters().get(argIndex));
                if (!nativeType2.equals(nativeType)) {
                    return "((" + nativeType2 + ") " + argName + ")";
                }
                return argName;
            }
        } else {
            throw reportError(executableElement, executableType, "Unsupported parameter type: " + argType);
        }
    }

    private void beginHandleException() {
        if (registry.processor.handleException) {
            cxxWriter.append("\ttry {\n").append("\t");
        }
    }

    private void endHandleException(String returnStmt) {
        if (registry.processor.handleException) {
            cxxWriter.append("\t} catch (...) {\n");
            cxxWriter.append("\t\tjthrowable exception = unknownException(env);\n");
            cxxWriter.append("\t\tenv->Throw(exception);\n");
            if (returnStmt != null) {
                cxxWriter.append("\t\t").append(returnStmt).append("\n");
            }
            cxxWriter.append("\t}\n");
        }
    }

    private void generateCallNative(ExecutableElement executableElement, ExecutableType executableType,
            ExecutableTypeMapping executableTypeMapping, String callExpr) {
        TypeMirror returnType = executableType.getReturnType();
        VRP returnVRP = getReturnVRP(executableElement);
        boolean needCXXvalueOpto = needCXXValueOptimization(executableElement, executableType);
        boolean isReturnVoid = returnType.getKind() == TypeKind.VOID;
        boolean isReturnFFIPointer = isFFIPointer(returnType);
        boolean isReturnJavaPrimitive = isJavaPrimitive(tryUnboxing(returnType));
        List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
        List<? extends VariableElement> parameterElements = executableElement.getParameters();

        String retValue;
        if (callExpr.contains("%s")) {
            StringBuilder argList = new StringBuilder();
            int totalParams = parameterTypes.size();
            for (int i = 0; i < totalParams; i++) {
                VariableElement variableElement = parameterElements.get(i);
                if (skipParameter(variableElement)) {
                    continue;
                }
                if (i > 0) {
                    argList.append(", ");
                }
                argList.append(copyArgumentToNative(executableElement, executableType, executableTypeMapping, "arg" + i, i));
            }
            retValue = String.format(callExpr, argList);
        } else {
            retValue = callExpr;
        }
        if (isReturnVoid) {
            beginHandleException();
            cxxWriter.append("\t").append(retValue).append(";\n");
            endHandleException(null);
        } else if (isReturnFFIPointer) {
            beginHandleException();
            TypeMapping returnTypeMapping = executableTypeMapping.getReturnTypeMapping();
            cxxWriter.append("\treturn ").append(returnVRP.outNativeAsPointer(retValue, returnTypeMapping.cxx, needCXXvalueOpto)).append(";\n");
            endHandleException("return 0;");
        } else if (isCXXEnum(returnType)) {
            beginHandleException();
            String nativeType = nativeType(usedInNative(returnType));
            if (!nativeType.equals("jint")) {
                throw reportError("A CXXEnum must be able to convert to a jint, but we got " + nativeType);
            }
            cxxWriter.append("\treturn (").append(nativeType).append(")(").append(retValue).append(");\n");
            endHandleException("return static_cast<" + nativeType + ">(0);");
        } else if (isReturnJavaPrimitive) {
            String nativeType = nativeType(tryUnboxing(returnType));
            beginHandleException();
            if (nativeType.equals("jboolean")) {
                cxxWriter.append("\treturn (").append(retValue).append(") ? JNI_TRUE : JNI_FALSE;\n");
            } else {
                cxxWriter.append("\treturn (").append(nativeType).append(")(").append(retValue).append(");\n");
            }
            endHandleException("return static_cast<" + nativeType + ">(0);");
        } else {
            throw reportError(executableElement, executableType, "Unsupported return type: " + returnType);
        }
    }

    private ExecutableTypeMapping getExecutableTypeMapping(ExecutableElement executableElement) {
        return this.executableElementExecutableTypeMappingMap.get(executableElement);
    }

    static Comparator<Name> methodNameComparator = Comparator.comparing(Object::toString);
    static Comparator<ExecutableDef> executableDefComparator =
        Comparator.comparing((ExecutableDef o) -> o.getExecutableElement().getSimpleName().toString())
                  .thenComparing(o -> o.getExecutableType().toString());

    private void reset() {
        theTypeElement = null;
        theTypeMirror = null;
        nameToExecutables.clear();
        nameToSupertype.clear();
        generationList.clear();
        executableElementExecutableTypeMappingMap = null;
        topLevelNameToMapping = null;
    }

    /**
     * Use BFS to iterate all super interfaces
     */
    private void collectMethods() {
        LinkedList<DeclaredType> queue = new LinkedList<>();
        queue.addLast(theTypeMirror);
        Types typeUtils = processingEnv.getTypeUtils();

        while (!queue.isEmpty()) {
            DeclaredType currentTypeMirror = queue.removeFirst();
            TypeElement currentTypeElement = (TypeElement) currentTypeMirror.asElement();

            for (Element ee : currentTypeElement.getEnclosedElements()) {
                if (ee instanceof ExecutableElement) {
                    ExecutableElement executableElement = (ExecutableElement) ee;
                    if (isStatic(executableElement)) {
                        continue;
                    }
                    if (isHiddenOrOverriden(nameToExecutables, executableElement)) {
                        continue;
                    }
                    ExecutableDef def = new ExecutableDef(currentTypeElement, executableElement, currentTypeMirror, typeUtils.asMemberOf(currentTypeMirror, executableElement));
                    addToMapList(nameToExecutables, executableElement.getSimpleName(), def);
                }
            }
            if (currentTypeElement.getSuperclass().getKind() != TypeKind.NONE) {
                throw new IllegalArgumentException("We can only handle interface whose super class is always null: " + currentTypeElement.getSuperclass());
            }
            List<? extends TypeMirror> directSuperTypes = typeUtils.directSupertypes(currentTypeMirror);
            if (directSuperTypes.size() > 1) {
                for (int i = 1; i < directSuperTypes.size(); i++) {
                    DeclaredType interfaceMirror = (DeclaredType) directSuperTypes.get(i);
                    TypeElement interfaceElement = (TypeElement) interfaceMirror.asElement();
                    Name FQN = interfaceElement.getQualifiedName();
                    DeclaredType check = nameToSupertype.get(FQN);
                    if (check != null) {
                        if (!typeUtils().isSameType(interfaceMirror, check)) {
                            throw new IllegalStateException("Cannot inherit two interface with different type: got " + interfaceMirror + ", expected " + check);
                        } else {
                            continue;
                        }
                    }
                    nameToSupertype.put(FQN, interfaceMirror);
                    queue.addLast(interfaceMirror);
                }
            }
        }

        // for generate unique native name
        List<Name> methodNames = new ArrayList<>(nameToExecutables.keySet());
        methodNames.sort(methodNameComparator);
        for (Name methodName : methodNames) {
            List<ExecutableDef> executables = nameToExecutables.get(methodName);
            executables.sort(executableDefComparator);
            generationList.addAll(executables);
        }
    }

}
