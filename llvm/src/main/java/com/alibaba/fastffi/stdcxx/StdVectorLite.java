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
package com.alibaba.fastffi.stdcxx;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.LLVMPointer;

import java.util.Iterator;

@FFIGen
@CXXHead(system = {"vector", "string", "memory"})
@CXXHead("clang/Frontend/ASTUnit.h")
@FFITypeAlias("std::vector")
@CXXTemplate(cxx="std::string", java="StdString")
@CXXTemplate(cxx="std::unique_ptr<clang::ASTUnit>", java="UniquePtr<com.alibaba.fastffi.clang.ASTUnit>")
public interface StdVectorLite<E> extends CXXPointer, LLVMPointer, Iterable<E> {

    default Iterator<E> iterator() {
        return new Iterator<E>() {
            int cur = 0;
            int size = Math.toIntExact(size());
            @Override
            public boolean hasNext() {
                return cur < size;
            }

            @Override
            public E next() {
                return get(cur++);
            }
        };
    }

    @FFIFactory
    interface Factory<E> {
        StdVectorLite<E> create();
    }

    @FFIFactory
    Factory<E> getFactory();

    long size();

    @CXXOperator("[]")
    @CXXReference E get(int index);
}
