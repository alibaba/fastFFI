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
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeFactory;

public enum Predicate {

    // Opcode           U L G E    Intuitive operation
    FCMP_FALSE(Library.INSTANCE.FCMP_FALSE()), ///< 0 0 0 0    Always false (always folded)
    FCMP_OEQ(Library.INSTANCE.FCMP_OEQ()),   ///< 0 0 0 1    True if ordered and equal
    FCMP_OGT(Library.INSTANCE.FCMP_OGT()),   ///< 0 0 1 0    True if ordered and greater than
    FCMP_OGE(Library.INSTANCE.FCMP_OGE()),   ///< 0 0 1 1    True if ordered and greater than or equal
    FCMP_OLT(Library.INSTANCE.FCMP_OLT()),   ///< 0 1 0 0    True if ordered and less than
    FCMP_OLE(Library.INSTANCE.FCMP_OLE()),   ///< 0 1 0 1    True if ordered and less than or equal
    FCMP_ONE(Library.INSTANCE.FCMP_ONE()),   ///< 0 1 1 0    True if ordered and operands are unequal
    FCMP_ORD(Library.INSTANCE.FCMP_ORD()),   ///< 0 1 1 1    True if ordered (no nans)
    FCMP_UNO(Library.INSTANCE.FCMP_UNO()),   ///< 1 0 0 0    True if unordered: isnan(X) | isnan(Y)
    FCMP_UEQ(Library.INSTANCE.FCMP_UEQ()),   ///< 1 0 0 1    True if unordered or equal
    FCMP_UGT(Library.INSTANCE.FCMP_UGT()),  ///< 1 0 1 0    True if unordered or greater than
    FCMP_UGE(Library.INSTANCE.FCMP_UGE()),  ///< 1 0 1 1    True if unordered, greater than, or equal
    FCMP_ULT(Library.INSTANCE.FCMP_ULT()),  ///< 1 1 0 0    True if unordered or less than
    FCMP_ULE(Library.INSTANCE.FCMP_ULE()),  ///< 1 1 0 1    True if unordered, less than, or equal
    FCMP_UNE(Library.INSTANCE.FCMP_UNE()),  ///< 1 1 1 0    True if unordered or not equal
    FCMP_TRUE(Library.INSTANCE.FCMP_TRUE()), ///< 1 1 1 1    Always true (always folded)

    ICMP_EQ(Library.INSTANCE.ICMP_EQ()),   ///< equal
    ICMP_NE(Library.INSTANCE.ICMP_NE()),   ///< not equal
    ICMP_UGT(Library.INSTANCE.ICMP_UGT()),  ///< unsigned greater than
    ICMP_UGE(Library.INSTANCE.ICMP_UGE()),  ///< unsigned greater or equal
    ICMP_ULT(Library.INSTANCE.ICMP_ULT()),  ///< unsigned less than
    ICMP_ULE(Library.INSTANCE.ICMP_ULE()),  ///< unsigned less or equal
    ICMP_SGT(Library.INSTANCE.ICMP_SGT()),  ///< signed greater than
    ICMP_SGE(Library.INSTANCE.ICMP_SGE()),  ///< signed greater or equal
    ICMP_SLT(Library.INSTANCE.ICMP_SLT()),  ///< signed less than
    ICMP_SLE(Library.INSTANCE.ICMP_SLE());  ///< signed less or equal

    @FFIGen
    @FFILibrary(value = "llvm::CmpInst::Predicate", namespace = "llvm::CmpInst")
    @CXXHead("llvm/IR/InstrTypes.h")
    interface Library {

        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);

        @FFIGetter
        int FCMP_FALSE(); ///< 0 0 0 0    Always false (always folded)
        @FFIGetter
        int FCMP_OEQ();   ///< 0 0 0 1    True if ordered and equal
        @FFIGetter
        int FCMP_OGT();   ///< 0 0 1 0    True if ordered and greater than
        @FFIGetter
        int FCMP_OGE();   ///< 0 0 1 1    True if ordered and greater than or equal
        @FFIGetter
        int FCMP_OLT();   ///< 0 1 0 0    True if ordered and less than
        @FFIGetter
        int FCMP_OLE();   ///< 0 1 0 1    True if ordered and less than or equal
        @FFIGetter
        int FCMP_ONE();   ///< 0 1 1 0    True if ordered and operands are unequal
        @FFIGetter
        int FCMP_ORD();   ///< 0 1 1 1    True if ordered (no nans)
        @FFIGetter
        int FCMP_UNO();   ///< 1 0 0 0    True if unordered: isnan(X) | isnan(Y)
        @FFIGetter
        int FCMP_UEQ();   ///< 1 0 0 1    True if unordered or equal
        @FFIGetter
        int FCMP_UGT();  ///< 1 0 1 0    True if unordered or greater than
        @FFIGetter
        int FCMP_UGE();  ///< 1 0 1 1    True if unordered, greater than, or equal
        @FFIGetter
        int FCMP_ULT();  ///< 1 1 0 0    True if unordered or less than
        @FFIGetter
        int FCMP_ULE();  ///< 1 1 0 1    True if unordered, less than, or equal
        @FFIGetter
        int FCMP_UNE();  ///< 1 1 1 0    True if unordered or not equal
        @FFIGetter
        int FCMP_TRUE(); ///< 1 1 1 1    Always true (always folded)
        @FFIGetter
        int ICMP_EQ();  ///< equal
        @FFIGetter
        int ICMP_NE();  ///< not equal
        @FFIGetter
        int ICMP_UGT(); ///< unsigned greater than
        @FFIGetter
        int ICMP_UGE(); ///< unsigned greater or equal
        @FFIGetter
        int ICMP_ULT(); ///< unsigned less than
        @FFIGetter
        int ICMP_ULE(); ///< unsigned less or equal
        @FFIGetter
        int ICMP_SGT(); ///< signed greater than
        @FFIGetter
        int ICMP_SGE(); ///< signed greater or equal
        @FFIGetter
        int ICMP_SLT(); ///< signed less than
        @FFIGetter
        int ICMP_SLE(); ///< signed less or equal
    }

    static Predicate[] values = values();

    int value;
    Predicate(int value) {
        this.value = value;
    }

    public int getCppValue() {
        return this.value;
    }

    public boolean isFPPredicate() {
        return this.value >= FCMP_FALSE.value && this.value <= FCMP_TRUE.value;
    }

    public boolean isIntPredicate() {
        return this.value >= ICMP_EQ.value && this.value <= ICMP_SLE.value;
    }

    static Predicate getPredicate(int cppValue) {
        if (cppValue >= FCMP_FALSE.value && cppValue <= FCMP_TRUE.value) {
            return values[cppValue];
        }
        if (cppValue >= ICMP_EQ.value && cppValue <= ICMP_SLE.value) {
            return values[cppValue - ICMP_EQ.value + FCMP_TRUE.value + 1];
        }
        throw new IllegalArgumentException("Bad cpp value for enum Preicate: " + cppValue);
    }
}
