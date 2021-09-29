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

import com.alibaba.fastffi.llvm4jni.type.MethodTypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDef;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;

public class MethodTypeDefImpl implements MethodTypeDef {

    private Executable executable;
    private String descriptor;
    private TypeDef owner;
    private TypeDef[] parameterTypes;
    private TypeDef returnType;

    public MethodTypeDefImpl(Executable executable, String descriptor, TypeDef owner, TypeDef[] parameterTypes, TypeDef returnType) {
        this.executable = executable;
        this.descriptor = descriptor;
        this.owner = owner;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    @Override
    public TypeDef owner() {
        return owner;
    }

    @Override
    public String descriptor() {
        return descriptor;
    }

    @Override
    public String name() {
        if (executable instanceof Constructor) {
            return "<init>";
        }
        return executable.getName();
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(executable.getModifiers());
    }

    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(executable.getModifiers());
    }

    @Override
    public TypeDef[] parameterTypes() {
        return parameterTypes;
    }

    @Override
    public TypeDef returnType() {
        return returnType;
    }

    @Override
    public boolean isVariadic() {
        return executable.isVarArgs();
    }
}
