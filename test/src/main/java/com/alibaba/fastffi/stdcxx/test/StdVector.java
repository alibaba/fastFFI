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
package com.alibaba.fastffi.stdcxx.test;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen(library = "ffitest")
@CXXHead(system = {"vector", "string"})
@FFITypeAlias("std::vector")
@CXXTemplate(cxx="uint32_t", java="Integer")
@CXXTemplate(cxx="int", java="Integer")
@CXXTemplate(cxx="char", java="Byte")
@CXXTemplate(cxx="std::string", java="StdString")
public interface StdVector<E> extends CXXPointer {

    @FFIFactory
    interface Factory<E> {
        StdVector<E> create();
    }

    @FFIFactory
    Factory<E> getFactory();

    long size();

    @CXXOperator("[]")
    @CXXReference E get(int index);

    @CXXReference E at(int index);

    void push_back(@CXXValue E e);

    void clear();

    long data();

    long capacity();

    void reserve(long size);

    void resize(long size);
}
