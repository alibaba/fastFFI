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
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.llvm4jni.simdjson.ErrorCode;
import com.alibaba.llvm4jni.simdjson.stdcxx.StringView;

@FFIGen
@FFITypeAlias("simdjson::dom::element")
@CXXHead("simdjson.h")
public interface SimdElement extends FFIPointer {

    Factory factory = FFITypeFactory.getFactory(SimdElement.class);

    static SimdElement create() {
        return factory.create();
    }

    @FFIFactory
    interface Factory {
        SimdElement create();
    }

    boolean is_null();
    boolean is_bool();
    boolean is_number();
    boolean is_int64();
    boolean is_uint64();
    boolean is_string();
    boolean is_array();
    boolean is_object();

    @FFIExpr("{0}->get_c_str().value_unsafe()")
    long get_c_str();

    @CXXValue
    @FFIExpr("{0}->get_string().value_unsafe()")
    StringView get_string();

    @FFIExpr("{0}->get_bool().value_unsafe()")
    boolean get_bool();

    @FFIExpr("{0}->get_uint64().value_unsafe()")
    long get_uint64();

    @FFIExpr("{0}->get_int64().value_unsafe()")
    long get_int64();

    @FFIExpr("{0}->get_double().value_unsafe()")
    double get_double();

    @CXXValue
    @FFIExpr("{0}->get_array()")
    SimdResult<SimdArray> get_array();
    @FFIExpr("{0}->get_array().value_unsafe()")
    @CXXValue SimdArray get_array_unsafe();

    @FFIExpr("(*{0})[{1}]")
    @CXXValue SimdResult<SimdElement> get(@CXXReference StringView key);

    @FFIExpr("(*{0})[{1}]")
    @CXXValue SimdResult<SimdElement> get(@FFITypeAlias("const char*") long key);
    default SimdElement get_unsafe(@CXXReference StringView key) {
        return get(key).value_unsafe();
    }
    default SimdElement get_unsafe(@FFITypeAlias("const char*") long key) {
        return get(key).value_unsafe();
    }

    @FFIExpr("(*{0})[{1}].value_unsafe().get_uint64().value_unsafe()")
    long get_unsafe_uint64(@FFITypeAlias("const char*") long key);
    @FFIExpr("(*{0})[{1}].value_unsafe().get_int64().value_unsafe()")
    long get_unsafe_int64(@FFITypeAlias("const char*") long key);
    @FFIExpr("(*{0})[{1}].value_unsafe()[{2}].value_unsafe().get_int64().value_unsafe()")
    long get_unsafe_int64(@FFITypeAlias("const char*") long key, @FFITypeAlias("const char*") long key2);
    @FFIExpr("(*{0})[{1}].value_unsafe().get_double().value_unsafe()")
    double get_unsafe_double(@FFITypeAlias("const char*") long key);
    @FFIExpr("(*{0})[{1}].value_unsafe().get_array().value_unsafe()")
    @CXXValue SimdArray get_unsafe_array(@FFITypeAlias("const char*") long key);
    @FFIExpr("(*{0})[{1}].value_unsafe().get_string().value_unsafe()")
    @CXXValue StringView get_unsafe_string(@FFITypeAlias("const char*") long key);

    @CXXValue
    @FFIExpr("{0}->get_object()")
    SimdResult<SimdObject> get_object();
    default SimdObject get_object_unsafe() {
        return get_object().value_unsafe();
    }

    @FFIExpr("{0}->get({1})")
    ErrorCode get_object(@CXXReference SimdObject value);
    @FFIExpr("{0}->get({1})")
    ErrorCode get_array(@CXXReference SimdArray value);
}
