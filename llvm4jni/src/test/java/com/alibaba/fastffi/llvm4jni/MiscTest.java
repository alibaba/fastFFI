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

import com.alibaba.fastffi.llvm4jni.runtime.JavaRuntime;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class MiscTest {

    @Test
    public void testSwitchTable() {
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch_table(0) == 1);
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch_table(1) == 1);
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch_table(2) == 2);
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch_table(3) == 3);
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch_table(4) == 5);
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch_table(5) == 0);
    }

    @Test
    public void testSwitch() {
        SwitchTestStruct struct = SwitchTestStruct.factory.create();
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch(struct, 0) == 1);
        Assert.assertTrue(struct.i() == 0);
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch(struct,1) == 1);
        Assert.assertTrue(struct.i() == 1);
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch(struct,2) == 2);
        Assert.assertTrue(struct.i() == 3);
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch(struct,3) == 3);
        Assert.assertTrue(struct.i() == 6);
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch(struct,4) == 5);
        Assert.assertTrue(struct.i() == 5);
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_switch(struct,5) == 0);
        Assert.assertTrue(struct.i() == 10);
    }

    // __builtin_xxx applies to 32 bit unsigned int
    static boolean useBuiltin = true;

    @Test
    public void testCountBits() {
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            if (!useBuiltin) {
                byte v = (byte) rand.nextInt();
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_ctlz_int8_t(v) == JavaRuntime.ctlz(v));
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_cttz_int8_t(v) == JavaRuntime.cttz(v));
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_ctpop_int8_t(v) == JavaRuntime.ctpop(v));
            }
            if (!useBuiltin) {
                short v = (short) rand.nextInt();
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_ctlz_int16_t(v) == JavaRuntime.ctlz(v));
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_cttz_int16_t(v) == JavaRuntime.cttz(v));
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_ctpop_int16_t(v) == JavaRuntime.ctpop(v));
            }
            {
                int v = rand.nextInt();
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_ctlz_int32_t(v) == JavaRuntime.ctlz(v));
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_cttz_int32_t(v) == JavaRuntime.cttz(v));
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_ctpop_int32_t(v) == JavaRuntime.ctpop(v));
            }
            if (!useBuiltin) {
                long v = rand.nextLong();
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_ctlz_int64_t(v) == JavaRuntime.ctlz(v));
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_cttz_int64_t(v) == JavaRuntime.cttz(v));
                Assert.assertTrue(MiscFFILibrary.INSTANCE.test_ctpop_int64_t(v) == JavaRuntime.ctpop(v));
            }
        }
    }

    @Test
    public void testAlloca() {
        MemoryOperationTestStruct m1 = MemoryOperationTestStruct.factory.create();
        m1.test_memset((byte) 1);

        Assert.assertTrue(m1.test_sum() == 10);
        Assert.assertTrue(MiscFFILibrary.INSTANCE.test_alloca(m1, 0, 5) == 1);

        m1.delete();
    }

    @Test
    public void testLocalconst() {
        MemoryOperationTestStruct m1 = MemoryOperationTestStruct.factory.create();
        m1.test_memset((byte) 1);

        Assert.assertTrue(m1.test_sum() == 10);

        MiscFFILibrary.INSTANCE.test_localconst(m1, 0);

        int val = 0;
        {
            char[] array = new char[10];
            Arrays.fill(array, (char) 1);
            array[0] = 'a';
            array[1] = 'l';
            array[2] = 'i';
            array[3] = 'b';
            array[4] = 'a';
            array[5] = 'b';
            array[6] = 'a';
            for (int i = 0; i < array.length; i++) {
                val += array[i];
            }
        }
        Assert.assertTrue(m1.test_sum() == val);

        m1.delete();
    }
}
