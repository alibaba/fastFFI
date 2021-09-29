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

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;

@FFIGen
@CXXHead("llvm/IR/Instructions.h")
@FFITypeAlias("llvm::AtomicRMWInst")
public interface AtomicRMWInst extends Instruction {
    static boolean isa(Value value) {
        return ValueCasting.INSTANCE.isa(value, (AtomicRMWInst) null);
    }

    static AtomicRMWInst cast(Value value) {
        return ValueCasting.INSTANCE.cast(value, (AtomicRMWInst) null);
    }

    static AtomicRMWInst dyn_cast(Value value) {
        return ValueCasting.INSTANCE.dyn_cast(value, (AtomicRMWInst) null);
    }

    @FFIGen
    @FFILibrary(value = "llvm::AtomicRMWInst::BinOp", namespace = "llvm::AtomicRMWInst::BinOp")
    @CXXHead("llvm/IR/Instructions.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        /// *p = v
        @FFIGetter int Xchg();
        /// *p = old + v
        @FFIGetter int Add();
        /// *p = old - v
        @FFIGetter int Sub();
        /// *p = old & v
        @FFIGetter int And();
        /// *p = ~(old & v)
        @FFIGetter int Nand();
        /// *p = old | v
        @FFIGetter int Or();
        /// *p = old ^ v
        @FFIGetter int Xor();
        /// *p = old >signed v ? old : v
        @FFIGetter int Max();
        /// *p = old <signed v ? old : v
        @FFIGetter int Min();
        /// *p = old >unsigned v ? old : v
        @FFIGetter int UMax();
        /// *p = old <unsigned v ? old : v
        @FFIGetter int UMin();
        /// *p = old + v
        @FFIGetter int FAdd();
        /// *p = old - v
        @FFIGetter int FSub();
        @FFIGetter int FIRST_BINOP();
        @FFIGetter int LAST_BINOP();
        @FFIGetter int BAD_BINOP();
    }

    @FFITypeAlias("llvm::AtomicRMWInst::BinOp")
    @FFITypeRefiner("com.alibaba.fastffi.llvm.AtomicRMWInst.BinOp.get")
    enum BinOp implements CXXEnum {
        /// *p = v
        Xchg(Library.INSTANCE.Xchg()),
        /// *p = old + v
        Add(Library.INSTANCE.Add()),
        /// *p = old - v
        Sub(Library.INSTANCE.Sub()),
        /// *p = old & v
        And(Library.INSTANCE.And()),
        /// *p = ~(old & v)
        Nand(Library.INSTANCE.Nand()),
        /// *p = old | v
        Or(Library.INSTANCE.Or()),
        /// *p = old ^ v
        Xor(Library.INSTANCE.Xor()),
        /// *p = old >signed v ? old : v
        Max(Library.INSTANCE.Max()),
        /// *p = old <signed v ? old : v
        Min(Library.INSTANCE.Min()),
        /// *p = old >unsigned v ? old : v
        UMax(Library.INSTANCE.UMax()),
        /// *p = old <unsigned v ? old : v
        UMin(Library.INSTANCE.UMin()),
        /// *p = old + v
        FAdd(Library.INSTANCE.FAdd()),
        /// *p = old - v
        FSub(Library.INSTANCE.FSub()),
        FIRST_BINOP(Library.INSTANCE.FIRST_BINOP()),
        LAST_BINOP(Library.INSTANCE.LAST_BINOP()),
        BAD_BINOP(Library.INSTANCE.BAD_BINOP());

        int value;
        BinOp(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }

        static CXXEnumMap<BinOp> map = new CXXEnumMap<>(values());
        public static BinOp get(int value) {
            return map.get(value);
        }
    }

    @CXXValue BinOp getOperation();
    Value getPointerOperand();
    Value getValOperand();
    AtomicOrdering getOrdering();
    boolean isVolatile();
}
