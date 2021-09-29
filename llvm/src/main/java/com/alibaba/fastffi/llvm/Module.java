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
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.CXXValueRange;
import com.alibaba.fastffi.CXXValueRangeElement;
import com.alibaba.fastffi.CXXValueScope;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

import java.util.function.Consumer;

@FFIGen
@CXXHead("llvm/IR/Module.h")
@FFITypeAlias("llvm::Module")
public interface Module extends LLVMPointer {

    @CXXValue StringRef getName();

    @CXXReference DataLayout getDataLayout();

    @CXXReference
    FunctionList getFunctionList();

    @CXXReference
    GlobalList getGlobalList();

    default void forEachFunction(Consumer<Function> consumer) {
        try (CXXValueScope scope = new CXXValueScope()) {
            FunctionList functionList = getFunctionList();
            for (FunctionList.Iter it : functionList) {
                consumer.accept(it.get());
            }
        }
    }

    default void forEachGlobal(Consumer<GlobalVariable> consumer) {
        try (CXXValueScope scope = new CXXValueScope()) {
            GlobalList globalList = getGlobalList();
            for (GlobalList.Iter it : globalList) {
                consumer.accept(it.get());
            }
        }
    }

    @FFIGen
    @CXXHead("llvm/IR/Module.h")
    @FFITypeAlias("llvm::Module::FunctionListType")
    interface FunctionList extends LLVMPointer, CXXValueRange<FunctionList.Iter> {

        @CXXValue
        FunctionList.Iter begin();

        @CXXValue
        FunctionList.Iter end();

        @FFIGen
        @CXXHead("llvm/IR/Module.h")
        @FFITypeAlias("llvm::Module::FunctionListType::iterator")
        interface Iter extends LLVMPointer, CXXValueRangeElement<Iter> {

            @CXXReference
            @CXXOperator("*")
            Function get();

        }
    }

    @FFIGen
    @CXXHead("llvm/IR/Module.h")
    @FFITypeAlias("llvm::Module::GlobalListType")
    interface GlobalList extends LLVMPointer, CXXValueRange<GlobalList.Iter> {

        @CXXValue
        GlobalList.Iter begin();

        @CXXValue
        GlobalList.Iter end();

        @FFIGen
        @CXXHead("llvm/IR/Module.h")
        @FFITypeAlias("llvm::Module::GlobalListType::iterator")
        interface Iter extends LLVMPointer, CXXValueRangeElement<Iter> {

            @CXXReference
            @CXXOperator("*")
            GlobalVariable get();

        }
    }
}
