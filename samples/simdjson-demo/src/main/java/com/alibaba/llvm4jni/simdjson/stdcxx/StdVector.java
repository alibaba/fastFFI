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
package com.alibaba.llvm4jni.simdjson.stdcxx;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.llvm4jni.simdjson.Tweet;

@FFIGen
@CXXHead(value = {"benchmarks.h"}, system = {"vector", "string"})
@FFITypeAlias("std::vector")
@CXXTemplate(cxx="int32_t", java="Integer")
@CXXTemplate(cxx="uint32_t", java="Integer")
@CXXTemplate(cxx="int", java="Integer")
@CXXTemplate(cxx="int64_t", java="Long")
@CXXTemplate(cxx="char", java="Byte")
@CXXTemplate(cxx="simdjson::point", java="com.alibaba.llvm4jni.simdjson.Point")
@CXXTemplate(cxx="simdjson::tweet<std::string_view>", java="com.alibaba.llvm4jni.simdjson.Tweet<com.alibaba.llvm4jni.simdjson.stdcxx.StringView>")
public interface StdVector<E> extends CXXPointer {

    Factory<Long> LONG_FACTORY = FFITypeFactory.getFactory(StdVector.class, "std::vector<int64_t>");
    Factory<Tweet<StringView>> TWEET_STRING_VIEW_FACTORY = FFITypeFactory.getFactory(StdVector.class, "std::vector<simdjson::tweet<std::string_view>>");

    @FFIFactory
    interface Factory<E> {
        StdVector<E> create();
    }

    @FFIFactory
    Factory<E> getFactory();

    long size();

    @CXXOperator("[]")
    @CXXReference E get(long index);
    @CXXOperator("[]")
    void set(long index, @CXXReference E value);
    void push_back(@CXXReference E e);
    @CXXReference E emplace_back(@CXXReference E e);

    default E append() {
        long size = size();
        long capacity = capacity();
        if (size >= capacity) {
            reserve(capacity + (capacity >> 1) + 1);
        }
        resize(size + 1);
        return get(size);
    }

    void clear();

    long data();

    long capacity();

    void reserve(long size);

    void resize(long size);
}