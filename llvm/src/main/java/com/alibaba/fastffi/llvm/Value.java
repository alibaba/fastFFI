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
package com.alibaba.fastffi.llvm;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIJava;
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeRefiner;

@FFIGen
@CXXHead("llvm/IR/Value.h")
@CXXHead("llvm/ADT/StringRef.h")
@FFITypeAlias("llvm::Value")
@FFITypeRefiner("com.alibaba.fastffi.llvm.LLVMTypeRefiner.refine")
public interface Value extends LLVMPointer, FFIJava {

    @FFINameAlias("getName")
    @CXXValue StringRef getNameOriginal();
    default String getName() {
        return getNameOriginal().toJavaString();
    }

    default String toJavaString() {
        return String.format("%s@%s@%s", getClass().getName(), Long.toHexString(getAddress()), getName());
    }

    ValueTy getValueID();

    @FFINameAlias("use_empty")
    boolean useEmpty();

    @FFINameAlias("getType")
    Type getTypeOriginal();
    default Type getType() {
        return LLVMTypeRefiner.refine(getTypeOriginal());
    }
}
