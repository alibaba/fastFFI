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
package com.alibaba.fastffi.llvm4jni.runtime;

import org.junit.Test;

import java.util.Random;

import static com.alibaba.fastffi.llvm4jni.runtime.JavaRuntime.ctlz;
import static com.alibaba.fastffi.llvm4jni.runtime.JavaRuntime.ctpop;
import static com.alibaba.fastffi.llvm4jni.runtime.JavaRuntime.cttz;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JavaRuntimeTest {

    @Test
    public void testJNIIsAssignableFrom() {
        assertTrue(JavaRuntime.jniIsAssignableFrom(Object.class, String.class));
        assertFalse(JavaRuntime.jniIsAssignableFrom(String.class, Object.class));
    }


    @Test
    public void testStrlen() {
        {
            long base = JavaRuntime.allocate(5);

            JavaRuntime.putByte(base + 0, (byte) 0);
            assertTrue(JavaRuntime.strlen(base) == 0);

            JavaRuntime.putByte(base + 0, (byte) 'a');
            JavaRuntime.putByte(base + 1, (byte) 0);
            assertTrue(JavaRuntime.strlen(base) == 1);

            JavaRuntime.putByte(base + 0, (byte) 'a');
            JavaRuntime.putByte(base + 1, (byte) 'b');
            JavaRuntime.putByte(base + 2, (byte) 'c');
            JavaRuntime.putByte(base + 3, (byte) 'd');
            JavaRuntime.putByte(base + 4, (byte) 0);
            assertTrue(JavaRuntime.strlen(base) == 4);
            JavaRuntime.free(base);
        }
    }

    @Test
    public void testBcmp() {
        {
            long base1 = JavaRuntime.allocate(4);
            long base2 = JavaRuntime.allocate(4);

            JavaRuntime.putLong(base1, 0xdeadbeefdeadbeefL);
            JavaRuntime.putLong(base2, 0xdeadbeefdeadbeefL);
            assertTrue(JavaRuntime.bcmp(base1, base2, 4) == 0);

            JavaRuntime.putLong(base1, 0xdeadbeefdeadbeefL);
            JavaRuntime.putLong(base2, 0xdeadbeefdeadbeeeL);
            assertTrue(JavaRuntime.bcmp(base1, base2, 4) == 1);

            JavaRuntime.putLong(base1, 0xdeacbeefdeadbeeeL);
            JavaRuntime.putLong(base2, 0xdeadaeefdeadbeefL);
            assertTrue(JavaRuntime.bcmp(base1, base2, 4) == -1);

            JavaRuntime.putLong(base1, 0xdeadbeefdeadbeefL);
            JavaRuntime.putLong(base2, 0xddadbeefdeadbeeeL);
            assertTrue(JavaRuntime.bcmp(base1, base2, 4) == 1);

            JavaRuntime.putLong(base1, 0xdfacbeefdeadbeeeL);
            JavaRuntime.putLong(base2, 0xdeadaeefdeadbeefL);
            assertTrue(JavaRuntime.bcmp(base1, base2, 4) == -1);

            JavaRuntime.free(base1);
            JavaRuntime.free(base2);
        }
    }

    @Test
    public void testJNIFillObjectArray() {
        {
            Object[] array = new Object[10];
            Object element = new Object();
            JavaRuntime.jniFillObjectArray(array, element);
            for (Object e : array) {
                assertTrue(e == element);
            }
        }
        {
            try {
                String[] array = new String[10];
                Object element = new Object();
                JavaRuntime.jniFillObjectArray(array, element);
                throw new IllegalStateException("Should not reach here");
            } catch (ArrayStoreException e) {
                // must be exception
            }
        }
    }

    static abstract class AbstractClass {}
    static class DefaultConctructor {}
    static class NonDefaultConctructor { NonDefaultConctructor(int t) {}}

    @Test
    public void testJNIAllocate() {
        assertNull(JavaRuntime.jniAllocObject(AbstractClass.class));
        assertNotNull(JavaRuntime.jniAllocObject(DefaultConctructor.class));
        assertNotNull(JavaRuntime.jniAllocObject(NonDefaultConctructor.class));
    }

    @Test
    public void testSelect() {
        {
            int trueInt = 1234;
            int falseInt = 5678;
            assertTrue(trueInt == JavaRuntime.select(true, trueInt, falseInt));
            assertTrue(falseInt == JavaRuntime.select(false, trueInt, falseInt));
        }
        {
            long trueLong = 1234L;
            long falseLong = 5678L;
            assertTrue(trueLong == JavaRuntime.select(true, trueLong, falseLong));
            assertTrue(falseLong == JavaRuntime.select(false, trueLong, falseLong));
        }
    }

    @Test
    public void testCTPOP() {
        assertTrue(JavaRuntime.ctpop((byte)0b10101010) == 4);
        assertTrue(JavaRuntime.ctpop((short)0b1010101010101010) == 8);
        assertTrue(JavaRuntime.ctpop(0xAAAAAAAA) == 16);
        assertTrue(JavaRuntime.ctpop(0xAAAAAAAAAAAAAAAAL) == 32);
    }

    @Test
    public void testCTLZ() {
        assertTrue(JavaRuntime.ctlz((byte)0b10101010) == 0);
        assertTrue(JavaRuntime.ctlz((byte)0b01010101) == 1);
        assertTrue(JavaRuntime.ctlz((short)0b1010101010101010) == 0);
        assertTrue(JavaRuntime.ctlz((short)0b0101010101010101) == 1);
        assertTrue(JavaRuntime.ctlz(0xFF) == 24);
        assertTrue(JavaRuntime.ctlz(0xFFL) == 56);
    }

    @Test
    public void testCTTZ() {
        assertTrue(JavaRuntime.cttz((byte)0b10101010) == 1);
        assertTrue(JavaRuntime.cttz((byte)0b01010101) == 0);
        assertTrue(JavaRuntime.cttz((short)0b1010101010101010) == 1);
        assertTrue(JavaRuntime.cttz((short)0b0101010101010101) == 0);
        assertTrue(JavaRuntime.cttz(0xFF000000) == 24);
        assertTrue(JavaRuntime.cttz(0xFF00000000000000L) == 56);
    }

    @Test
    public void testCopyMemory() {
        int arraySize = 16;
        int intSize = 4;
        long address = JavaRuntime.allocate(arraySize * intSize);
        for (int i = 0; i < arraySize; i ++) {
            JavaRuntime.putInt(address + i * intSize, i);
        }
        for (int i = 0; i < arraySize; i ++) {
            assertTrue(JavaRuntime.getInt(address + i * intSize) == i);
        }

        int halfArray = arraySize >> 1;
        JavaRuntime.copyMemory(address, address + halfArray * intSize, halfArray * intSize);

        for (int i = 0; i < halfArray; i ++) {
            assertTrue(JavaRuntime.getInt(address + i * intSize) == i);
            assertTrue(JavaRuntime.getInt(address + (halfArray + i) * intSize) == i);
        }

        int bytes = arraySize * intSize;
        JavaRuntime.memset(address, (byte) 63, bytes);
        for (int i = 0; i < bytes; i++) {
            assertTrue(JavaRuntime.getByte(address + i) == (byte) 63);
        }

        JavaRuntime.free(address);
        // test free null pointer: nop
        JavaRuntime.free(0L);
    }

    static void assertMemoryEqual(long addr1, long addr2, long size) {
        for (int i = 0; i < size; i++) {
            assertTrue(JavaRuntime.getByte(addr1 + i) == JavaRuntime.getByte( addr2 + i));
        }
    }

    @Test
    public void testMoveMemory() {
        int arraySize = 16;
        int halfArray = 16 >> 1;
        int quarterArray = halfArray >> 1;
        int intSize = 4;

        int totalLength = arraySize * intSize;
        int halfLength = halfArray * intSize;
        int quarterLength = quarterArray * intSize;


        long address = JavaRuntime.allocate(totalLength << 1);
        long checked = JavaRuntime.allocate(totalLength << 1);
        for (int i = 0; i < arraySize; i ++) {
            JavaRuntime.putInt(address + i * intSize, i);
        }
        JavaRuntime.copyMemory(address, checked, totalLength);
        assertMemoryEqual(address, checked, totalLength);

        long begin = address;
        long q1 = begin + quarterLength;
        long half = begin + halfLength;

        {
            testMoveMemoryHelper(begin, half, checked, halfLength);
            testMoveMemoryHelper(half, begin, checked, halfLength);
            testMoveMemoryHelper(begin, half, checked, quarterLength);
            testMoveMemoryHelper(half, begin, checked, quarterLength);
            testMoveMemoryHelper(q1, half, checked, halfLength);
            testMoveMemoryHelper(half, q1, checked, halfLength);
            testMoveMemoryHelper(q1, half, checked, halfLength + quarterLength);
            testMoveMemoryHelper(half, q1, checked, halfLength + quarterLength);
            testMoveMemoryHelper(q1, half, checked, quarterLength);
            testMoveMemoryHelper(half, q1, checked, quarterLength);
        }
        JavaRuntime.free(address);
    }

    static void testMoveMemoryHelper(long src, long tgt, long chk, long size) {
        JavaRuntime.copyMemory(src, chk, size);
        JavaRuntime.moveMemory(src, tgt, size);
        assertMemoryEqual(tgt, chk, size);
    }

    @Test
    public void testGetPut() {
        long address = JavaRuntime.allocate(8);
        {
            byte value = 8;
            JavaRuntime.putByte(address, value);
            assertTrue(JavaRuntime.getByte(address) == value);
            value = -8;
            JavaRuntime.putByte(address, value);
            assertTrue(JavaRuntime.getByte(address) == value);
        }
        {
            boolean value = false;
            JavaRuntime.putBoolean(address, value);
            assertTrue(JavaRuntime.getBoolean(address) == value);
            value = true;
            JavaRuntime.putBoolean(address, value);
            assertTrue(JavaRuntime.getBoolean(address) == value);
        }
        {
            short value = 8;
            JavaRuntime.putShort(address, value);
            assertTrue(JavaRuntime.getShort(address) == value);
            value = -8;
            JavaRuntime.putShort(address, value);
            assertTrue(JavaRuntime.getShort(address) == value);
        }
        {
            char value = '8';
            JavaRuntime.putChar(address, value);
            assertTrue(JavaRuntime.getChar(address) == value);
        }
        {
            int value = 8;
            JavaRuntime.putInt(address, value);
            assertTrue(JavaRuntime.getInt(address) == value);
            value = -8;
            JavaRuntime.putInt(address, value);
            assertTrue(JavaRuntime.getInt(address) == value);
        }
        {
            float value = 8;
            JavaRuntime.putFloat(address, value);
            assertTrue(JavaRuntime.getFloat(address) == value);
            value = -8;
            JavaRuntime.putFloat(address, value);
            assertTrue(JavaRuntime.getFloat(address) == value);
        }
        {
            long value = 8;
            JavaRuntime.putLong(address, value);
            assertTrue(JavaRuntime.getLong(address) == value);
            value = -8;
            JavaRuntime.putLong(address, value);
            assertTrue(JavaRuntime.getLong(address) == value);
        }
        {
            double value = 8;
            JavaRuntime.putDouble(address, value);
            assertTrue(JavaRuntime.getDouble(address) == value);
            value = -8;
            JavaRuntime.putDouble(address, value);
            assertTrue(JavaRuntime.getDouble(address) == value);
        }
    }

    @Test
    public void testAtomicRMW() {
        long address = JavaRuntime.allocate(8);
        Random rand = new Random();
        {
            for (int i = 0; i < 1000; i++) {
                long v1 = rand.nextLong();
                long v2 = rand.nextLong();
                {
                    JavaRuntime.putLong(address, v1);
                    JavaRuntime.atomic_xchg(address, v2);
                    assertTrue(JavaRuntime.getLong(address) == v2);
                }
                {
                    JavaRuntime.putLong(address, v1);
                    JavaRuntime.atomic_add(address, v2);
                    assertTrue(JavaRuntime.getLong(address) == (v1 + v2));
                }
                {
                    JavaRuntime.putLong(address, v1);
                    JavaRuntime.atomic_sub(address, v2);
                    assertTrue(JavaRuntime.getLong(address) == (v1 - v2));
                }
                {
                    JavaRuntime.putLong(address, v1);
                    JavaRuntime.atomic_and(address, v2);
                    assertTrue(JavaRuntime.getLong(address) == (v1 & v2));
                }
                {
                    JavaRuntime.putLong(address, v1);
                    JavaRuntime.atomic_or(address, v2);
                    assertTrue(JavaRuntime.getLong(address) == (v1 | v2));
                }
                {
                    JavaRuntime.putLong(address, v1);
                    JavaRuntime.atomic_xor(address, v2);
                    assertTrue(JavaRuntime.getLong(address) == (v1 ^ v2));
                }
            }
        }
        {
            for (int i = 0; i < 1000; i++) {
                int v1 = rand.nextInt();
                int v2 = rand.nextInt();
                {
                    JavaRuntime.putInt(address, v1);
                    JavaRuntime.atomic_xchg(address, v2);
                    assertTrue(JavaRuntime.getInt(address) == v2);
                }
                {
                    JavaRuntime.putInt(address, v1);
                    JavaRuntime.atomic_add(address, v2);
                    assertTrue(JavaRuntime.getInt(address) == (v1 + v2));
                }
                {
                    JavaRuntime.putInt(address, v1);
                    JavaRuntime.atomic_sub(address, v2);
                    assertTrue(JavaRuntime.getInt(address) == (v1 - v2));
                }
                {
                    JavaRuntime.putInt(address, v1);
                    JavaRuntime.atomic_and(address, v2);
                    assertTrue(JavaRuntime.getInt(address) == (v1 & v2));
                }
                {
                    JavaRuntime.putInt(address, v1);
                    JavaRuntime.atomic_or(address, v2);
                    assertTrue(JavaRuntime.getInt(address) == (v1 | v2));
                }
                {
                    JavaRuntime.putInt(address, v1);
                    JavaRuntime.atomic_xor(address, v2);
                    assertTrue(JavaRuntime.getInt(address) == (v1 ^ v2));
                }
            }
        }
        JavaRuntime.free(address);
    }
}
