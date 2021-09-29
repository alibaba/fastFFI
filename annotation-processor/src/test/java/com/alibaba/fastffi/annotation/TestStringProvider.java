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
package com.alibaba.fastffi.annotation;

import com.alibaba.fastffi.StringProvider;
import org.junit.Test;

import static com.alibaba.fastffi.FFIUnsafe.U;
import static org.junit.Assert.assertEquals;

public class TestStringProvider {

    @Test
    public void test() {
        int size = 4;
        long addr = U.allocateMemory(size);
        String str = "1234";
        byte[] bytes = str.getBytes();
        assertEquals(size, bytes.length);
        for (int i = 0; i < size; i++) {
            U.putByte(addr + i, bytes[i]);
        }

        StringProvider sp = new StringProvider() {

            @Override
            public long size() {
                return size;
            }

            @Override
            public long data() {
                return addr;
            }
        };
        assertEquals(str, sp.toJavaString());
    }
}
