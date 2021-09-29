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

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Random;

public class VTableTest {

    @Test
    public void test() throws Exception {
        VTableTestStruct struct = VTableTestStruct.factory.create();
        Class<?> cls = struct.getClass();
        ClassLoader cl = cls.getClassLoader();
        Method method = cls.getDeclaredMethod("nativeDoCalc", long.class, int.class, int.class);
        if (cl != null && cl.getClass().getName().equals(GeneratedTestClassLoader.class.getName())) {
            Assert.assertFalse(Modifier.isNative(method.getModifiers()));
        } else {
            Assert.assertTrue(Modifier.isNative(method.getModifiers()));
        }

        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            int x = rand.nextInt();
            int y = rand.nextInt();
            Assert.assertTrue(struct.doCalc(x, y) == x + y);
        }
        struct.delete();
    }
}
