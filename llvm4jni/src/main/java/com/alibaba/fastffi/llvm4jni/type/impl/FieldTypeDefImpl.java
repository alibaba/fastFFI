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
import com.alibaba.fastffi.llvm4jni.type.TypeDef;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldTypeDefImpl implements FieldTypeDef {

    private Field field;
    private String descriptor;
    private TypeDef owner;
    private TypeDef type;

    public FieldTypeDefImpl(Field field, String descriptor, TypeDef owner, TypeDef type) {
        this.field = field;
        this.owner = owner;
        this.descriptor = descriptor;
        this.type = type;
    }

    @Override
    public TypeDef owner() {
        return owner;
    }

    @Override
    public String name() {
        return field.getName();
    }

    @Override
    public String descriptor() {
        return descriptor;
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(field.getModifiers());
    }

    @Override
    public TypeDef type() {
        return type;
    }
}
