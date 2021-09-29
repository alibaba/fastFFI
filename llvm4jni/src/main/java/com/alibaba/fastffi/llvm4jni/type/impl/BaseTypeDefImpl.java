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
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class BaseTypeDefImpl implements TypeDef {

    protected final TypeDefFactoryImpl typeDefFactory;
    protected Type asmType;
    protected final Class<?> type;
    protected Map<String, Object> keyToMemberDef;
    protected TypeDef superType;
    protected final boolean isExact;
    protected TypeDef exactType;
    public BaseTypeDefImpl(TypeDefFactoryImpl typeDefFactory, boolean isExact, Class<?> type) {
        this.typeDefFactory = typeDefFactory;
        this.isExact = isExact;
        this.type = type;
    }

    public boolean isExact() {
        return isExact;
    }

    public TypeDef getExact() {
        if (isExact) {
            return this;
        }
        if (exactType != null) {
            return exactType;
        }
        exactType = createExact();
        return exactType;
    }

    protected abstract TypeDef createExact();

    public String getDescriptor() {
        return getAsmType().getDescriptor();
    }

    public String getInternalName() {
        return getAsmType().getInternalName();
    }

    @Override
    public TypeDef getComponentTypeDef() {
        return null;
    }

    private Type getAsmType() {
        if (asmType == null) {
            asmType = Type.getType(type);
        }
        return asmType;
    }

    public Constructor getConstructor(Class<?>[] parameterTypes) {
        try {
            return type.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public Method getMethod(String name, Class<?>[] parameterTypes) {
        try {
            return type.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public Field getField(String name) {
        try {
            return type.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public String toString() {
        return type.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseTypeDefImpl that = (BaseTypeDefImpl) o;
        return isExact == that.isExact && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, isExact);
    }

    @Override
    public TypeDef getArrayType() {
        TypeDef arrayType = getArrayTypeInternal();
        if (isExact()) {
            return arrayType.getExact();
        }
        return arrayType;
    }

    protected TypeDef getArrayTypeInternal() {
        return typeDefFactory.getArrayType(type);
    }

    private Map<String, Object> getKeyToMemberDefMap() {
        if (this.keyToMemberDef == null) {
            this.keyToMemberDef = new HashMap<>();
        }
        return this.keyToMemberDef;
    }

    public MethodTypeDef getMethodTypeDef(String name, String descriptor) {
        String key = name + descriptor;
        MethodTypeDef mtd = (MethodTypeDef) getKeyToMemberDefMap().get(key);
        if (mtd != null) {
            return mtd;
        }
        mtd = typeDefFactory.createMethodTypeDef(this, name, descriptor);
        this.getKeyToMemberDefMap().put(key, mtd);
        return mtd;
    }

    public FieldTypeDef getFieldTypeDef(String name, String descriptor) {
        String key = name + descriptor;
        FieldTypeDef ftd = (FieldTypeDef) getKeyToMemberDefMap().get(key);
        if (ftd != null) {
            return ftd;
        }
        ftd = typeDefFactory.createFieldTypeDef(this, name, descriptor);
        this.getKeyToMemberDefMap().put(key, ftd);
        return ftd;
    }


    @Override
    public boolean isAssignableFrom(TypeDef def) {
        if (def instanceof BaseTypeDefImpl) {
            return type.isAssignableFrom(((BaseTypeDefImpl) def).type);
        }
        throw new IllegalArgumentException("Not a type produced by the type factory.");
    }

    protected TypeDef getSuperTypeInternal() {
        if (isArray()) {
            return typeDefFactory.getJavaLangObject();
        }
        if (isPrimitive()) {
            return null;
        }
        if (isInterface()) {
            return null;
        }
        if (this == typeDefFactory.getJavaLangObject()) {
            return null;
        }
        if (superType == null) {
            superType = this.typeDefFactory.getTypeDef(type.getSuperclass());
        }
        return superType;
    }

    @Override
    public final boolean isSameType(TypeDef typeDef) {
        if (typeDef instanceof BaseTypeDefImpl) {
            return ((BaseTypeDefImpl) typeDef).type == this.type;
        }
        throw new IllegalStateException("Not implemented yet");
    }

    @Override
    public TypeDef getSuperType() {
        TypeDef superType = getSuperTypeInternal();
        if (superType == null) {
            return null;
        }
        if (isExact) {
            return superType.getExact();
        }
        return superType;
    }

    public TypeDefFactoryImpl getTypeDefFactory() {
        return this.typeDefFactory;
    }

    public String getName() {
        return type.getName();
    }

}
