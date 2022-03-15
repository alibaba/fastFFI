package com.alibaba.fastffi.tool;

import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.clang.Decl;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

import javax.lang.model.element.Modifier;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.WildcardType;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TypeGen {
    enum Status {
        None,
        Succ,
        Fail;
    }

    final ClassName className;
    final TypeSpec.Builder builder;
    final Decl decl;  // might be null, e.g., when generating oracles for int*, etc.
    TypeSpec typeSpec;
    Status status;

    private final String fileComment;

    TypeGen enclosingTypeGen;
    List<TypeGen> enclosedTypeGenList;

    private TypeSpec.Builder factoryBuilder;
    private TypeSpec.Builder libraryBuilder;

    private Set<String> methods;

    TypeGen(ClassName className, TypeSpec.Builder builder, Decl decl, String fileComment) {
        this.className = className;
        this.builder = builder;
        this.decl = decl;
        this.status = Status.None;

        this.fileComment = fileComment;

        this.methods = new TreeSet<>();
    }

    @Override
    public String toString() {
        return "TypeGen <" + className + ">";
    }

    public Set<String> getMethods() {
        return methods;
    }

    public boolean hasMethod(final String name, final String tag) {
        return methods.contains(name + tag);
    }

    public boolean addMethod(final String name, final String tag) {
        return methods.add(name + tag);
    }

    public boolean addMethod(final String method) {
        return methods.add(method);
    }

    public void addMethods(Collection<String> methods) {
        this.methods.addAll(methods);
    }

    public String generateMethodTag(List<ParameterSpec> parameters) {
        StringBuilder builder = new StringBuilder();
        for (ParameterSpec parameter: parameters) {
            builder.append("|");
            generateTypeTag(builder, parameter.type);
        }
        return builder.toString();
    }

    private void generateTypeTag(final StringBuilder builder, TypeName type) {
        if (type.isPrimitive()) {
            builder.append(type);
        } if (type instanceof ArrayTypeName) {
            builder.append("[");
            generateTypeTag(builder, ((ArrayTypeName) type).componentType);
            builder.append("]");
        } else if (type instanceof ClassName) {
            builder.append(((ClassName) type));
        } else if (type instanceof ParameterizedTypeName) {
            builder.append(((ParameterizedTypeName) type).rawType);
        } else if (type instanceof TypeVariableName) {
            builder.append("?O");
        } else if (type instanceof WildcardTypeName) {
            builder.append("?");
        }
    }

    List<TypeGen> getEnclosedTypeGenList() {
        if (enclosedTypeGenList == null) {
            return Collections.emptyList();
        }
        return enclosedTypeGenList;
    }

    void addEnclosedTypeGen(TypeGen typeGen) {
        if (enclosedTypeGenList == null) {
            enclosedTypeGenList = new ArrayList<>(3);
        }
        if (enclosedTypeGenList.contains(typeGen)) {
            throw new IllegalStateException(
                    String.format("Oops: already exists: %s, enclosed: %s",
                            typeGen.className, enclosedTypeGenList));
        }
        if (typeGen.enclosingTypeGen != null) {
            throw new IllegalStateException(
                    String.format("Oops: enclosing not null: %s, %s, enclosing: %s",
                            className, typeGen.className, typeGen.enclosingTypeGen));
        }
        typeGen.enclosingTypeGen = this;
        enclosedTypeGenList.add(typeGen);
    }

    void addHeader(AnnotationSpec spec) {
        builder.addAnnotation(spec);
        if (factoryBuilder != null) {
            factoryBuilder.addAnnotation(spec);
        }
        if (libraryBuilder != null) {
            libraryBuilder.addAnnotation(spec);
        }
    }

    TypeGen getEnclosingTypeGen() {
        return enclosingTypeGen;
    }

    boolean isSucc() {
        return status == Status.Succ;
    }

    boolean isFail() {
        return status == Status.Fail;
    }

    void fail(String reason) {
        Logger.info("Mark failed: %s@%d, reason=%s", className, hashCode(), reason);
        if (status != Status.None) {
            throw new IllegalStateException("Oops, expected None, got " + status);
        }
        status = Status.Fail;
    }

    void succ() {
        Logger.info("Mark successful: %s@%d", className, hashCode());
        if (status != Status.None) {
            throw new IllegalStateException("Oops, expected None, got " + status);
        }
        status = Status.Succ;
    }

    void build(Path output) throws IOException
    {
        Logger.info("Build %s@%d", className, hashCode());
        if (!isSucc()) {
            throw new IllegalStateException("Oops, expected Succ, got " + status);
        }
        if (typeSpec != null) {
            throw new IllegalStateException("Cannot build twice " + className);
        }
        this.generateConstructors();
        if (factoryBuilder != null) {
            builder.addType(factoryBuilder.build());
        }
        if (libraryBuilder != null) {
            builder.addType(libraryBuilder.build());
        }
        processEnclosedTypes();
        typeSpec = builder.build();

        if (getEnclosingTypeGen() == null) {
            JavaFile javaFile = JavaFile
                    .builder(className.packageName(), typeSpec)
                    .addFileComment(fileComment)
                    .indent("    ")
                    .build();
            if (Logger.verbose()) {
                System.out.println(javaFile);
            }
            javaFile.writeTo(output);
        }
    }

    void processEnclosedTypes() {
        if (isFail()) {
            return;
        }
        if (!isSucc()) {
            throw new IllegalStateException("Oops, expected ongoing, got " + status);
        }
        if (enclosedTypeGenList != null) {
            for (TypeGen child : enclosedTypeGenList) {
                if (child.isSucc()) {
                    if (child.typeSpec == null) {
                        throw new NullPointerException();
                    }
                    builder.addType(child.typeSpec);
                }
            }
        }
    }

    public boolean isNone() {
        return status == Status.None;
    }

    public TypeSpec.Builder getFactoryBuilder() {
        if (factoryBuilder == null) {
            ClassName factoryClassName = className.nestedClass("Factory");
            factoryBuilder = TypeSpec.interfaceBuilder(factoryClassName).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
            factoryBuilder.addAnnotation(FFIFactory.class);
        }
        return factoryBuilder;
    }

    public TypeSpec.Builder getFactoryBuilderOrNull() {
        return factoryBuilder;
    }

    public TypeSpec.Builder getLibraryBuilder(String cxxName) {
        if (libraryBuilder == null) {
            ClassName libraryClassName = className.nestedClass("Library");
            libraryBuilder = TypeSpec.interfaceBuilder(libraryClassName).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
            libraryBuilder.addAnnotation(FFIGen.class);
            libraryBuilder.addAnnotation(AnnotationSpec.builder(FFILibrary.class)
                    .addMember("value", "$S", cxxName)
                    .addMember("namespace", "$S", cxxName)
                    .build());
            libraryBuilder.addField(FieldSpec.builder(libraryClassName, "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$T.getLibrary($T.class)", FFITypeFactory.class, libraryClassName)
                    .build());
        }
        return libraryBuilder;
    }

    public TypeSpec.Builder getLibraryBuilderOrNull() {
        return libraryBuilder;
    }

    private String makeTypeVariableVar(TypeVariableName tv) {
        return "__" + tv.name.toLowerCase();
    }

    private List<ParameterSpec> makeTypeVariableParameters() {
        return builder.typeVariables
                .stream()
                .map(tv -> ParameterSpec.builder(
                        ParameterizedTypeName.get(ClassName.get(Class.class), tv),
                        this.makeTypeVariableVar(tv)).build())
                .collect(Collectors.toList());
    }

    private String makeTypeVariableArguments() {
        return builder.typeVariables.stream().map(this::makeTypeVariableVar).collect(Collectors.joining(", "));
    }

    private void makeGetFFIType(final MethodSpec.Builder methBuilder, final TypeName returnType, final String typeVariableArguments) {
        if (builder.typeVariables.isEmpty()) {
            methBuilder.addStatement("$T<$T> clz = ($T<$T>) $T.getType($T.getFFITypeName($T.class, true))",
                    Class.class, returnType, Class.class, returnType, FFITypeFactory.class, FFITypeFactory.class, className);
        } else {
            methBuilder.addStatement("$T<$T> clz = ($T<$T>) $T.getType($T.getFFITypeName($T.makeParameterizedType($T.class, $L), true))",
                    Class.class, returnType, Class.class, returnType, FFITypeFactory.class, FFITypeFactory.class, FFITypeFactory.class, className, typeVariableArguments);
        }
    }

    private void makeGetAndReturnFFIFactory(final MethodSpec.Builder methBuilder, final String typeVariableArguments) {
        if (builder.typeVariables.isEmpty()) {
            methBuilder.addStatement("return $T.getFactory($T.getFFITypeName($T.class, true))",
                    FFITypeFactory.class, FFITypeFactory.class, className);
        } else {
            methBuilder.addStatement("return $T.getFactory($T.getFFITypeName($T.makeParameterizedType($T.class, $L), true))",
                    FFITypeFactory.class, FFITypeFactory.class, FFITypeFactory.class, className, typeVariableArguments);
        }
    }

    public void generateConstructors() {
        TypeName returnType = className;
        if (!builder.typeVariables.isEmpty()) {
            returnType = ParameterizedTypeName.get(className, builder.typeVariables.toArray(new TypeName[0]));
        }
        List<ParameterSpec> typeVariableParameters = makeTypeVariableParameters();
        String typeVariableArguments = makeTypeVariableArguments();
        {
            // T(long address)
            MethodSpec.Builder methBuilder = MethodSpec.methodBuilder("cast")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addTypeVariables(builder.typeVariables)
                    .returns(returnType)
                    .addParameters(typeVariableParameters)
                    .addParameter(ParameterSpec.builder(TypeName.LONG, "__foreign_address", Modifier.FINAL).build())
                    .beginControlFlow("try");
            makeGetFFIType(methBuilder, returnType, typeVariableArguments);
            methBuilder.addStatement("return clz.getConstructor($T.TYPE).newInstance(__foreign_address)", Long.class)
                    .nextControlFlow("catch ($T | $T | $T | $T | $T e)",
                            ClassNotFoundException.class,
                            NoSuchMethodException.class,
                            InvocationTargetException.class,
                            InstantiationException.class,
                            IllegalAccessException.class)
                    .addStatement("return null")
                    .endControlFlow();
            builder.addMethod(methBuilder.build());
        }
        {
            // T(FFIPointer pointer)
            MethodSpec.Builder methBuilder = MethodSpec.methodBuilder("cast")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addTypeVariables(builder.typeVariables)
                    .returns(returnType)
                    .addParameters(typeVariableParameters)
                    .addParameter(ParameterSpec.builder(ClassName.get(FFIPointer.class), "__foreign_pointer", Modifier.FINAL).build());
            if (builder.typeVariables.isEmpty()) {
                methBuilder.addStatement("return $T.cast(__foreign_pointer.getAddress())", className);
            } else {
                methBuilder.addStatement("return $T.cast($L, __foreign_pointer.getAddress())", className, typeVariableArguments);
            }
            builder.addMethod(methBuilder.build());
        }
        // generate a "create" methods on the class itself, (template class is not supported)
        if (factoryBuilder != null) {
            TypeName factoryType = className.nestedClass("Factory");
            if (!builder.typeVariables.isEmpty()) {
                factoryType = ParameterizedTypeName.get(className.nestedClass("Factory"), builder.typeVariables.toArray(new TypeName[0]));
            }
            // generate a `getFactory` method
            MethodSpec.Builder methBuilder = MethodSpec.methodBuilder("getFactory")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .addTypeVariables(builder.typeVariables)
                            .returns(factoryType)
                            .addParameters(typeVariableParameters);
            makeGetAndReturnFFIFactory(methBuilder, typeVariableArguments);
            builder.addMethod(methBuilder.build());
            // add constructors
            for (MethodSpec methodSpec: factoryBuilder.methodSpecs) {
                if (!methodSpec.name.equals("create")) {
                    continue;
                }
                String ctorArguments = methodSpec.parameters
                        .stream()
                        .map(param -> param.name)
                        .collect(Collectors.joining(", "));
                builder.addMethod(MethodSpec.methodBuilder(methodSpec.name)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addTypeVariables(builder.typeVariables)
                        .returns(methodSpec.returnType)
                        .addParameters(typeVariableParameters)
                        .addParameters(methodSpec.parameters)
                        .addStatement("return $T.getFactory($L).create($L)",
                                className, typeVariableArguments, ctorArguments)
                        .build());
            }
        }
    }
}
