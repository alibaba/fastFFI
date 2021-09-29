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
package com.alibaba.fastffi.llvm;

import com.kenai.jffi.Library;
import jnr.ffi.provider.Invoker;
import jnr.ffi.provider.jffi.LLVMIndirectInvokerFactory;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Unit test for simple App.
 */
public class LLVMIndirectInvokerFactoryTest
{
    static Library libtest;
    static LLVMIndirectInvokerFactory factory;

    @BeforeClass
    public static void setUp() throws IOException {
        libtest = Library.getCachedInstance(TestUtils.getResourceAsFile("liblibtest.so").getAbsolutePath(), Library.LAZY | Library.GLOBAL);
        factory = new LLVMIndirectInvokerFactory();
    }

    interface TestAddInvoker {
        int invoke(long fun, int a, int b);
    }

    // FIXME
    // @Test
    public void testAdd() {
        long test_add_int = libtest.getSymbolAddress("test_add_int");
        Assert.assertTrue(test_add_int != 0L);
        try {
            Method method = TestAddInvoker.class.getDeclaredMethod("invoke", long.class, int.class, int.class);
            Invoker invoker = factory.createInvoker(method);
            int result = (int) invoker.invoke(null, new Object[] {test_add_int, 123, 321});
            Assert.assertTrue(result == (123 + 321));
        } catch (NoSuchMethodException e) {
            Assert.assertTrue(false);
        }

    }
}
