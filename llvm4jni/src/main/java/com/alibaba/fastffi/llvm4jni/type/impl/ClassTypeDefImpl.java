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

import com.alibaba.fastffi.llvm4jni.type.ClassTypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDef;

import java.lang.reflect.Modifier;

public class ClassTypeDefImpl extends BaseTypeDefImpl implements ClassTypeDef {

    public ClassTypeDefImpl(TypeDefFactoryImpl typeDefFactory, Class<?> type) {
        this(typeDefFactory, false, type);
    }

    public ClassTypeDefImpl(TypeDefFactoryImpl typeDefFactory, boolean exact, Class<?> type) {
        super(typeDefFactory, exact, type);
    }

    @Override
    protected TypeDef createExact() {
        return new ClassTypeDefImpl(this.typeDefFactory, true, type);
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(type.getModifiers());
    }

    public boolean isInterface() {
        return type.isInterface();
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }
}
