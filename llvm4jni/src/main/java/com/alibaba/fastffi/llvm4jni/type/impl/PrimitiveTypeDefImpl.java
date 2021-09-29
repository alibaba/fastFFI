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

import com.alibaba.fastffi.llvm4jni.type.PrimitiveTypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDef;

public class PrimitiveTypeDefImpl extends BaseTypeDefImpl implements PrimitiveTypeDef {

    public PrimitiveTypeDefImpl(TypeDefFactoryImpl typeDefFactory, Class<?> type) {
        super(typeDefFactory, true, type);
    }

    @Override
    public final boolean isPrimitive() {
        return true;
    }

    @Override
    public final boolean isAbstract() {
        return false;
    }

    @Override
    public final boolean isInterface() {
        return false;
    }

    @Override
    public final boolean isArray() {
        return false;
    }

    @Override
    protected TypeDef createExact() {
        throw new IllegalArgumentException("Primitive type itself is exact.");
    }
}
