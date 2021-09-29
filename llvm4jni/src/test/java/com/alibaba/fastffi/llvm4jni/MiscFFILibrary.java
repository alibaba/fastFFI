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
package com.alibaba.fastffi.llvm4jni;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeFactory;

@FFIGen(library = "llvm4jni-test")
@CXXHead("llvm4jni_test.hpp")
@FFILibrary(value = "llvm4jni::misc", namespace = "llvm4jni")
public interface MiscFFILibrary {

    MiscFFILibrary INSTANCE = FFITypeFactory.getLibrary(MiscFFILibrary.class);

    int test_switch_table(int i);
    int test_switch(SwitchTestStruct struct, int i);

    int test_ctlz_int8_t(byte v);
    int test_cttz_int8_t(byte v);
    int test_ctpop_int8_t(byte v);

    int test_ctlz_int16_t(short v);
    int test_cttz_int16_t(short v);
    int test_ctpop_int16_t(short v);

    int test_ctlz_int32_t(int v);
    int test_cttz_int32_t(int v);
    int test_ctpop_int32_t(int v);

    int test_ctlz_int64_t(long v);
    int test_cttz_int64_t(long v);
    int test_ctpop_int64_t(long v);

    int test_alloca(MemoryOperationTestStruct struct, int i, int j);
    void test_localconst(MemoryOperationTestStruct struct, int i);

}

