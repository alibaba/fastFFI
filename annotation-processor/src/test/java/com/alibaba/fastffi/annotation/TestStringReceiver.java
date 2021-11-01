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

import com.alibaba.fastffi.FFIStringReceiver;
import org.junit.Test;

import static com.alibaba.fastffi.FFIUnsafe.U;
import static org.junit.Assert.assertEquals;

public class TestStringReceiver {

    @Test
    public void test() {
        FFIStringReceiver sr = new FFIStringReceiver() {
            long addr;

            @Override
            public void resize(long size) {
                if (addr != 0) {
                    U.freeMemory(addr);
                }
                addr = U.allocateMemory(size);
            }

            @Override
            public long data() {
                return addr;
            }
        };

        try {
            sr.fromJavaString("123");
            assertEquals('1', U.getByte(sr.data()));
            assertEquals('2', U.getByte(sr.data() + 1));
            assertEquals('3', U.getByte(sr.data() + 2));
        } finally {
            if (sr.data() != 0) {
                U.freeMemory(sr.data());
            }
        }
    }
}
