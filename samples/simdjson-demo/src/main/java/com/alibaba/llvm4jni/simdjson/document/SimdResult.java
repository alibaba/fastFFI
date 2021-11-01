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
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.llvm4jni.simdjson.ErrorCode;

@FFIGen
@FFITypeAlias("simdjson::simdjson_result")
@CXXHead("simdjson.h")
@CXXTemplate(cxx = "simdjson::dom::element", java = "SimdElement")
@CXXTemplate(cxx = "simdjson::dom::array", java = "SimdArray")
@CXXTemplate(cxx = "simdjson::dom::object", java = "SimdObject")
@CXXTemplate(cxx = "simdjson::padded_string", java = "SimdPaddedString")
@CXXTemplate(cxx = "std::string_view", java = "com.alibaba.llvm4jni.simdjson.stdcxx.StringView")
public interface SimdResult<T> extends FFIPointer {

    @CXXValue
    ErrorCode error();

    @FFINameAlias("error")
    int error_code();

    @CXXReference T value_unsafe();
}
