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

@FFITypeAlias("llvm::AtomicOrdering")
@FFITypeRefiner("com.alibaba.fastffi.llvm.AtomicOrdering.getAtomicOrdering")
public enum AtomicOrdering implements CXXEnum {
    NotAtomic(Library.INSTANCE.NotAtomic()),
    Unordered(Library.INSTANCE.Unordered()),
    Monotonic(Library.INSTANCE.Monotonic()), // Equivalent to C++'s relaxed.
    // Consume = 3,  // Not specified yet.
    Acquire(Library.INSTANCE.Acquire()),
    Release(Library.INSTANCE.Release()),
    AcquireRelease(Library.INSTANCE.AcquireRelease()),
    SequentiallyConsistent(Library.INSTANCE.SequentiallyConsistent()),
    ;

    @FFIGen
    @CXXHead("llvm/Support/AtomicOrdering.h")
    @FFILibrary(value = "llmv::AtomicOrdering", namespace = "llvm::AtomicOrdering")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter
        int NotAtomic();
        @FFIGetter
        int Unordered();
        @FFIGetter
        int Monotonic(); // Equivalent to C++'s relaxed.
        // Consume = 3,  // Not specified yet.
        @FFIGetter
        int Acquire();
        @FFIGetter
        int Release();
        @FFIGetter
        int AcquireRelease();
        @FFIGetter
        int SequentiallyConsistent();
    }

    int value;
    AtomicOrdering(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    public static CXXEnumMap<AtomicOrdering> map = new CXXEnumMap<>(values());
    public static AtomicOrdering getAtomicOrdering(int value) {
        return map.get(value);
    }
}
