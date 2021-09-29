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
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("llvm/IR/Instructions.h")
@FFITypeAlias("llvm::AllocaInst")
public interface AllocaInst extends UnaryInstruction {

    static boolean isa(Value value) {
        return ValueCasting.INSTANCE.isa(value, (AllocaInst) null);
    }

    static AllocaInst cast(Value value) {
        return ValueCasting.INSTANCE.cast(value, (AllocaInst) null);
    }

    static AllocaInst dyn_cast(Value value) {
        return ValueCasting.INSTANCE.dyn_cast(value, (AllocaInst) null);
    }

    boolean isStaticAlloca();
    int getAlignment();

    @CXXValue
    @FFITypeAlias("llvm::Optional<uint64_t>")
    Optional<Long> getAllocationSizeInBits(@CXXReference DataLayout dataLayout);
}
