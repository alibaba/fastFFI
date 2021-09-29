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
package com.alibaba.fastffi.llvm4jni;

/**
 * Only primitives are allowed
 */
public class JavaFunctionType {

    private JavaKind returnKind;

    private final JavaKind[] argumentKinds;
    private final boolean isVarargs;

    public JavaFunctionType(JavaKind returnKind, JavaKind[] argumentKinds, boolean isVarargs) {
        this.returnKind = returnKind;
        this.argumentKinds = argumentKinds;
        this.isVarargs = isVarargs;
    }

    public String toDescriptor() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (JavaKind kind : argumentKinds) {
            sb.append(LLVMToBytecode.toDescriptor(kind));
        }
        sb.append(")");
        sb.append(LLVMToBytecode.toDescriptor(returnKind));
        return sb.toString();
    }

    public String toMangledName() {
        StringBuilder sb = new StringBuilder();
        sb.append("__");
        for (JavaKind kind : argumentKinds) {
            sb.append(LLVMToBytecode.toDescriptor(kind));
        }
        sb.append("__");
        sb.append(LLVMToBytecode.toDescriptor(returnKind));
        return sb.toString();
    }

    public JavaKind[] getArgumentKinds() {
        return argumentKinds;
    }

    public JavaKind getReturnKind() {
        return returnKind;
    }

    public boolean isVarargs() {
        return isVarargs;
    }
}
