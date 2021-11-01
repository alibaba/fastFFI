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
package com.alibaba.llvm4jni.simdjson.document;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIConst;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.llvm4jni.simdjson.ErrorCode;
import com.alibaba.llvm4jni.simdjson.stdcxx.StdString;

@FFIGen
@CXXHead("simdjson.h")
@FFITypeAlias("simdjson::padded_string")
public interface SimdPaddedString extends CXXPointer {

    static SimdResult<SimdPaddedString> load(String path) {
        StdString stdString = StdString.create(path);
        SimdResult<SimdPaddedString> result = Static.INSTANCE.load(stdString);
        stdString.delete();
        return result;
    }

    static SimdPaddedString create(String content) {
        StdString stdString = StdString.create(content);
        SimdPaddedString paddedString = factory.create(stdString);
        stdString.delete();
        return paddedString;
    }

    Factory factory = FFITypeFactory.getFactory(SimdPaddedString.class);

    @FFIFactory
    interface Factory {
        SimdPaddedString create(@FFIConst @CXXReference StdString input);
    }

    @FFIGen
    @CXXHead("simdjson.h")
    @FFILibrary(value = "simdjson::padded_string", namespace = "simdjson::padded_string")
    interface Static {
        Static INSTANCE = FFITypeFactory.getLibrary(Static.class);
        @CXXValue SimdResult<SimdPaddedString> load(@CXXReference StdString path);
    }
}
