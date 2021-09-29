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
import com.alibaba.fastffi.FFITypeAlias;

import java.util.Iterator;

@FFIGen
@CXXHead("llvm/IR/DerivedTypes.h")
@FFITypeAlias("llvm::FunctionType")
public interface FunctionType extends Type {

    static boolean isa(Type type) {
        return TypeCasting.INSTANCE.isa(type, (FunctionType) null);
    }

    static FunctionType cast(Type type) {
        return TypeCasting.INSTANCE.cast(type, (FunctionType) null);
    }

    static FunctionType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (FunctionType) null);
    }

    boolean isVarArg();
    Type getReturnType();
    Type getParamType(int index);
    int getNumParams();

    default Iterable<Type> getParamTypes() {
        return new Iterable<Type>() {

            @Override
            public Iterator<Type> iterator() {

                return new Iterator<Type>() {
                    int index = 0;
                    @Override
                    public boolean hasNext() {
                        return index < getNumParams();
                    }

                    @Override
                    public Type next() {
                        return getParamType(index++);
                    }
                };
            }
        };
    }
}
