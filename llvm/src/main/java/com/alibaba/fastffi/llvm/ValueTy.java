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
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;

@FFITypeAlias("llvm::Value::ValueTy")
@FFITypeRefiner("com.alibaba.fastffi.llvm.ValueTy.getValueTy")
public enum ValueTy implements CXXEnum {

    FunctionVal(Library.INSTANCE.FunctionVal()),
    GlobalAliasVal(Library.INSTANCE.GlobalAliasVal()),
    GlobalIFuncVal(Library.INSTANCE.GlobalIFuncVal()),
    GlobalVariableVal(Library.INSTANCE.GlobalVariableVal()),
    BlockAddressVal(Library.INSTANCE.BlockAddressVal()),
    ConstantExprVal(Library.INSTANCE.ConstantExprVal()),
    ConstantArrayVal(Library.INSTANCE.ConstantArrayVal()),
    ConstantStructVal(Library.INSTANCE.ConstantStructVal()),
    ConstantVectorVal(Library.INSTANCE.ConstantVectorVal()),
    UndefValueVal(Library.INSTANCE.UndefValueVal()),
    ConstantAggregateZeroVal(Library.INSTANCE.ConstantAggregateZeroVal()),
    ConstantDataArrayVal(Library.INSTANCE.ConstantDataArrayVal()),
    ConstantDataVectorVal(Library.INSTANCE.ConstantDataVectorVal()),
    ConstantIntVal(Library.INSTANCE.ConstantIntVal()),
    ConstantFPVal(Library.INSTANCE.ConstantFPVal()),
    ConstantPointerNullVal(Library.INSTANCE.ConstantPointerNullVal()),
    ConstantTokenNoneVal(Library.INSTANCE.ConstantTokenNoneVal()),
    ArgumentVal(Library.INSTANCE.ArgumentVal()),
    BasicBlockVal(Library.INSTANCE.BasicBlockVal()),
    MetadataAsValueVal(Library.INSTANCE.MetadataAsValueVal()),
    InlineAsmVal(Library.INSTANCE.InlineAsmVal()),
    MemoryUseVal(Library.INSTANCE.MemoryUseVal()),
    MemoryDefVal(Library.INSTANCE.MemoryDefVal()),
    MemoryPhiVal(Library.INSTANCE.MemoryPhiVal()),
    InstructionVal(Library.INSTANCE.InstructionVal()),
    ;

    @FFIGen
    @CXXHead("llvm/IR/Value.h")
    @FFILibrary(value = "llvm::Value::ValueTy", namespace = "llvm::Value::ValueTy")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter
        int FunctionVal();
        @FFIGetter
        int GlobalAliasVal();
        @FFIGetter
        int GlobalIFuncVal();
        @FFIGetter
        int GlobalVariableVal();
        @FFIGetter
        int BlockAddressVal();
        @FFIGetter
        int ConstantExprVal();
        @FFIGetter
        int ConstantArrayVal();
        @FFIGetter
        int ConstantStructVal();
        @FFIGetter
        int ConstantVectorVal();
        @FFIGetter
        int UndefValueVal();
        @FFIGetter
        int ConstantAggregateZeroVal();
        @FFIGetter
        int ConstantDataArrayVal();
        @FFIGetter
        int ConstantDataVectorVal();
        @FFIGetter
        int ConstantIntVal();
        @FFIGetter
        int ConstantFPVal();
        @FFIGetter
        int ConstantPointerNullVal();
        @FFIGetter
        int ConstantTokenNoneVal();
        @FFIGetter
        int ArgumentVal();
        @FFIGetter
        int BasicBlockVal();
        @FFIGetter
        int MetadataAsValueVal();
        @FFIGetter
        int InlineAsmVal();
        @FFIGetter
        int MemoryUseVal();
        @FFIGetter
        int MemoryDefVal();
        @FFIGetter
        int MemoryPhiVal();
        @FFIGetter
        int InstructionVal();
    }

    int value;
    ValueTy(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    public static CXXEnumMap<ValueTy> map = new CXXEnumMap<>(values());

    public static ValueTy getValueTy(int value) {
        if (value > InstructionVal.getValue()) {
            return InstructionVal;
        }
        return map.get(value);
    }
}
