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
package com.alibaba.fastffi.stdcxx.test;

import com.alibaba.fastffi.FFITypeFactory;

import static org.junit.Assert.assertTrue;

public class StdVectorTest {

    public void testStdVector() {
        StdVector.Factory<Integer> factory = FFITypeFactory.getFactory(StdVector.class, "std::vector<int>");
        StdVector<Integer> vector = factory.create();

        int total = 10;
        for (int i = 0; i < total; i++) {
            vector.push_back(i);
        }
        assertTrue(vector.size() == total);
        for (int i = 0; i < total; i++) {
            assertTrue(vector.at(i) == vector.get(i));
            assertTrue(vector.at(i) == i);
        }

        vector.delete();
    }

}
