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
package com.alibaba.fastffi.llvm4jni.type.impl;

import com.alibaba.fastffi.llvm4jni.type.FieldTypeDef;
import com.alibaba.fastffi.llvm4jni.type.MethodTypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDefException;
import com.alibaba.fastffi.llvm4jni.type.TypeDefFactory;
import org.objectweb.asm.Type;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TypeDefFactoryImpl implements TypeDefFactory {

    public static TypeDefFactory create(Path[] classpath) {
        URLClassLoader typeLoader = new URLClassLoader(Arrays.stream(classpath).map(c -> {
            try {
                return c.toUri().toURL();
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Malformed URL ", e);
            }
        }).toArray(URL[]::new), URLClassLoader.getSystemClassLoader());
        return new TypeDefFactoryImpl(typeLoader);
    }

    ClassLoader typeLoader;
    Map<Class<?>, TypeDef> typeToTypeDef;

    TypeDef javaLangObject;
    TypeDef javaLangClass;
    TypeDef javaLangString;
    TypeDef javaLangThrowable;

    public TypeDefFactoryImpl(ClassLoader typeLoader) {
        this.typeLoader = typeLoader;
        this.typeToTypeDef = new HashMap<>();
        initializeWellKnownClasses();
    }

    private void initializeWellKnownClasses() {
        javaLangClass = getTypeDef(Class.class);
        javaLangObject = getTypeDef(Object.class);
        javaLangString = getTypeDef(String.class);
        javaLangThrowable = getTypeDef(Throwable.class);
    }

    @Override
    public TypeDef getTypeDef(String descriptor) {
        return getTypeDef(getType(descriptor));
    }

    public MethodTypeDef createMethodTypeDef(TypeDef owner, String name, String descriptor) {
        if (owner instanceof BaseTypeDefImpl) {
            TypeDef returnType = getTypeDef(getType(Type.getReturnType(descriptor)));
            Class<?>[] parameterTypes = Arrays.stream(Type.getArgumentTypes(descriptor)).map(
                    s -> getType(s)
            ).toArray(Class[]::new);
            Executable executable;
            if (name.equals("<init>")) {
                executable = ((BaseTypeDefImpl) owner).getConstructor(parameterTypes);
                if (executable == null) {
                    throw new TypeDefException("No such constructor " + name + descriptor + " in " + owner);
                }
            } else {
                executable = ((BaseTypeDefImpl) owner).getMethod(name, parameterTypes);
                if (executable == null) {
                    throw new TypeDefException("No such method " + name + descriptor + " in " + owner);
                }
            }
            return new MethodTypeDefImpl(executable, descriptor, owner,
                    Arrays.stream(parameterTypes).map(s -> getTypeDef(s)).toArray(TypeDef[]::new),
                    returnType);
        }
        throw new IllegalArgumentException("Unsupported TypeDef: " + owner);
    }

    public FieldTypeDef createFieldTypeDef(TypeDef owner, String name, String descriptor) {
        if (owner instanceof BaseTypeDefImpl) {
            TypeDef type = getTypeDef(getType(descriptor));
            Field field = ((BaseTypeDefImpl) owner).getField(name);
            if (field == null) {
                throw new TypeDefException("No such field " + name + " " + descriptor + " in " + owner);
            }
            return new FieldTypeDefImpl(field, descriptor, owner, type);
        }
        throw new IllegalArgumentException("Unsupported TypeDef: " + owner);
    }

    @Override
    public TypeDef getJavaLangObject() {
        return javaLangObject;
    }

    @Override
    public TypeDef getJavaLangString() {
        return javaLangString;
    }

    @Override
    public TypeDef getJavaLangClass() {
        return javaLangClass;
    }

    @Override
    public TypeDef getJavaLangThrowable() {
        return javaLangThrowable;
    }

    /**
     * return the inexact version of the given type.
     * @param type
     * @return
     */
    public TypeDef getTypeDef(Class<?> type) {
        if (type == null) {
            return null;
        }
        TypeDef check = this.typeToTypeDef.get(type);
        if (check != null) {
            return check;
        }
        if (type.isPrimitive()) {
            check = new PrimitiveTypeDefImpl(this, type);
        } else if (type.isArray()) {
            check = new ArrayTypeDefImpl(this, type);
        } else {
            check = new ClassTypeDefImpl(this, type);
        }
        this.typeToTypeDef.put(type, check);
        return check;
    }

    public TypeDef getArrayType(Class<?> componentType) {
        Type type = Type.getType(componentType);
        return getTypeDef("[" + type.getDescriptor());
    }

    private Class<?> loadClass(Type type) {
        String name = type.getSort() == Type.OBJECT ? type.getClassName() : type.getInternalName().replace('/', '.');
        try {
            return Class.forName(name, false, typeLoader);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            throw new TypeDefException("Cannot load " + name);
        }
    }

    public Class<?> getType(String descriptor) {
        return getType(Type.getType(descriptor));
    }

    public Class<?> getType(Type type) {
        switch (type.getSort()) {
            case Type.VOID:
                return void.class;
            case Type.BOOLEAN:
                return boolean.class;
            case Type.BYTE:
                return byte.class;
            case Type.CHAR:
                return char.class;
            case Type.SHORT:
                return short.class;
            case Type.INT:
                return int.class;
            case Type.FLOAT:
                return float.class;
            case Type.LONG:
                return long.class;
            case Type.DOUBLE:
                return double.class;
            case Type.ARRAY:
            case Type.OBJECT:
                return loadClass(type);
        }
        return null;
    }
}
