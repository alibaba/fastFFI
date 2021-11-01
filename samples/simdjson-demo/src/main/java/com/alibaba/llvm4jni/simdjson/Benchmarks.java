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
package com.alibaba.llvm4jni.simdjson;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.llvm4jni.simdjson.document.SimdElement;
import com.alibaba.llvm4jni.simdjson.document.SimdPaddedString;
import com.alibaba.llvm4jni.simdjson.document.SimdParser;
import com.alibaba.llvm4jni.simdjson.document.SimdResult;
import com.alibaba.llvm4jni.simdjson.stdcxx.StdVector;
import com.alibaba.llvm4jni.simdjson.stdcxx.StringView;

@FFIGen
@FFILibrary(value = "simdjson::Benchmarks", namespace = "simdjson::Benchmarks")
@CXXHead("benchmarks.h")
public interface Benchmarks {
    Benchmarks INSTANCE = FFITypeFactory.getLibrary(Benchmarks.class);
    void distinctuserid_recursive(@CXXReference SimdResult<SimdElement> dom, @CXXReference @FFITypeAlias("std::vector<int64_t>") StdVector<Long> answer);

    void distinctuserid(@CXXReference SimdResult<SimdElement> dom, @CXXReference @FFITypeAlias("std::vector<int64_t>") StdVector<Long> answer);
    void distinctuserid(@CXXReference SimdPaddedString content, @CXXReference @FFITypeAlias("std::vector<int64_t>") StdVector<Long> answer);
    void distinctuserid(@CXXReference SimdParser parser, @CXXReference SimdPaddedString content, @CXXReference @FFITypeAlias("std::vector<int64_t>") StdVector<Long> answer);

    void kostya(@CXXReference SimdResult<SimdElement> dom, @CXXReference StdVector<Point> result);
    void kostya(@CXXReference SimdPaddedString content, @CXXReference StdVector<Point> result);
    void kostya(@CXXReference SimdParser parser, @CXXReference SimdPaddedString content, @CXXReference StdVector<Point> result);

    void top_tweet(@CXXReference SimdResult<SimdElement> dom, long max_retweet_count, @CXXReference TopTweetResult<StringView> result);
    void top_tweet(@CXXReference SimdPaddedString content, long max_retweet_count, @CXXReference TopTweetResult<StringView> result);
    void top_tweet(@CXXReference SimdParser parser, @CXXReference SimdPaddedString content, long max_retweet_count, @CXXReference TopTweetResult<StringView> result);

    void partial_tweets(@CXXReference SimdResult<SimdElement> dom, @CXXReference StdVector<Tweet<StringView>> result);
    void partial_tweets(@CXXReference SimdPaddedString content, @CXXReference StdVector<Tweet<StringView>> result);
    void partial_tweets(@CXXReference SimdParser parser, @CXXReference SimdPaddedString content, @CXXReference StdVector<Tweet<StringView>> result);
}
