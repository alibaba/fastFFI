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
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

import static com.alibaba.fastffi.llvm4jni.OperatorFFILibrary.INSTANCE;

import static org.junit.Assert.assertTrue;

public class OperatorTest {

    @Test
    public void testBoolean() {
        boolean v1 = true;
        boolean v2 = true;
        testBooleanHelper(v1, v2);
        v1 = false;
        v2 = true;
        testBooleanHelper(v1, v2);
        testBooleanHelper(v2, v1);
        v1 = false;
        v2 = false;
        testBooleanHelper(v1, v2);
    }

    static void testBooleanHelper(boolean v1, boolean v2) {
        assertTrue(INSTANCE.eq_bool(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_bool(v1, v2) == (v1 && !v2));
        assertTrue(INSTANCE.ge_bool(v1, v2) == ((v1 && !v2) || (v1 == v2)));
        assertTrue(INSTANCE.lt_bool(v1, v2) == (!v1 && v2));
        assertTrue(INSTANCE.le_bool(v1, v2) == ((!v1 && v2) || (v1 == v2)));
        assertTrue(INSTANCE.ne_bool(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.and_bool(v1, v2) == (v1 & v2));
        assertTrue(INSTANCE.or_bool(v1, v2) == (v1 | v2));
        assertTrue(INSTANCE.xor_bool(v1, v2) == (v1 ^ v2));

        assertTrue(INSTANCE.select_eq_bool(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_bool(v1, v2) == ((v1 && !v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_bool(v1, v2) == (((v1 && !v2) || (v1 == v2)) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_bool(v1, v2) == ((!v1 && v2) ? v1 : v2));
        assertTrue(INSTANCE.select_le_bool(v1, v2) == (((!v1 && v2) || (v1 == v2)) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_bool(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.from_bool_to_int8_t(v1) == (byte) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_uint8_t(v1) ==  (byte) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_int16_t(v1) == (short) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_uint16_t(v1) == (short) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_int32_t(v1) == (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_uint32_t(v1) == (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_int64_t(v1) == (long) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_uint64_t(v1) == (long) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_float(v1) == (float) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_double(v1) == (double) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_jbyte(v1) == (byte) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_bool(v1) == v1);
        assertTrue(INSTANCE.from_bool_to_jshort(v1) == (short) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_jchar(v1) == (char) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_jint(v1) == (int) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_jlong(v1) == (long) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_jfloat(v1) == (float) (v1 ? 1 : 0));
        assertTrue(INSTANCE.from_bool_to_jdouble(v1) == (double) (v1 ? 1 : 0));

    }


    @Test
    public void testByte() {
        byte v1 = -64;
        byte v2 = +4;
        testByteHelper(v1, v2);
        testByteHelper(v2, v1);
        v2 = 15;
        testByteHelper(v1, v2);
        testByteHelper(v2, v1);
        v1 = -1;
        v2 = 0;
        testByteHelper(v1, v2);
        testByteHelper(v2, v1);
        v1 = Byte.MAX_VALUE;
        v2 = 0;
        testByteHelper(v1, v2);
        testByteHelper(v2, v1);
        v1 = 0;
        v2 = Byte.MIN_VALUE;
        testByteHelper(v1, v2);
        testByteHelper(v2, v1);
    }

    static byte uadd(byte v1, byte v2) {
        return (byte) (Byte.toUnsignedInt(v1) + Byte.toUnsignedInt(v2));
    }
    static byte usub(byte v1, byte v2) {
        return (byte) (Byte.toUnsignedInt(v1) - Byte.toUnsignedInt(v2));
    }
    static byte umul(byte v1, byte v2) {
        return (byte) (Byte.toUnsignedInt(v1) * Byte.toUnsignedInt(v2));
    }
    static byte udiv(byte v1, byte v2) {
        return (byte) (Byte.toUnsignedInt(v1) / Byte.toUnsignedInt(v2));
    }
    static byte urem(byte v1, byte v2) {
        return (byte) (Byte.toUnsignedInt(v1) % Byte.toUnsignedInt(v2));
    }
    static byte uand(byte v1, byte v2) {
        return (byte) (Byte.toUnsignedInt(v1) & Byte.toUnsignedInt(v2));
    }
    static byte uor(byte v1, byte v2) {
        return (byte) (Byte.toUnsignedInt(v1) | Byte.toUnsignedInt(v2));
    }
    static byte uxor(byte v1, byte v2) {
        return (byte) (Byte.toUnsignedInt(v1) ^ Byte.toUnsignedInt(v2));
    }
    static byte ushl(byte v1, byte v2) {
        return (byte) (Byte.toUnsignedInt(v1) << Byte.toUnsignedInt(v2));
    }
    static byte ushr(byte v1, byte v2) {
        return (byte) (Byte.toUnsignedInt(v1) >> Byte.toUnsignedInt(v2));
    }

    static void testByteHelper(byte v1, byte v2) {
        assertTrue(INSTANCE.add_int8_t(v1, v2) == (byte) (v1 + v2));
        assertTrue(INSTANCE.sub_int8_t(v1, v2) == (byte) (v1 - v2));
        assertTrue(INSTANCE.mul_int8_t(v1, v2) == (byte) (v1 * v2));

        assertTrue(INSTANCE.and_int8_t(v1, v2) == (byte) (v1 & v2));
        assertTrue(INSTANCE.or_int8_t(v1, v2) == (byte) (v1 | v2));
        assertTrue(INSTANCE.xor_int8_t(v1, v2) == (byte) (v1 ^ v2));
        assertTrue(INSTANCE.shl_int8_t(v1, v2) == (byte) (v1 << v2));
        assertTrue(INSTANCE.shr_int8_t(v1, v2) == (byte) (v1 >> v2));

        assertTrue(INSTANCE.add_jbyte(v1, v2) == (byte) (v1 + v2));
        assertTrue(INSTANCE.sub_jbyte(v1, v2) == (byte) (v1 - v2));
        assertTrue(INSTANCE.mul_jbyte(v1, v2) == (byte) (v1 * v2));
        assertTrue(INSTANCE.and_jbyte(v1, v2) == (byte) (v1 & v2));
        assertTrue(INSTANCE.or_jbyte(v1, v2) == (byte) (v1 | v2));
        assertTrue(INSTANCE.xor_jbyte(v1, v2) == (byte) (v1 ^ v2));
        assertTrue(INSTANCE.shl_jbyte(v1, v2) == (byte) (v1 << v2));
        assertTrue(INSTANCE.shr_jbyte(v1, v2) == (byte) (v1 >> v2));

        assertTrue(INSTANCE.add_uint8_t(v1, v2) == uadd(v1, v2));
        assertTrue(INSTANCE.sub_uint8_t(v1, v2) == usub(v1, v2));
        assertTrue(INSTANCE.mul_uint8_t(v1, v2) == umul(v1, v2));
        assertTrue(INSTANCE.and_uint8_t(v1, v2) == uand(v1, v2));
        assertTrue(INSTANCE.or_uint8_t(v1, v2) == uor(v1, v2));
        assertTrue(INSTANCE.xor_uint8_t(v1, v2) == uxor(v1, v2));
        assertTrue(INSTANCE.shl_uint8_t(v1, v2) == ushl(v1, v2));
        assertTrue(INSTANCE.shr_uint8_t(v1, v2) == ushr(v1, v2));

        if (v2 >= 0) {
            byte oldV2 = v2;
            v2 = (byte) (v2 % 8);
            assertTrue(INSTANCE.shr_int8_t(v1, v2) == (v1 >> v2));
            assertTrue(INSTANCE.shr_jbyte(v1, v2) == (v1 >> v2));
            assertTrue(INSTANCE.shr_uint8_t(v1, v2) == ushr(v1, v2));
            v2 = oldV2;
        }

        if (v2 != 0) {
            assertTrue(INSTANCE.div_int8_t(v1, v2) == (byte) (v1 / v2));
            assertTrue(INSTANCE.rem_int8_t(v1, v2) == (byte) (v1 % v2));
            assertTrue(INSTANCE.div_jbyte(v1, v2) == (byte) (v1 / v2));
            assertTrue(INSTANCE.rem_jbyte(v1, v2) == (byte) (v1 % v2));
            assertTrue(INSTANCE.div_uint8_t(v1, v2) == udiv(v1, v2));
            assertTrue(INSTANCE.rem_uint8_t(v1, v2) == urem(v1, v2));
        }

        assertTrue(INSTANCE.neg_int8_t(v1) == (byte) -v1);
        assertTrue(INSTANCE.neg_int8_t(v2) == (byte) -v2);
        assertTrue(INSTANCE.neg_jbyte(v1) == (byte) -v1);
        assertTrue(INSTANCE.neg_jbyte(v2) == (byte) -v2);
        assertTrue(INSTANCE.neg_uint8_t(v1) == (byte) -v1);
        assertTrue(INSTANCE.neg_uint8_t(v2) == (byte) -v2);

        assertTrue(INSTANCE.eq_jbyte(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_jbyte(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_jbyte(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_jbyte(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_jbyte(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_jbyte(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.eq_int8_t(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_int8_t(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_int8_t(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_int8_t(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_int8_t(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_int8_t(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.eq_uint8_t(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_uint8_t(v1, v2) == (ucmp(v1, v2) > 0));
        assertTrue(INSTANCE.ge_uint8_t(v1, v2) == (ucmp(v1, v2) >= 0));
        assertTrue(INSTANCE.lt_uint8_t(v1, v2) == (ucmp(v1, v2) < 0));
        assertTrue(INSTANCE.le_uint8_t(v1, v2) == (ucmp(v1, v2) <= 0));
        assertTrue(INSTANCE.ne_uint8_t(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.select_eq_jbyte(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_jbyte(v1, v2) == ((v1 > v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_jbyte(v1, v2) == ((v1 >= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_jbyte(v1, v2) == ((v1 < v2) ? v1 : v2));
        assertTrue(INSTANCE.select_le_jbyte(v1, v2) == ((v1 <= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_jbyte(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.select_eq_int8_t(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_int8_t(v1, v2) == ((v1 > v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_int8_t(v1, v2) == ((v1 >= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_int8_t(v1, v2) == ((v1 < v2) ? v1 : v2));
        assertTrue(INSTANCE.select_le_int8_t(v1, v2) == ((v1 <= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_int8_t(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.select_eq_uint8_t(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_uint8_t(v1, v2) == ((ucmp(v1, v2) > 0) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_uint8_t(v1, v2) == ((ucmp(v1, v2) >= 0) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_uint8_t(v1, v2) == ((ucmp(v1, v2) < 0) ? v1 : v2));
        assertTrue(INSTANCE.select_le_uint8_t(v1, v2) == ((ucmp(v1, v2) <= 0) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_uint8_t(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.from_jbyte_to_int8_t(v1) == v1);
        assertTrue(INSTANCE.from_jbyte_to_uint8_t(v1) == v1);
        assertTrue(INSTANCE.from_jbyte_to_int16_t(v1) == (short) v1);
        assertTrue(INSTANCE.from_jbyte_to_uint16_t(v1) == (short) v1);
        assertTrue(INSTANCE.from_jbyte_to_int32_t(v1) == (int) v1);
        assertTrue(INSTANCE.from_jbyte_to_uint32_t(v1) == (int) v1);
        assertTrue(INSTANCE.from_jbyte_to_int64_t(v1) == (long) v1);
        assertTrue(INSTANCE.from_jbyte_to_uint64_t(v1) == (long) v1);
        assertTrue(INSTANCE.from_jbyte_to_float(v1) == (float) v1);
        assertTrue(INSTANCE.from_jbyte_to_double(v1) == (double) v1);
        assertTrue(INSTANCE.from_jbyte_to_jbyte(v1) == v1);
        assertTrue(INSTANCE.from_jbyte_to_bool(v1) == (v1 != 0));
        assertTrue(INSTANCE.from_jbyte_to_jshort(v1) == (short) v1);
        assertTrue(INSTANCE.from_jbyte_to_jchar(v1) == (char) v1);
        assertTrue(INSTANCE.from_jbyte_to_jint(v1) == (int) v1);
        assertTrue(INSTANCE.from_jbyte_to_jlong(v1) == (long) v1);
        assertTrue(INSTANCE.from_jbyte_to_jfloat(v1) == (float) v1);
        assertTrue(INSTANCE.from_jbyte_to_jdouble(v1) == (double) v1);

        assertTrue(INSTANCE.from_uint8_t_to_jdouble(v1) == (double) Byte.toUnsignedInt(v1));
        assertTrue(INSTANCE.from_uint8_t_to_jfloat(v1) == (float) Byte.toUnsignedInt(v1));

        assertTrue(INSTANCE.bitcast_int8_t_to_jbyte(v1) == v1);
        assertTrue(INSTANCE.bitcast_jbyte_to_int8_t(v1) == v1);
    }

    static int ucmp(byte v1, byte v2) {
        return Byte.toUnsignedInt(v1) - Byte.toUnsignedInt(v2);
    }
    static int ucmp(short v1, short v2) {
        return Short.toUnsignedInt(v1) - Short.toUnsignedInt(v2);
    }
    static int ucmp(int v1, int v2) {
        return Integer.compareUnsigned(v1, v2);
    }
    static int ucmp(long v1, long v2) {
        return Long.compareUnsigned(v1, v2);
    }

    @Test
    public void testShort() {
        short v1 = -64;
        short v2 = +4;
        testShortHelper(v1, v2);
        testShortHelper(v2, v1);
        v2 = 15;
        testShortHelper(v1, v2);
        testShortHelper(v2, v1);
        v1 = -1;
        v2 = 0;
        testShortHelper(v1, v2);
        testShortHelper(v2, v1);
        v1 = Short.MAX_VALUE;
        v2 = 0;
        testShortHelper(v1, v2);
        testShortHelper(v2, v1);
        v1 = 0;
        v2 = Short.MIN_VALUE;
        testShortHelper(v1, v2);
        testShortHelper(v2, v1);
    }

    static short uadd(short v1, short v2) {
        return (short) (Short.toUnsignedInt(v1) + Short.toUnsignedInt(v2));
    }
    static short usub(short v1, short v2) {
        return (short) (Short.toUnsignedInt(v1) - Short.toUnsignedInt(v2));
    }
    static short umul(short v1, short v2) {
        return (short) (Short.toUnsignedInt(v1) * Short.toUnsignedInt(v2));
    }
    static short udiv(short v1, short v2) {
        return (short) (Short.toUnsignedInt(v1) / Short.toUnsignedInt(v2));
    }
    static short urem(short v1, short v2) {
        return (short) (Short.toUnsignedInt(v1) % Short.toUnsignedInt(v2));
    }
    static short uand(short v1, short v2) {
        return (short) (Short.toUnsignedInt(v1) & Short.toUnsignedInt(v2));
    }
    static short uor(short v1, short v2) {
        return (short) (Short.toUnsignedInt(v1) | Short.toUnsignedInt(v2));
    }
    static short uxor(short v1, short v2) {
        return (short) (Short.toUnsignedInt(v1) ^ Short.toUnsignedInt(v2));
    }
    static short ushl(short v1, short v2) {
        return (short) (Short.toUnsignedInt(v1) << Short.toUnsignedInt(v2));
    }
    static short ushr(short v1, short v2) {
        return (short) (Short.toUnsignedInt(v1) >> Short.toUnsignedInt(v2));
    }

    static void testShortHelper(short v1, short v2) {
        assertTrue(INSTANCE.add_int16_t(v1, v2) == (short) (v1 + v2));
        assertTrue(INSTANCE.sub_int16_t(v1, v2) == (short) (v1 - v2));
        assertTrue(INSTANCE.mul_int16_t(v1, v2) == (short) (v1 * v2));

        assertTrue(INSTANCE.and_int16_t(v1, v2) == (short) (v1 & v2));
        assertTrue(INSTANCE.or_int16_t(v1, v2) == (short) (v1 | v2));
        assertTrue(INSTANCE.xor_int16_t(v1, v2) == (short) (v1 ^ v2));
        assertTrue(INSTANCE.shl_int16_t(v1, v2) == (short) (v1 << v2));
        assertTrue(INSTANCE.shr_int16_t(v1, v2) == (short) (v1 >> v2));

        assertTrue(INSTANCE.add_jshort(v1, v2) == (short) (v1 + v2));
        assertTrue(INSTANCE.sub_jshort(v1, v2) == (short) (v1 - v2));
        assertTrue(INSTANCE.mul_jshort(v1, v2) == (short) (v1 * v2));
        assertTrue(INSTANCE.and_jshort(v1, v2) == (short) (v1 & v2));
        assertTrue(INSTANCE.or_jshort(v1, v2) == (short) (v1 | v2));
        assertTrue(INSTANCE.xor_jshort(v1, v2) == (short) (v1 ^ v2));
        assertTrue(INSTANCE.shl_jshort(v1, v2) == (short) (v1 << v2));
        assertTrue(INSTANCE.shr_jshort(v1, v2) == (short) (v1 >> v2));



        assertTrue(INSTANCE.add_uint16_t(v1, v2) == uadd(v1, v2));
        assertTrue(INSTANCE.sub_uint16_t(v1, v2) == usub(v1, v2));
        assertTrue(INSTANCE.mul_uint16_t(v1, v2) == umul(v1, v2));
        assertTrue(INSTANCE.and_uint16_t(v1, v2) == uand(v1, v2));
        assertTrue(INSTANCE.or_uint16_t(v1, v2) == uor(v1, v2));
        assertTrue(INSTANCE.xor_uint16_t(v1, v2) == uxor(v1, v2));
        assertTrue(INSTANCE.shl_uint16_t(v1, v2) == ushl(v1, v2));
        assertTrue(INSTANCE.shr_uint16_t(v1, v2) == ushr(v1, v2));

        if (v2 >= 0) {
            short oldV2 = v2;
            v2 = (short) (v2 % 16);
            assertTrue(INSTANCE.shr_int16_t(v1, v2) == (v1 >> v2));
            assertTrue(INSTANCE.shr_jshort(v1, v2) == (v1 >> v2));
            assertTrue(INSTANCE.shr_uint16_t(v1, v2) == ushr(v1, v2));
            v2 = oldV2;
        }

        if (v2 != 0) {
            assertTrue(INSTANCE.div_int16_t(v1, v2) == (short) (v1 / v2));
            assertTrue(INSTANCE.rem_int16_t(v1, v2) == (short) (v1 % v2));
            assertTrue(INSTANCE.div_jshort(v1, v2) == (short) (v1 / v2));
            assertTrue(INSTANCE.rem_jshort(v1, v2) == (short) (v1 % v2));
            assertTrue(INSTANCE.div_uint16_t(v1, v2) == udiv(v1, v2));
            assertTrue(INSTANCE.rem_uint16_t(v1, v2) == urem(v1, v2));
        }

        assertTrue(INSTANCE.neg_int16_t(v1) == (short) -v1);
        assertTrue(INSTANCE.neg_int16_t(v2) == (short) -v2);
        assertTrue(INSTANCE.neg_jshort(v1) == (short) -v1);
        assertTrue(INSTANCE.neg_jshort(v2) == (short) -v2);
        assertTrue(INSTANCE.neg_uint16_t(v1) == (short) -v1);
        assertTrue(INSTANCE.neg_uint16_t(v2) == (short) -v2);

        assertTrue(INSTANCE.eq_jshort(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_jshort(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_jshort(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_jshort(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_jshort(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_jshort(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.eq_int16_t(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_int16_t(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_int16_t(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_int16_t(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_int16_t(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_int16_t(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.eq_uint16_t(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_uint16_t(v1, v2) == (ucmp(v1, v2) > 0));
        assertTrue(INSTANCE.ge_uint16_t(v1, v2) == (ucmp(v1, v2) >= 0));
        assertTrue(INSTANCE.lt_uint16_t(v1, v2) == (ucmp(v1, v2) < 0));
        assertTrue(INSTANCE.le_uint16_t(v1, v2) == (ucmp(v1, v2) <= 0));
        assertTrue(INSTANCE.ne_uint16_t(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.select_eq_jshort(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_jshort(v1, v2) == ((v1 > v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_jshort(v1, v2) == ((v1 >= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_jshort(v1, v2) == ((v1 < v2) ? v1 : v2));
        assertTrue(INSTANCE.select_le_jshort(v1, v2) == ((v1 <= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_jshort(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.select_eq_int16_t(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_int16_t(v1, v2) == ((v1 > v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_int16_t(v1, v2) == ((v1 >= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_int16_t(v1, v2) == ((v1 < v2) ? v1 : v2));
        assertTrue(INSTANCE.select_le_int16_t(v1, v2) == ((v1 <= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_int16_t(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.select_eq_uint16_t(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_uint16_t(v1, v2) == ((ucmp(v1, v2) > 0) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_uint16_t(v1, v2) == ((ucmp(v1, v2) >= 0) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_uint16_t(v1, v2) == ((ucmp(v1, v2) < 0) ? v1 : v2));
        assertTrue(INSTANCE.select_le_uint16_t(v1, v2) == ((ucmp(v1, v2) <= 0) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_uint16_t(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.from_jshort_to_int8_t(v1) == (byte) v1);
        assertTrue(INSTANCE.from_jshort_to_uint8_t(v1) == (byte) v1);
        assertTrue(INSTANCE.from_jshort_to_int16_t(v1) == v1);
        assertTrue(INSTANCE.from_jshort_to_uint16_t(v1) == v1);
        assertTrue(INSTANCE.from_jshort_to_int32_t(v1) == (int) v1);
        assertTrue(INSTANCE.from_jshort_to_uint32_t(v1) == (int) v1);
        assertTrue(INSTANCE.from_jshort_to_int64_t(v1) == (long) v1);
        assertTrue(INSTANCE.from_jshort_to_uint64_t(v1) == (long) v1);
        assertTrue(INSTANCE.from_jshort_to_float(v1) == (float) v1);
        assertTrue(INSTANCE.from_jshort_to_double(v1) == (double) v1);
        assertTrue(INSTANCE.from_jshort_to_jbyte(v1) == (byte) v1);
        assertTrue(INSTANCE.from_jshort_to_bool(v1) == (v1 != 0));
        assertTrue(INSTANCE.from_jshort_to_jshort(v1) == v1);
        assertTrue(INSTANCE.from_jshort_to_jchar(v1) == (char) v1);
        assertTrue(INSTANCE.from_jshort_to_jint(v1) == (int) v1);
        assertTrue(INSTANCE.from_jshort_to_jlong(v1) == (long) v1);
        assertTrue(INSTANCE.from_jshort_to_jfloat(v1) == (float) v1);
        assertTrue(INSTANCE.from_jshort_to_jdouble(v1) == (double) v1);

        assertTrue(INSTANCE.from_uint16_t_to_jdouble(v1) == (double) Short.toUnsignedInt(v1));
        assertTrue(INSTANCE.from_uint16_t_to_jfloat(v1) == (float) Short.toUnsignedInt(v1));

        assertTrue(INSTANCE.bitcast_int16_t_to_jshort(v1) == v1);
        assertTrue(INSTANCE.bitcast_jshort_to_int16_t(v1) == v1);
    }

    @Test
    public void testInteger() {
        int v1 = -64;
        int v2 = +4;
        testIntegerHelper(v1, v2);
        testIntegerHelper(v2, v1);
        v2 = 15;
        testIntegerHelper(v1, v2);
        testIntegerHelper(v2, v1);
        v1 = -1;
        v2 = 0;
        testIntegerHelper(v1, v2);
        testIntegerHelper(v2, v1);
        v1 = Integer.MAX_VALUE;
        v2 = 0;
        testIntegerHelper(v1, v2);
        testIntegerHelper(v2, v1);
        v1 = 0;
        v2 = Integer.MIN_VALUE;
        testIntegerHelper(v1, v2);
        testIntegerHelper(v2, v1);
    }

    static int uadd(int v1, int v2) {
        return (int) (Integer.toUnsignedLong(v1) + Integer.toUnsignedLong(v2));
    }
    static int usub(int v1, int v2) {
        return (int) (Integer.toUnsignedLong(v1) - Integer.toUnsignedLong(v2));
    }
    static int umul(int v1, int v2) {
        return (int) (Integer.toUnsignedLong(v1) * Integer.toUnsignedLong(v2));
    }
    static int udiv(int v1, int v2) {
        return (int) (Integer.toUnsignedLong(v1) / Integer.toUnsignedLong(v2));
    }
    static int urem(int v1, int v2) {
        return (int) (Integer.toUnsignedLong(v1) % Integer.toUnsignedLong(v2));
    }
    static int uand(int v1, int v2) {
        return (int) (Integer.toUnsignedLong(v1) & Integer.toUnsignedLong(v2));
    }
    static int uor(int v1, int v2) {
        return (int) (Integer.toUnsignedLong(v1) | Integer.toUnsignedLong(v2));
    }
    static int uxor(int v1, int v2) {
        return (int) (Integer.toUnsignedLong(v1) ^ Integer.toUnsignedLong(v2));
    }
    static int ushl(int v1, int v2) {
        return (int) (Integer.toUnsignedLong(v1) << Integer.toUnsignedLong(v2));
    }
    static int ushr(int v1, int v2) {
        return (int) (Integer.toUnsignedLong(v1) >> Integer.toUnsignedLong(v2));
    }

    static void testIntegerHelper(int v1, int v2) {
        assertTrue(INSTANCE.add_int32_t(v1, v2) == (v1 + v2));
        assertTrue(INSTANCE.sub_int32_t(v1, v2) == (v1 - v2));
        assertTrue(INSTANCE.mul_int32_t(v1, v2) == (v1 * v2));

        assertTrue(INSTANCE.and_int32_t(v1, v2) == (v1 & v2));
        assertTrue(INSTANCE.or_int32_t(v1, v2) == (v1 | v2));
        assertTrue(INSTANCE.xor_int32_t(v1, v2) == (v1 ^ v2));
        assertTrue(INSTANCE.shl_int32_t(v1, v2) == (v1 << v2));

        assertTrue(INSTANCE.add_jint(v1, v2) == (v1 + v2));
        assertTrue(INSTANCE.sub_jint(v1, v2) == (v1 - v2));
        assertTrue(INSTANCE.mul_jint(v1, v2) == (v1 * v2));
        assertTrue(INSTANCE.and_jint(v1, v2) == (v1 & v2));
        assertTrue(INSTANCE.or_jint(v1, v2) == (v1 | v2));
        assertTrue(INSTANCE.xor_jint(v1, v2) == (v1 ^ v2));
        assertTrue(INSTANCE.shl_jint(v1, v2) == (v1 << v2));



        assertTrue(INSTANCE.add_uint32_t(v1, v2) == uadd(v1, v2));
        assertTrue(INSTANCE.sub_uint32_t(v1, v2) == usub(v1, v2));
        assertTrue(INSTANCE.mul_uint32_t(v1, v2) == umul(v1, v2));
        assertTrue(INSTANCE.and_uint32_t(v1, v2) == uand(v1, v2));
        assertTrue(INSTANCE.or_uint32_t(v1, v2) == uor(v1, v2));
        assertTrue(INSTANCE.xor_uint32_t(v1, v2) == uxor(v1, v2));
        assertTrue(INSTANCE.shl_uint32_t(v1, v2) == ushl(v1, v2));

        if (v2 >= 0) {
            int oldV2 = v2;
            v2 = v2 % 32;
            assertTrue(INSTANCE.shr_int32_t(v1, v2) == (v1 >> v2));
            assertTrue(INSTANCE.shr_jint(v1, v2) == (v1 >> v2));
            assertTrue(INSTANCE.shr_uint32_t(v1, v2) == ushr(v1, v2));
            v2 = oldV2;
        }


        if (v2 != 0) {
            assertTrue(INSTANCE.div_int32_t(v1, v2) == (v1 / v2));
            assertTrue(INSTANCE.rem_int32_t(v1, v2) == (v1 % v2));
            assertTrue(INSTANCE.div_jint(v1, v2) == (v1 / v2));
            assertTrue(INSTANCE.rem_jint(v1, v2) == (v1 % v2));
            assertTrue(INSTANCE.div_uint32_t(v1, v2) == udiv(v1, v2));
            assertTrue(INSTANCE.rem_uint32_t(v1, v2) == urem(v1, v2));
        }

        assertTrue(INSTANCE.neg_int32_t(v1) == -v1);
        assertTrue(INSTANCE.neg_int32_t(v2) == -v2);
        assertTrue(INSTANCE.neg_jint(v1) == -v1);
        assertTrue(INSTANCE.neg_jint(v2) == -v2);
        assertTrue(INSTANCE.neg_uint32_t(v1) == -v1);
        assertTrue(INSTANCE.neg_uint32_t(v2) == -v2);

        assertTrue(INSTANCE.eq_jint(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_jint(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_jint(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_jint(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_jint(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_jint(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.eq_int32_t(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_int32_t(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_int32_t(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_int32_t(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_int32_t(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_int32_t(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.eq_uint32_t(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_uint32_t(v1, v2) == (ucmp(v1, v2) > 0));
        assertTrue(INSTANCE.ge_uint32_t(v1, v2) == (ucmp(v1, v2) >= 0));
        assertTrue(INSTANCE.lt_uint32_t(v1, v2) == (ucmp(v1, v2) < 0));
        assertTrue(INSTANCE.le_uint32_t(v1, v2) == (ucmp(v1, v2) <= 0));
        assertTrue(INSTANCE.ne_uint32_t(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.select_eq_jint(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_jint(v1, v2) == ((v1 > v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_jint(v1, v2) == ((v1 >= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_jint(v1, v2) == ((v1 < v2) ? v1 : v2));
        assertTrue(INSTANCE.select_le_jint(v1, v2) == ((v1 <= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_jint(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.select_eq_int32_t(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_int32_t(v1, v2) == ((v1 > v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_int32_t(v1, v2) == ((v1 >= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_int32_t(v1, v2) == ((v1 < v2) ? v1 : v2));
        assertTrue(INSTANCE.select_le_int32_t(v1, v2) == ((v1 <= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_int32_t(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.select_eq_uint32_t(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_uint32_t(v1, v2) == ((ucmp(v1, v2) > 0) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_uint32_t(v1, v2) == ((ucmp(v1, v2) >= 0) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_uint32_t(v1, v2) == ((ucmp(v1, v2) < 0) ? v1 : v2));
        assertTrue(INSTANCE.select_le_uint32_t(v1, v2) == ((ucmp(v1, v2) <= 0) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_uint32_t(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.from_jint_to_int8_t(v1) == (byte) v1);
        assertTrue(INSTANCE.from_jint_to_uint8_t(v1) == (byte) v1);
        assertTrue(INSTANCE.from_jint_to_int16_t(v1) == (short) v1);
        assertTrue(INSTANCE.from_jint_to_uint16_t(v1) == (short) v1);
        assertTrue(INSTANCE.from_jint_to_int32_t(v1) == v1);
        assertTrue(INSTANCE.from_jint_to_uint32_t(v1) == v1);
        assertTrue(INSTANCE.from_jint_to_int64_t(v1) == (long) v1);
        assertTrue(INSTANCE.from_jint_to_uint64_t(v1) == (long) v1);
        assertTrue(INSTANCE.from_jint_to_float(v1) == (float) v1);
        assertTrue(INSTANCE.from_jint_to_double(v1) == (double) v1);
        assertTrue(INSTANCE.from_jint_to_jbyte(v1) == (byte) v1);
        assertTrue(INSTANCE.from_jint_to_bool(v1) == (v1 != 0));
        assertTrue(INSTANCE.from_jint_to_jshort(v1) == (short) v1);
        assertTrue(INSTANCE.from_jint_to_jchar(v1) == (char) v1);
        assertTrue(INSTANCE.from_jint_to_jint(v1) == v1);
        assertTrue(INSTANCE.from_jint_to_jlong(v1) == (long) v1);
        assertTrue(INSTANCE.from_jint_to_jfloat(v1) == (float) v1);
        assertTrue(INSTANCE.from_jint_to_jdouble(v1) == (double) v1);

        assertTrue(INSTANCE.from_uint32_t_to_jdouble(v1) == (double) Integer.toUnsignedLong(v1));
        assertTrue(INSTANCE.from_uint32_t_to_jfloat(v1) == (float) Integer.toUnsignedLong(v1));

        assertTrue(INSTANCE.bitcast_int32_t_to_jint(v1) == v1);
        assertTrue(INSTANCE.bitcast_jint_to_int32_t(v1) == v1);
        assertTrue(Float.compare(INSTANCE.bitcast_jint_to_float(v1), Float.intBitsToFloat(v1)) == 0);
    }

    @Test
    public void testLong() {
        long v1 = -64;
        long v2 = +4;
        testLongHelper(v1, v2);
        testLongHelper(v2, v1);
        v2 = 15;
        testLongHelper(v1, v2);
        testLongHelper(v2, v1);
        v1 = -1;
        v2 = 0;
        testLongHelper(v1, v2);
        testLongHelper(v2, v1);
        v1 = Long.MAX_VALUE;
        v2 = 0;
        testLongHelper(v1, v2);
        testLongHelper(v2, v1);
        v1 = 0;
        v2 = Long.MIN_VALUE;
        testLongHelper(v1, v2);
        testLongHelper(v2, v1);
    }

    static long uadd(long v1, long v2) {
        return v1 + v2;
    }
    static long usub(long v1, long v2) {
        return v1 - v2;
    }
    static long umul(long v1, long v2) {
        return v1 * v2;
    }
    static long udiv(long v1, long v2) {
        return Long.divideUnsigned(v1, v2);
    }
    static long urem(long v1, long v2) {
        return Long.remainderUnsigned(v1, v2);
    }
    static long uand(long v1, long v2) {
        return v1 & v2;
    }
    static long uor(long v1, long v2) {
        return v1 | v2;
    }
    static long uxor(long v1, long v2) {
        return v1 ^ v2;
    }
    static long ushl(long v1, long v2) {
        return v1 << v2;
    }
    static long ushr(long v1, long v2) {
        if (v2 < 0) {
            throw new IllegalArgumentException("Invalid shift number: " + v2);
        }
        BigInteger v1Unsigned = new BigInteger(Long.toUnsignedString(v1));
        return Long.parseUnsignedLong(v1Unsigned.shiftRight((int) v2).toString());
    }

    static void testLongHelper(long v1, long v2) {
        assertTrue(INSTANCE.add_int64_t(v1, v2) == (v1 + v2));
        assertTrue(INSTANCE.sub_int64_t(v1, v2) == (v1 - v2));
        assertTrue(INSTANCE.mul_int64_t(v1, v2) == (v1 * v2));

        assertTrue(INSTANCE.and_int64_t(v1, v2) == (v1 & v2));
        assertTrue(INSTANCE.or_int64_t(v1, v2) == (v1 | v2));
        assertTrue(INSTANCE.xor_int64_t(v1, v2) == (v1 ^ v2));
        assertTrue(INSTANCE.shl_int64_t(v1, v2) == (v1 << v2));

        assertTrue(INSTANCE.add_jlong(v1, v2) == (v1 + v2));
        assertTrue(INSTANCE.sub_jlong(v1, v2) == (v1 - v2));
        assertTrue(INSTANCE.mul_jlong(v1, v2) == (v1 * v2));
        assertTrue(INSTANCE.and_jlong(v1, v2) == (v1 & v2));
        assertTrue(INSTANCE.or_jlong(v1, v2) == (v1 | v2));
        assertTrue(INSTANCE.xor_jlong(v1, v2) == (v1 ^ v2));
        assertTrue(INSTANCE.shl_jlong(v1, v2) == (v1 << v2));



        assertTrue(INSTANCE.add_uint64_t(v1, v2) == uadd(v1, v2));
        assertTrue(INSTANCE.sub_uint64_t(v1, v2) == usub(v1, v2));
        assertTrue(INSTANCE.mul_uint64_t(v1, v2) == umul(v1, v2));
        assertTrue(INSTANCE.and_uint64_t(v1, v2) == uand(v1, v2));
        assertTrue(INSTANCE.or_uint64_t(v1, v2) == uor(v1, v2));
        assertTrue(INSTANCE.xor_uint64_t(v1, v2) == uxor(v1, v2));
        assertTrue(INSTANCE.shl_uint64_t(v1, v2) == ushl(v1, v2));

        if (v2 >= 0) {
            long oldV2 = v2;
            v2 = v2 % 64;
            assertTrue(INSTANCE.shr_int64_t(v1, v2) == (v1 >> v2));
            assertTrue(INSTANCE.shr_jlong(v1, v2) == (v1 >> v2));
            assertTrue(INSTANCE.shr_uint64_t(v1, v2) == ushr(v1, v2));
            v2 = oldV2;
        }


        if (v2 != 0) {
            // avoid divide by zero error
            assertTrue(INSTANCE.div_int64_t(v1, v2) == (v1 / v2));
            assertTrue(INSTANCE.rem_int64_t(v1, v2) == (v1 % v2));
            assertTrue(INSTANCE.div_jlong(v1, v2) == (v1 / v2));
            assertTrue(INSTANCE.rem_jlong(v1, v2) == (v1 % v2));
            assertTrue(INSTANCE.div_uint64_t(v1, v2) == udiv(v1, v2));
            assertTrue(INSTANCE.rem_uint64_t(v1, v2) == urem(v1, v2));
        }

        assertTrue(INSTANCE.neg_int64_t(v1) == -v1);
        assertTrue(INSTANCE.neg_int64_t(v2) == -v2);
        assertTrue(INSTANCE.neg_jlong(v1) == -v1);
        assertTrue(INSTANCE.neg_jlong(v2) == -v2);
        assertTrue(INSTANCE.neg_uint64_t(v1) == -v1);
        assertTrue(INSTANCE.neg_uint64_t(v2) == -v2);

        assertTrue(INSTANCE.eq_jlong(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_jlong(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_jlong(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_jlong(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_jlong(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_jlong(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.eq_int64_t(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_int64_t(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_int64_t(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_int64_t(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_int64_t(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_int64_t(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.eq_uint64_t(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_uint64_t(v1, v2) == (ucmp(v1, v2) > 0));
        assertTrue(INSTANCE.ge_uint64_t(v1, v2) == (ucmp(v1, v2) >= 0));
        assertTrue(INSTANCE.lt_uint64_t(v1, v2) == (ucmp(v1, v2) < 0));
        assertTrue(INSTANCE.le_uint64_t(v1, v2) == (ucmp(v1, v2) <= 0));
        assertTrue(INSTANCE.ne_uint64_t(v1, v2) == (v1 != v2));

        assertTrue(INSTANCE.select_eq_jlong(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_jlong(v1, v2) == ((v1 > v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_jlong(v1, v2) == ((v1 >= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_jlong(v1, v2) == ((v1 < v2) ? v1 : v2));
        assertTrue(INSTANCE.select_le_jlong(v1, v2) == ((v1 <= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_jlong(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.select_eq_int64_t(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_int64_t(v1, v2) == ((v1 > v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_int64_t(v1, v2) == ((v1 >= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_int64_t(v1, v2) == ((v1 < v2) ? v1 : v2));
        assertTrue(INSTANCE.select_le_int64_t(v1, v2) == ((v1 <= v2) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_int64_t(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.select_eq_uint64_t(v1, v2) == ((v1 == v2) ? v1 : v2));
        assertTrue(INSTANCE.select_gt_uint64_t(v1, v2) == ((ucmp(v1, v2) > 0) ? v1 : v2));
        assertTrue(INSTANCE.select_ge_uint64_t(v1, v2) == ((ucmp(v1, v2) >= 0) ? v1 : v2));
        assertTrue(INSTANCE.select_lt_uint64_t(v1, v2) == ((ucmp(v1, v2) < 0) ? v1 : v2));
        assertTrue(INSTANCE.select_le_uint64_t(v1, v2) == ((ucmp(v1, v2) <= 0) ? v1 : v2));
        assertTrue(INSTANCE.select_ne_uint64_t(v1, v2) == ((v1 != v2) ? v1 : v2));

        assertTrue(INSTANCE.from_jlong_to_int8_t(v1) == (byte) v1);
        assertTrue(INSTANCE.from_jlong_to_uint8_t(v1) == (byte) v1);
        assertTrue(INSTANCE.from_jlong_to_int16_t(v1) == (short) v1);
        assertTrue(INSTANCE.from_jlong_to_uint16_t(v1) == (short) v1);
        assertTrue(INSTANCE.from_jlong_to_int32_t(v1) == (int) v1);
        assertTrue(INSTANCE.from_jlong_to_uint32_t(v1) == (int) v1);
        assertTrue(INSTANCE.from_jlong_to_int64_t(v1) == v1);
        assertTrue(INSTANCE.from_jlong_to_uint64_t(v1) == v1);
        assertTrue(INSTANCE.from_jlong_to_float(v1) == (float) v1);
        assertTrue(INSTANCE.from_jlong_to_double(v1) == (double) v1);
        assertTrue(INSTANCE.from_jlong_to_jbyte(v1) == (byte) v1);
        assertTrue(INSTANCE.from_jlong_to_bool(v1) == (v1 != 0));
        assertTrue(INSTANCE.from_jlong_to_jshort(v1) == (short) v1);
        assertTrue(INSTANCE.from_jlong_to_jchar(v1) == (char) v1);
        assertTrue(INSTANCE.from_jlong_to_jint(v1) == (int) v1);
        assertTrue(INSTANCE.from_jlong_to_jlong(v1) == v1);
        assertTrue(INSTANCE.from_jlong_to_jfloat(v1) == (float) v1);
        assertTrue(INSTANCE.from_jlong_to_jdouble(v1) == (double) v1);

        assertTrue(INSTANCE.from_uint64_t_to_jdouble(v1) == JavaRuntime.uitofp_i64tod(v1));
        assertTrue(INSTANCE.from_uint64_t_to_jfloat(v1) == JavaRuntime.uitofp_i64tof(v1));

        assertTrue(INSTANCE.bitcast_int64_t_to_jlong(v1) == v1);
        assertTrue(INSTANCE.bitcast_jlong_to_int64_t(v1) == v1);

        assertTrue(Double.compare(INSTANCE.bitcast_jlong_to_double(v1), Double.longBitsToDouble(v1)) == 0);
    }


    @Test
    public void testFloat() {
        float v1 = -64;
        float v2 = +4;
        testFloatHelper(v1, v2);
        testFloatHelper(v2, v1);
        v2 = 15;
        testFloatHelper(v1, v2);
        testFloatHelper(v2, v1);
        v1 = -1;
        v2 = 0;
        testFloatHelper(v1, v2);
        testFloatHelper(v2, v1);
        v1 = Float.MAX_VALUE;
        v2 = 0;
        testFloatHelper(v1, v2);
        testFloatHelper(v2, v1);
        v1 = 0;
        v2 = Float.MIN_VALUE;
        testFloatHelper(v1, v2);
        testFloatHelper(v2, v1);
    }

    static void testFloatHelper(float v1, float v2) {
        assertTrue(INSTANCE.add_float(v1, v2) == (v1 + v2));
        assertTrue(INSTANCE.sub_float(v1, v2) == (v1 - v2));
        assertTrue(INSTANCE.mul_float(v1, v2) == (v1 * v2));
        assertTrue(INSTANCE.add_jfloat(v1, v2) == (v1 + v2));
        assertTrue(INSTANCE.sub_jfloat(v1, v2) == (v1 - v2));
        assertTrue(INSTANCE.mul_jfloat(v1, v2) == (v1 * v2));

        if (v2 != 0) {
            assertTrue(INSTANCE.div_float(v1, v2) == (v1 / v2));
            assertTrue(INSTANCE.div_jfloat(v1, v2) == (v1 / v2));
        }

        assertTrue(INSTANCE.neg_float(v1) == -v1);
        assertTrue(INSTANCE.neg_float(v2) == -v2);
        assertTrue(INSTANCE.neg_jfloat(v1) == -v1);
        assertTrue(INSTANCE.neg_jfloat(v2) == -v2);

        assertTrue(INSTANCE.eq_float(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_float(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_float(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_float(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_float(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_float(v1, v2) == (v1 != v2));
        assertTrue(INSTANCE.eq_jfloat(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_jfloat(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_jfloat(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_jfloat(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_jfloat(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_jfloat(v1, v2) == (v1 != v2));

        if (v1 >= Byte.MIN_VALUE && v1 <= Byte.MAX_VALUE) {
            assertTrue(INSTANCE.from_jfloat_to_int8_t(v1) == (byte) v1);
            assertTrue(INSTANCE.from_jfloat_to_jbyte(v1) == (byte) v1);
        }
        if (v1 >= 0 && v1 <= Byte.toUnsignedInt((byte) -1)) {
            assertTrue(INSTANCE.from_jfloat_to_uint8_t(v1) == (byte) v1);
        }

        if (v1 >= Short.MIN_VALUE && v1 <= Short.MAX_VALUE) {
            assertTrue(INSTANCE.from_jfloat_to_int16_t(v1) == (short) v1);
            assertTrue(INSTANCE.from_jfloat_to_jshort(v1) == (short) v1);
        }
        if (v1 >= 0 && v1 <= Short.toUnsignedInt((short) -1)) {
            assertTrue(INSTANCE.from_jfloat_to_uint16_t(v1) == (short) v1);
            assertTrue(INSTANCE.from_jfloat_to_jchar(v1) == (char) v1);
        }

        if (v1 >= Integer.MIN_VALUE && v1 <= Integer.MAX_VALUE) {
            assertTrue(INSTANCE.from_jfloat_to_int32_t(v1) == (int) v1);
            assertTrue(INSTANCE.from_jfloat_to_jint(v1) == (int) v1);
        }
        if (v1 >= 0 && v1 <= Integer.toUnsignedLong(-1)) {
            assertTrue(INSTANCE.from_jfloat_to_uint32_t(v1) == (int) v1);
        }

        if (v1 >= Long.MIN_VALUE && v1 <= Long.MAX_VALUE) {
            assertTrue(INSTANCE.from_jfloat_to_int64_t(v1) == (long) v1);
            assertTrue(INSTANCE.from_jfloat_to_jlong(v1) == (long) v1);
        }
        if (v1 >= 0 && v1 <= ((float) Long.MAX_VALUE) * 2 + 1) {
            assertTrue(INSTANCE.from_jfloat_to_uint64_t(v1) == (long) v1);
        }

        assertTrue(INSTANCE.from_jfloat_to_float(v1) == v1);
        assertTrue(INSTANCE.from_jfloat_to_double(v1) == (double) v1);
        assertTrue(INSTANCE.from_jfloat_to_bool(v1) == (v1 != 0));

        assertTrue(INSTANCE.from_jfloat_to_jfloat(v1) == v1);
        assertTrue(INSTANCE.from_jfloat_to_jdouble(v1) == (double) v1);

        assertTrue(INSTANCE.bitcast_float_to_jint(v1) == Float.floatToIntBits(v1));
    }

    @Test
    public void testDouble() {
        double v1 = -64;
        double v2 = +4;
        testDoubleHelper(v1, v2);
        testDoubleHelper(v2, v1);
        v2 = 15;
        testDoubleHelper(v1, v2);
        testDoubleHelper(v2, v1);
        v1 = -1;
        v2 = 0;
        testDoubleHelper(v1, v2);
        testDoubleHelper(v2, v1);
        v1 = Double.MAX_VALUE;
        v2 = 0;
        testDoubleHelper(v1, v2);
        testDoubleHelper(v2, v1);
        v1 = 0;
        v2 = Double.MIN_VALUE;
        testDoubleHelper(v1, v2);
        testDoubleHelper(v2, v1);
    }

    static void testDoubleHelper(double v1, double v2) {
        assertTrue(INSTANCE.add_double(v1, v2) == (v1 + v2));
        assertTrue(INSTANCE.sub_double(v1, v2) == (v1 - v2));
        assertTrue(INSTANCE.mul_double(v1, v2) == (v1 * v2));
        assertTrue(INSTANCE.add_jdouble(v1, v2) == (v1 + v2));
        assertTrue(INSTANCE.sub_jdouble(v1, v2) == (v1 - v2));
        assertTrue(INSTANCE.mul_jdouble(v1, v2) == (v1 * v2));

        if (v2 != 0) {
            assertTrue(INSTANCE.div_double(v1, v2) == (v1 / v2));
            assertTrue(INSTANCE.div_jdouble(v1, v2) == (v1 / v2));
        }

        assertTrue(INSTANCE.neg_double(v1) == -v1);
        assertTrue(INSTANCE.neg_double(v2) == -v2);
        assertTrue(INSTANCE.neg_jdouble(v1) == -v1);
        assertTrue(INSTANCE.neg_jdouble(v2) == -v2);

        assertTrue(INSTANCE.eq_double(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_double(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_double(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_double(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_double(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_double(v1, v2) == (v1 != v2));
        assertTrue(INSTANCE.eq_jdouble(v1, v2) == (v1 == v2));
        assertTrue(INSTANCE.gt_jdouble(v1, v2) == (v1 > v2));
        assertTrue(INSTANCE.ge_jdouble(v1, v2) == (v1 >= v2));
        assertTrue(INSTANCE.lt_jdouble(v1, v2) == (v1 < v2));
        assertTrue(INSTANCE.le_jdouble(v1, v2) == (v1 <= v2));
        assertTrue(INSTANCE.ne_jdouble(v1, v2) == (v1 != v2));

        if (v1 >= Byte.MIN_VALUE && v1 <= Byte.MAX_VALUE) {
            assertTrue(INSTANCE.from_jdouble_to_int8_t(v1) == (byte) v1);
            assertTrue(INSTANCE.from_jdouble_to_jbyte(v1) == (byte) v1);
        }
        if (v1 >= 0 && v1 <= Byte.toUnsignedInt((byte) -1)) {
            assertTrue(INSTANCE.from_jdouble_to_uint8_t(v1) == (byte) v1);
        }

        if (v1 >= Short.MIN_VALUE && v1 <= Short.MAX_VALUE) {
            assertTrue(INSTANCE.from_jdouble_to_int16_t(v1) == (short) v1);
            assertTrue(INSTANCE.from_jdouble_to_jshort(v1) == (short) v1);
        }
        if (v1 >= 0 && v1 <= Short.toUnsignedInt((short) -1)) {
            assertTrue(INSTANCE.from_jdouble_to_uint16_t(v1) == (short) v1);
            assertTrue(INSTANCE.from_jdouble_to_jchar(v1) == (char) v1);
        }

        if (v1 >= Integer.MIN_VALUE && v1 <= Integer.MAX_VALUE) {
            assertTrue(INSTANCE.from_jdouble_to_int32_t(v1) == (int) v1);
            assertTrue(INSTANCE.from_jdouble_to_jint(v1) == (int) v1);
        }
        if (v1 >= 0 && v1 <= Integer.toUnsignedLong(-1)) {
            assertTrue(INSTANCE.from_jdouble_to_uint32_t(v1) == (int) v1);
        }

        if (v1 >= Long.MIN_VALUE && v1 <= Long.MAX_VALUE) {
            assertTrue(INSTANCE.from_jdouble_to_int64_t(v1) == (long) v1);
            assertTrue(INSTANCE.from_jdouble_to_jlong(v1) == (long) v1);
        }
        if (v1 >= 0 && v1 <= ((double) Long.MAX_VALUE) * 2 + 1) {
            assertTrue(INSTANCE.from_jdouble_to_uint64_t(v1) == (long) v1);
        }

        assertTrue(INSTANCE.from_jdouble_to_float(v1) == (float) v1);
        assertTrue(INSTANCE.from_jdouble_to_double(v1) == v1);
        assertTrue(INSTANCE.from_jdouble_to_bool(v1) == (v1 != 0));

        assertTrue(INSTANCE.from_jdouble_to_jfloat(v1) == (float) v1);
        assertTrue(INSTANCE.from_jdouble_to_jdouble(v1) == v1);

        assertTrue(INSTANCE.bitcast_double_to_jlong(v1) == Double.doubleToLongBits(v1));
    }

    @Test
    public void testBitCast() {
        Random rand = new Random();
        {
            byte v = (byte) rand.nextInt();
            assertTrue(INSTANCE.bitcast_int8_t_to_jbyte(v) == v);
            assertTrue(INSTANCE.bitcast_jbyte_to_int8_t(v) == v);
        }
        {
            short v = (short) rand.nextInt();
            assertTrue(INSTANCE.bitcast_int16_t_to_jshort(v) == v);
            assertTrue(INSTANCE.bitcast_jshort_to_int16_t(v) == v);
        }
        {
            char v = (char) rand.nextInt();
            assertTrue(INSTANCE.bitcast_uint16_t_to_jchar(v) == v);
            assertTrue(INSTANCE.bitcast_jchar_to_uint16_t(v) == v);
        }
        {
            int v = rand.nextInt();
            assertTrue(INSTANCE.bitcast_int32_t_to_jint(v) == v);
            assertTrue(INSTANCE.bitcast_jint_to_int32_t(v) == v);
            assertTrue(Float.compare(INSTANCE.bitcast_jint_to_float(v), Float.intBitsToFloat(v)) == 0);

            float f = rand.nextFloat();
            assertTrue(INSTANCE.bitcast_float_to_jint(f) == Float.floatToIntBits(f));
        }
        {
            long v = rand.nextLong();
            assertTrue(INSTANCE.bitcast_int64_t_to_jlong(v) == v);
            assertTrue(INSTANCE.bitcast_jlong_to_int64_t(v) == v);
            assertTrue(Double.compare(INSTANCE.bitcast_jlong_to_double(v), Double.longBitsToDouble(v)) == 0);

            float d = rand.nextFloat();
            assertTrue(INSTANCE.bitcast_double_to_jlong(d) == Double.doubleToLongBits(d));
        }
    }
}
