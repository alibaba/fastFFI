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

@FFIGen
@CXXHead("llvm/IR/DerivedTypes.h")
@FFITypeAlias("llvm::PointerType")
public interface PointerType extends Type {
    static boolean isa(Type type) {
        return TypeCasting.INSTANCE.isa(type, (PointerType) null);
    }

    static PointerType cast(Type type) {
        return TypeCasting.INSTANCE.cast(type, (PointerType) null);
    }

    static PointerType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (PointerType) null);
    }
    Type getElementType();
}