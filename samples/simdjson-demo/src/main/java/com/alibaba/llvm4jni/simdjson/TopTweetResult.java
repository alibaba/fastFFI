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
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFISetter;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.llvm4jni.simdjson.stdcxx.StringView;

@FFIGen
@FFITypeAlias("simdjson::top_tweet_result")
@CXXHead("benchmarks.h")
@CXXTemplate(cxx = "std::string_view", java = "com.alibaba.llvm4jni.simdjson.stdcxx.StringView")
public interface TopTweetResult<T> extends CXXPointer {

    Factory<StringView> STRING_VIEW_FACTORY = FFITypeFactory.getFactory(TopTweetResult.class, "simdjson::top_tweet_result<std::string_view>");

    @FFIFactory
    interface Factory<T> {
        TopTweetResult<T> create();
    }

    @FFIGetter long retweet_count();
    @FFISetter void retweet_count(long v);

    @FFIGetter @CXXReference T screen_name();
    @FFISetter void screen_name(@CXXReference T v);

    @FFIGetter @CXXReference T text();
    @FFISetter void text(@CXXReference T v);

}
