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
package com.alibaba.fastffi.test;

import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFIVector;
import org.junit.Test;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;

public class FFITypeFactoryTest {

    @Test
    public void testGetFFITypeName() {
        {
            assertEquals(FFITypeFactory.getFFITypeName(byte.class, true), "jbyte");
            assertEquals(FFITypeFactory.getFFITypeName(boolean.class, true), "jboolean");
            assertEquals(FFITypeFactory.getFFITypeName(short.class, true), "jshort");
            assertEquals(FFITypeFactory.getFFITypeName(int.class, true), "jint");
            assertEquals(FFITypeFactory.getFFITypeName(float.class, true), "jfloat");
            assertEquals(FFITypeFactory.getFFITypeName(long.class, true), "jlong");
            assertEquals(FFITypeFactory.getFFITypeName(double.class, true), "jdouble");
            assertEquals(FFITypeFactory.getFFITypeName(char.class, true), "jchar");
        }
        {
            assertEquals(FFITypeFactory.getFFITypeName(Byte.class, true), "jbyte");
            assertEquals(FFITypeFactory.getFFITypeName(Boolean.class, true), "jboolean");
            assertEquals(FFITypeFactory.getFFITypeName(Short.class, true), "jshort");
            assertEquals(FFITypeFactory.getFFITypeName(Integer.class, true), "jint");
            assertEquals(FFITypeFactory.getFFITypeName(Float.class, true), "jfloat");
            assertEquals(FFITypeFactory.getFFITypeName(Long.class, true), "jlong");
            assertEquals(FFITypeFactory.getFFITypeName(Double.class, true), "jdouble");
            assertEquals(FFITypeFactory.getFFITypeName(Character.class, true), "jchar");
        }
        {
            Type type = FFITypeFactory.makeParameterizedType(FFIVector.class, Integer.class);
            assertEquals(FFITypeFactory.getFFITypeName(type, true), "std::vector<jint>");
            type = FFITypeFactory.makeParameterizedType(FFIVector.class, type);
            assertEquals(FFITypeFactory.getFFITypeName(type, true), "std::vector<std::vector<jint>>");
        }
    }
}
