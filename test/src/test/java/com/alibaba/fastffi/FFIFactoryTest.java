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
package com.alibaba.fastffi;

import com.alibaba.fastffi.stdcxx.test.StdString;
import com.alibaba.fastffi.stdcxx.test.StdVector;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FFIFactoryTest {

    @Test
    public void testGetFactory() {
        {
            // We have multiple std::string
            assertTrue(FFITypeFactory.getFactory("std::string") != null);
        }
        {
            StdString.Factory factory = FFITypeFactory.getFactory(StdString.class, "std::string");
            assertTrue(factory != null);
        }
        {
            StdString.Factory factory = FFITypeFactory.getFactory(StdString.Factory.class, StdString.class);
            assertTrue(factory != null);
        }
        {
            StdString.Factory factory = FFITypeFactory.getFactory(StdString.class);
            assertTrue(factory != null);
        }
        {
            StdVector.Factory<Integer> factory = FFITypeFactory.getFactory("std::vector<int>");
            assertTrue(factory != null);
        }
        {
            StdVector.Factory<Integer> factory = FFITypeFactory.getFactory(StdVector.class, "std::vector<int>");
            assertTrue(factory != null);
        }
    }

    @Test
    public void testGetType() throws Exception {
        // We have multiple std::string
        assertTrue(FFITypeFactory.getType("std::string") != null);
        assertTrue(FFITypeFactory.getType(StdString.class) != null);
        assertTrue(FFITypeFactory.getType(FFIType.class, "std::string") != null);
        assertTrue(FFITypeFactory.getType(FFIPointer.class, "std::string") != null);
        assertTrue(FFITypeFactory.getType(StdString.class, "std::string") != null);

        assertTrue(FFITypeFactory.getType("std::vector<int>") != null);
        assertTrue(FFITypeFactory.getType(FFIType.class, "std::vector<int>") != null);
        assertTrue(FFITypeFactory.getType(FFIPointer.class, "std::vector<int>") != null);
        assertTrue(FFITypeFactory.getType(FFIPointer.class, "std::vector<int>")== FFITypeFactory.getType(StdVector.class, "std::vector<int>"));
        assertTrue(FFITypeFactory.getType(StdVector.class, "std::vector<int>") != null);
        assertTrue(FFITypeFactory.getType(FFIPointer.class, "std::vector<char>") != null);
        assertTrue(FFITypeFactory.getType(StdVector.class, "std::vector<char>") != null);
        assertTrue(FFITypeFactory.getType(StdVector.class, "std::vector<char>") != FFITypeFactory.getType(StdVector.class, "std::vector<int>"));

    }
}
