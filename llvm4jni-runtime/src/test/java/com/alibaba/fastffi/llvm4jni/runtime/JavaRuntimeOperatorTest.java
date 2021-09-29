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

import java.math.BigInteger;

import static org.junit.Assert.assertTrue;

/**
 * A list of meta-morphic testing
 */
public class JavaRuntimeOperatorTest {

    @Test
    public void testBitsCast() {
        {
            int value = 123456789;
            assertTrue(JavaRuntime.floatToIntBits(JavaRuntime.intBitsToFloat(value)) == value);
        }
        {
            float value = 123456.789F;
            assertTrue(JavaRuntime.intBitsToFloat(JavaRuntime.floatToIntBits(value)) == value);
        }
        {
            long value = 123456789L;
            assertTrue(JavaRuntime.doubleToLongBits(JavaRuntime.longBitsToDouble(value)) == value);
        }
        {
            double value = 123456.789D;
            assertTrue(JavaRuntime.longBitsToDouble(JavaRuntime.doubleToLongBits(value)) == value);
        }
    }

    @Test
    public void testUDiv() {
        {
            byte v1 = +16;
            byte v2 = -64;
            byte v3 = JavaRuntime.udiv(v2, v1);
            assertTrue(Byte.toUnsignedInt(v1) * Byte.toUnsignedInt(v3) == Byte.toUnsignedInt(v2));
        }
        {
            short v1 = +16;
            short v2 = -64;
            short v3 = JavaRuntime.udiv(v2, v1);
            assertTrue(Short.toUnsignedInt(v1) * Short.toUnsignedInt(v3) == Short.toUnsignedInt(v2));
        }
        {
            int v1 = +16;
            int v2 = -64;
            int v3 = JavaRuntime.udiv(v2, v1);
            assertTrue(Integer.toUnsignedLong(v1) * Integer.toUnsignedLong(v3) == Integer.toUnsignedLong(v2));
        }
        {
            long v1 = +16;
            long v2 = -64;
            long v3 = JavaRuntime.udiv(v2, v1);
            assertTrue(v3 != v2 / v1);
            BigInteger v1Unsigned = new BigInteger(Long.toUnsignedString(v1));
            BigInteger v2Unsigned = new BigInteger(Long.toUnsignedString(v2));
            BigInteger v3Unsigned = new BigInteger(Long.toUnsignedString(v3));
            assertTrue(v2Unsigned.divide(v1Unsigned).equals(v3Unsigned));
        }
        {
            byte v1 = +16;
            byte v2 = +64;
            byte v3 = JavaRuntime.udiv(v2, v1);
            assertTrue(Byte.toUnsignedInt(v1) * Byte.toUnsignedInt(v3) == Byte.toUnsignedInt(v2));
            assertTrue(v3 == v2 / v1);
        }
        {
            short v1 = +16;
            short v2 = +64;
            short v3 = JavaRuntime.udiv(v2, v1);
            assertTrue(Short.toUnsignedInt(v1) * Short.toUnsignedInt(v3) == Short.toUnsignedInt(v2));
            assertTrue(v3 == v2 / v1);
        }
        {
            int v1 = +16;
            int v2 = +64;
            int v3 = JavaRuntime.udiv(v2, v1);
            assertTrue(Integer.toUnsignedLong(v1) * Integer.toUnsignedLong(v3) == Integer.toUnsignedLong(v2));
            assertTrue(v3 == v2 / v1);
        }
        {
            long v1 = +16;
            long v2 = +64;
            long v3 = JavaRuntime.udiv(v2, v1);
            assertTrue(v3 == v2 / v1);
        }
    }

    @Test
    public void testURem() {
        {
            byte v1 = +15;
            byte v2 = -64;
            byte v3 = JavaRuntime.udiv(v2, v1);
            byte v4 = JavaRuntime.urem(v2, v1);
            assertTrue((Byte.toUnsignedInt(v1) * Byte.toUnsignedInt(v3) + Byte.toUnsignedInt(v4)) == Byte.toUnsignedInt(v2));
        }
        {
            short v1 = +15;
            short v2 = -64;
            short v3 = JavaRuntime.udiv(v2, v1);
            short v4 = JavaRuntime.urem(v2, v1);
            assertTrue((Short.toUnsignedInt(v1) * Short.toUnsignedInt(v3) + Short.toUnsignedInt(v4)) == Short.toUnsignedInt(v2));
        }
        {
            int v1 = +15;
            int v2 = -64;
            int v3 = JavaRuntime.udiv(v2, v1);
            int v4 = JavaRuntime.urem(v2, v1);
            assertTrue((Integer.toUnsignedLong(v1) * Integer.toUnsignedLong(v3) + Integer.toUnsignedLong(v4)) == Integer.toUnsignedLong(v2));
        }
        {
            long v1 = +15;
            long v2 = -64;
            long v3 = JavaRuntime.udiv(v2, v1);
            long v4 = JavaRuntime.urem(v2, v1);
            BigInteger v1Unsigned = new BigInteger(Long.toUnsignedString(v1));
            BigInteger v2Unsigned = new BigInteger(Long.toUnsignedString(v2));
            BigInteger v3Unsigned = new BigInteger(Long.toUnsignedString(v3));
            BigInteger v4Unsigned = new BigInteger(Long.toUnsignedString(v4));
            assertTrue(v2Unsigned.divide(v1Unsigned).equals(v3Unsigned));
            assertTrue(v2Unsigned.remainder(v1Unsigned).equals(v4Unsigned));
            assertTrue(v3Unsigned.multiply(v1Unsigned).add(v4Unsigned).equals(v2Unsigned));
        }
    }
}
