package com.alibaba.fastffi.tool;

import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.FFIStringProvider;
import com.alibaba.fastffi.FFIStringReceiver;
import com.alibaba.fastffi.impl.CharPointer;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public class BuiltinElaborator
{
    private String rootPackage;

    public BuiltinElaborator(String rootPackage) {
        if (rootPackage != null && !rootPackage.isEmpty()) {
            this.rootPackage = rootPackage + ".";
        }
    }

    public void elaborate(TypeGen typeGen) {
        switch (stripRootPackage(typeGen.className.canonicalName())) {
            case "std.string": {
                elaborateStdString(typeGen);
            }
        }
    }

    private void elaborateStdString(TypeGen typeGen) {
        TypeSpec.Builder classBuilder = typeGen.builder;
        TypeSpec.Builder factoryClassBuilder = typeGen.getFactoryBuilder();

        // from/to java string
        typeGen.builder.addSuperinterface(ClassName.get(FFIStringProvider.class));
        typeGen.builder.addSuperinterface(ClassName.get(FFIStringReceiver.class));

        // std::string()
        {
            factoryClassBuilder.addMethod(MethodSpec.methodBuilder("create")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(typeGen.className)
                    .build());
        }
        // std::string(const char *)
        {
            factoryClassBuilder.addMethod(MethodSpec.methodBuilder("create")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(typeGen.className)
                    .addParameter(ParameterSpec.builder(ClassName.get(CharPointer.class), "buffer").build())
                    .build());
        }
        // std::string(const char *, size_t length)
        {
            factoryClassBuilder.addMethod(MethodSpec.methodBuilder("create")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(typeGen.className)
                    .addParameter(ParameterSpec.builder(ClassName.get(CharPointer.class), "buffer").build())
                    .addParameter(ParameterSpec.builder(TypeName.LONG, "length").build())
                    .build());
        }
        // std::string(std::string const &)
        {
            factoryClassBuilder.addMethod(MethodSpec.methodBuilder("create")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(typeGen.className)
                    .addParameter(ParameterSpec.builder(typeGen.className, "other").addAnnotation(CXXReference.class).build())
                    .build());
        }
        // std::string(java.lang.String)
        {
            factoryClassBuilder.addMethod(MethodSpec.methodBuilder("create")
                    .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                    .returns(typeGen.className)
                    .addParameter(ParameterSpec.builder(ClassName.get(String.class), "str").build())
                    .addStatement("$T s = create()", typeGen.className)
                    .addStatement("s.fromJavaString(str)")
                    .addStatement("return s")
                    .build());
        }

        // size()
        {
            classBuilder.addMethod(MethodSpec.methodBuilder("size")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(TypeName.LONG)
                    .build());
        }
        // data
        {
            classBuilder.addMethod(MethodSpec.methodBuilder("data")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(TypeName.LONG)
                    .build());
        }
    }

    private String stripRootPackage(final String className) {
        if (className == null || rootPackage == null || className.isEmpty() || rootPackage.isEmpty()) {
            return className;
        }
        if (className.startsWith(rootPackage)){
            return className.substring(rootPackage.length());
        }
        return className;
    }
}
