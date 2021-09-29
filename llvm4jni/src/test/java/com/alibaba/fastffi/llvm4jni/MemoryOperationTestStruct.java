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
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;

@FFIGen( library = "llvm4jni-test")
@FFITypeAlias("llvm4jni::MemoryOperationTestStruct")
@CXXHead("llvm4jni_test.hpp")
public interface MemoryOperationTestStruct extends CXXPointer {

    Factory factory = FFITypeFactory.getFactory(MemoryOperationTestStruct.class);

    @FFIFactory
    interface Factory {
        MemoryOperationTestStruct create();
    }

    int test_sum();
    void test_memset(byte v);
    void test_memcpy(@CXXReference MemoryOperationTestStruct other);
    void test_memmove(@CXXReference MemoryOperationTestStruct other);
}
