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
package com.alibaba.fastffi.annotation.test;

import com.alibaba.fastffi.FFITypeFactory;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ClassFunctionTemplateTest {

    @Test
    public void testInt() {
        TestClassFunctionTemplate.Factory<Integer> factory = (TestClassFunctionTemplate.Factory<Integer>) FFITypeFactory.getFactory("test::TestClassFunctionTemplate" + "<int>");
        TestClassFunctionTemplate<Integer> test = factory.create();

        TestClassFunctionTemplateGen<Integer> testGen = (TestClassFunctionTemplateGen<Integer>) test;
        {
            Byte ret = testGen.getValueNoSuffix(1, Byte.valueOf((byte) 2));
            assertTrue(ret != null && ret.intValue() == 3);
        }
        {
            Integer ret = testGen.getValueNoSuffix(1, Integer.valueOf((byte) 2));
            assertTrue(ret != null && ret.intValue() == 3);
        }
        {
            Byte ret = testGen.getValueSkip(1, (Byte) null);
            assertTrue(ret != null && ret.intValue() == 1);
        }
        {
            Integer ret = testGen.getValueSkip(1, (Integer) null);
            assertTrue(ret != null && ret.intValue() == 1);
        }
        test.delete();
    }

    @Test
    public void testByte() {
        TestClassFunctionTemplate.Factory<Byte> factory = (TestClassFunctionTemplate.Factory<Byte>) FFITypeFactory.getFactory("test::TestClassFunctionTemplate" + "<char>");
        TestClassFunctionTemplate<Byte> test = factory.create();

        TestClassFunctionTemplateGen<Byte> testGen = (TestClassFunctionTemplateGen<Byte>) test;
        {
            Byte ret = testGen.getValueNoSuffix(Byte.valueOf((byte) 1), Byte.valueOf((byte) 2));
            assertTrue(ret != null && ret.intValue() == 3);
        }
        {
            Integer ret = testGen.getValueNoSuffix(Byte.valueOf((byte) 1), Integer.valueOf((byte) 2));
            assertTrue(ret != null && ret.intValue() == 3);
        }
        {
            Byte ret = testGen.getValueSkip(Byte.valueOf((byte) 1), (Byte) null);
            assertTrue(ret != null && ret.intValue() == 1);
        }
        {
            Integer ret = testGen.getValueSkip(Byte.valueOf((byte) 1), (Integer) null);
            assertTrue(ret != null && ret.intValue() == 1);
        }
        test.delete();
    }
}
