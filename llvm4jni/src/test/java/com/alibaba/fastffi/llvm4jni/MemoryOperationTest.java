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

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MemoryOperationTest {

    @Test
    public void test() {
        MemoryOperationTestStruct struct = MemoryOperationTestStruct.factory.create();
        MemoryOperationTestStruct other = MemoryOperationTestStruct.factory.create();

        assertTrue(struct.test_sum() == 0);
        assertTrue(other.test_sum() == 0);
        struct.test_memset((byte) 1);
        assertTrue(struct.test_sum() == 10);
        other.test_memset((byte) 2);
        assertTrue(other.test_sum() == 20);
        struct.test_memcpy(other);
        assertTrue(struct.test_sum() == 20);
        other.test_memset((byte) 0);
        struct.test_memmove(other);
        assertTrue(struct.test_sum() == 0);
        assertTrue(other.test_sum() == 0);

        struct.delete();
    }
}
