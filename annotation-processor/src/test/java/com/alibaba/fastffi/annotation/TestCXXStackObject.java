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

import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXStackObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCXXStackObject {

    @Test
    public void test1() {
        Pointer pointer = new Pointer();
        try (CXXStackObject<Pointer> pointerCXXStackObject = new CXXStackObject<>(pointer)) {
            assertEquals(pointer, pointerCXXStackObject.get());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void test2() {
        Pointer pointer = new Pointer();
        try (CXXStackObject<Pointer> pointerCXXStackObject = new CXXStackObject<>(pointer)) {
            assertEquals(pointer, pointerCXXStackObject.get());
            //noinspection RedundantExplicitClose
            pointerCXXStackObject.close();
        }
    }

    static class Pointer implements CXXPointer {

        @Override
        public void delete() {
        }

        @Override
        public long getAddress() {
            return 1024;
        }
    }
}
