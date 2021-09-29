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
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFITypeAlias;

import java.util.function.Consumer;

@FFIGen
@CXXHead("llvm/IR/Function.h")
@FFITypeAlias("llvm::Function")
public interface Function extends GlobalObject {

    static boolean isa(Value value) {
        return ValueCasting.INSTANCE.isa(value, (Function) null);
    }

    static Function cast(Value value) {
        return ValueCasting.INSTANCE.cast(value, (Function) null);
    }

    static Function dyn_cast(Value value) {
        return ValueCasting.INSTANCE.dyn_cast(value, (Function) null);
    }

    Module getParent();

    FunctionType getFunctionType();

    @CXXReference
    BasicBlockList getBasicBlockList();

    default void forEachBasicBlock(Consumer<BasicBlock> consumer) {
        try (CXXValueScope scope = new CXXValueScope()) {
            Function.BasicBlockList basicBlockList = getBasicBlockList();
            for (Function.BasicBlockList.Iter it : basicBlockList) {
                consumer.accept(it.get());
            }
        }
    }

    @CXXReference
    BasicBlock getEntryBlock();

    int getCallingConv();

    long arg_size();

    default int getNumArgs() {
        return Math.toIntExact(arg_size());
    }

    Argument getArg(int index);

    boolean isIntrinsic();

    @FFIGen
    @CXXHead("llvm/IR/Function.h")
    @FFITypeAlias("llvm::Function::BasicBlockListType")
    interface BasicBlockList extends LLVMPointer, CXXValueRange<BasicBlockList.Iter> {

        @CXXValue
        BasicBlockList.Iter begin();

        @CXXValue
        BasicBlockList.Iter end();

        @FFIGen
        @CXXHead("llvm/IR/Function.h")
        @FFITypeAlias("llvm::Function::BasicBlockListType::iterator")
        interface Iter extends LLVMPointer, CXXValueRangeElement<BasicBlockList.Iter> {

            @CXXReference
            @CXXOperator("*")
            BasicBlock get();

        }
    }
}
