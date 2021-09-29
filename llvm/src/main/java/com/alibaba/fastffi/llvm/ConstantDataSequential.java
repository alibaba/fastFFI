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
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("llvm/IR/Constants.h")
@FFITypeAlias("llvm::ConstantDataSequential")
public interface ConstantDataSequential extends ConstantData {

    static boolean isa(Value value) {
        return ValueCasting.INSTANCE.isa(value, (ConstantDataSequential) null);
    }

    static ConstantDataSequential cast(Value value) {
        return ValueCasting.INSTANCE.cast(value, (ConstantDataSequential) null);
    }

    static ConstantDataSequential dyn_cast(Value value) {
        return ValueCasting.INSTANCE.dyn_cast(value, (ConstantDataSequential) null);
    }

    boolean isCString();
    @FFINameAlias("getAsCString")
    @CXXValue StringRef getAsCStringOrignal();
    default String getAsCString() {
        return getAsCStringOrignal().toJavaString();
    }

    boolean isString();
    @FFINameAlias("getAsString")
    @CXXValue StringRef getAsStringOrignal();
    default String getAsString() {
        return getAsStringOrignal().toJavaString();
    }

    long getElementAsInteger(int index);
    float getElementAsFloat(int index);
    double getElementAsDouble(int index);
    Constant getElementAsConstant(int index);
    Type getElementType();
    int getNumElements();
}
