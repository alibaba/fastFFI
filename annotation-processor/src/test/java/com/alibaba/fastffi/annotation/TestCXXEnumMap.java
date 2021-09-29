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

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCXXEnumMap extends TestBase {

    CXXEnumMap<MyEnum> map = new CXXEnumMap<>(MyEnum.values());

    @Test
    public void test1() {
        assertEquals(MyEnum.A, map.get(1));
        assertEquals(MyEnum.B, map.get(2));
        assertEquals(MyEnum.C, map.get(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2() {
        map.get(0);
    }

    enum MyEnum implements CXXEnum {

        A(1), B(2), C(3);

        int value;

        MyEnum(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
