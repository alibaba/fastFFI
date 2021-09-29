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

import com.alibaba.fastffi.llvm4jni.type.ArrayTypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDef;

public class ArrayTypeDefImpl extends BaseTypeDefImpl implements ArrayTypeDef {

    public ArrayTypeDefImpl(TypeDefFactoryImpl typeDefFactory, Class<?> type) {
        this(typeDefFactory, false, type);
    }

    public ArrayTypeDefImpl(TypeDefFactoryImpl typeDefFactory, boolean exact, Class<?> type) {
        super(typeDefFactory, exact, type);
    }

    @Override
    protected TypeDef createExact() {
        return new ArrayTypeDefImpl(typeDefFactory, true, type);
    }

    /**
     * An exact array type does not mean its elements are all of exact types.
     * @return
     */
    protected TypeDef getComponentTypeDefInternal() {
        return getTypeDefFactory().getTypeDef(type.getComponentType());
    }

    @Override
    public TypeDef getComponentTypeDef() {
        return getComponentTypeDefInternal();
    }

    @Override
    public final boolean isAbstract() {
        return false;
    }

    @Override
    public final boolean isPrimitive() {
        return false;
    }

    @Override
    public final boolean isArray() {
        return true;
    }

    @Override
    public final boolean isInterface() {
        return false;
    }
}
