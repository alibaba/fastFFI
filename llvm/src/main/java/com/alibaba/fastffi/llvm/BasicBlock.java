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
@CXXHead("llvm/IR/BasicBlock.h")
@FFITypeAlias("llvm::BasicBlock")
public interface BasicBlock extends Value {

    static boolean isa(Value value) {
        return ValueCasting.INSTANCE.isa(value, (BasicBlock) null);
    }

    static BasicBlock cast(Value value) {
        return ValueCasting.INSTANCE.cast(value, (BasicBlock) null);
    }

    static BasicBlock dyn_cast(Value value) {
        return ValueCasting.INSTANCE.dyn_cast(value, (BasicBlock) null);
    }

    Function getParent();

    @CXXReference
    InstList getInstList();

    default void forEachInstruction(Consumer<Instruction> consumer) {
        try (CXXValueScope scope = new CXXValueScope()) {
            BasicBlock.InstList instList = getInstList();
            for (BasicBlock.InstList.Iter it : instList) {
                consumer.accept(it.get());
            }
        }
    }

    @FFIGen
    @CXXHead("llvm/IR/BasicBlock.h")
    @FFITypeAlias("llvm::BasicBlock::InstListType")
    interface InstList extends LLVMPointer, CXXValueRange<InstList.Iter> {

        @CXXValue
        BasicBlock.InstList.Iter begin();
        @CXXValue
        BasicBlock.InstList.Iter end();

        @FFIGen
        @CXXHead("llvm/IR/BasicBlock.h")
        @FFITypeAlias("llvm::BasicBlock::InstListType::iterator")
        interface Iter extends LLVMPointer, CXXValueRangeElement<Iter> {

            @CXXReference
            @CXXOperator("*")
            Instruction get();
        }
    }

    Instruction getTerminator();
    BasicBlock getSinglePredecessor();
    BasicBlock getSingleSuccessor();
    boolean hasNPredecessors(int N);
}
