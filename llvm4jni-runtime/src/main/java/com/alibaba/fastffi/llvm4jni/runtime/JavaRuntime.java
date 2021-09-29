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

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * A Java Runtime that could be used during code generation and runtime.
 * Remember to keep this class with clean dependency, i.e., no dependency of Truffle/Sulong/Graal.
 */
public class JavaRuntime {
    public static final int I16_MASK = 0xffff;
    public static final long I32_MASK = 0xffffffffL;
    public static final int I8_MASK = 0xff;

    public static final double DOUBLE_LEADING_BIT = 0x1.0p63;
    public static final float FLOAT_LEADING_BIT = 0x1.0p63f;

    public static final float MAX_INT_AS_FLOAT = Integer.MAX_VALUE;
    public static final double MAX_INT_AS_DOUBLE = Integer.MAX_VALUE;

    public static final float MAX_LONG_AS_FLOAT = Long.MAX_VALUE;
    public static final double MAX_LONG_AS_DOUBLE = Long.MAX_VALUE;

    public static final String DESCRIPTOR = JavaRuntime.class.getName().replace('.', '/');

    public static final Unsafe UNSAFE;
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            System.out.println("Cannot obtain UNSAFE");
            throw new RuntimeException(e);
        }
    }

    public static byte getByte(long address) {
        return UNSAFE.getByte(address);
    }

    public static boolean getBoolean(long address) {
        return UNSAFE.getByte(address) != 0;
    }

    public static short getShort(long address) {
        return UNSAFE.getShort(address);
    }

    public static char getChar(long address) {
        return UNSAFE.getChar(address);
    }

    public static int getInt(long address) {
        return UNSAFE.getInt(address);
    }

    public static long getLong(long address) {
        return UNSAFE.getLong(address);
    }

    public static float getFloat(long address) {
        return UNSAFE.getFloat(address);
    }

    public static double getDouble(long address) {
        return UNSAFE.getDouble(address);
    }

    public static void putByte(long address, byte value) {
        UNSAFE.putByte(address, value);
    }

    public static void putBoolean(long address, boolean value) {
        UNSAFE.putByte(address, (byte) (value ? 1 : 0));
    }

    public static void putShort(long address, short value) {
        UNSAFE.putShort(address, value);
    }

    public static void putChar(long address, char value) {
        UNSAFE.putChar(address, value);
    }

    public static void putInt(long address, int value) {
        UNSAFE.putInt(address, value);
    }

    public static void putLong(long address, long value) {
        UNSAFE.putLong(address, value);
    }

    public static void putFloat(long address, float value) {
        UNSAFE.putFloat(address, value);
    }

    public static void putDouble(long address, double value) {
        UNSAFE.putDouble(address, value);
    }

    /**
     * We only support 1.8+
     * @return
     */
    public static int jniGetVersion() {
        return 0x00010006;
    }

    public static boolean jniIsAssignableFrom(Object class1, Object class2) {
        return ((Class<?>) class1).isAssignableFrom((Class<?>) class2);
    }

    public static void jniFillObjectArray(Object[] array, Object value) {
        if (value != null) {
            Arrays.fill(array, value);
        }
    }

    public static Object jniAllocObject(Class<?> cls) {
        try {
            return UNSAFE.allocateInstance(cls);
        } catch (InstantiationException e) {
            return null;
        }
    }

    public static int select(boolean cond, int value1, int value2) {
        return cond ? value1 : value2;
    }

    public static long select(boolean cond, long value1, long value2) {
        return cond ? value1 : value2;
    }

    public static byte ctpop(byte value) {
        return (byte) Integer.bitCount(0xFF & value);
    }

    public static short ctpop(short value) {
        return (short) Integer.bitCount(0xFFFF & value);
    }

    public static int ctpop(int value) {
        return Integer.bitCount(value);
    }

    public static long ctpop(long value) {
        return Long.bitCount(value);
    }

    public static byte ctlz(byte value) {
        return (byte) (Integer.numberOfLeadingZeros(0xFF & value) - 24);
    }

    public static short ctlz(short value) {
        return (short) (Integer.numberOfLeadingZeros(0xFFFF & value) - 16);
    }

    public static int ctlz(int value) {
        return Integer.numberOfLeadingZeros(value);
    }

    public static long ctlz(long value) {
        return Long.numberOfLeadingZeros(value);
    }

    public static byte cttz(byte value) {
        return (byte) Integer.numberOfTrailingZeros(0xFFFFFF00 | value);
    }

    public static short cttz(short value) {
        return (short) Integer.numberOfTrailingZeros(0xFFFF0000 | value);
    }

    public static int cttz(int value) {
        return Integer.numberOfTrailingZeros(value);
    }

    public static long cttz(long value) {
        return Long.numberOfTrailingZeros(value);
    }

    public static Stack getStack() {
        return Stack.getStack();
    }

    public static void free(long address) {
        if (address != 0) UNSAFE.freeMemory(address);
    }

    public static void memset(long address, byte value, long length) {
        UNSAFE.setMemory(address, length ,value);
    }

    public static void moveMemory(long sourceAddress, long targetAddress, long length) {
        long sourceEnd = sourceAddress + length;
        long targetEnd = targetAddress + length;
        if (sourceAddress < targetAddress) {
            if (sourceEnd <= targetAddress) {
                UNSAFE.copyMemory(sourceAddress, targetAddress, length);
            } else {
                // sourceAddr < targetAddr < sourceEnd < targetEnd
                long deltaEnd = targetEnd - sourceEnd;
                UNSAFE.copyMemory(sourceEnd - deltaEnd, sourceEnd, deltaEnd);
                UNSAFE.copyMemory(sourceAddress, targetAddress, length - deltaEnd);
            }
        } else { //
            if (targetEnd <= sourceAddress) {
                UNSAFE.copyMemory(sourceAddress, targetAddress, length);
            } else {
                // targetAddr < sourceAddr < targetEnd < sourceEnd;
                long delta = sourceAddress - targetAddress;
                UNSAFE.copyMemory(sourceAddress, targetAddress, delta);
                UNSAFE.copyMemory(sourceAddress + delta, sourceAddress, length - delta);
            }
        }
    }

    public static void copyMemory(long sourceAddress, long targetAddress, long length) {
        UNSAFE.copyMemory(sourceAddress, targetAddress, length);
    }

    public static long allocate(long size) {
        return UNSAFE.allocateMemory(size);
    }

    public static byte udiv(byte left, byte right) {
        return (byte) (Byte.toUnsignedInt(left) / Byte.toUnsignedInt(right));
    }

    public static short udiv(short left, short right) {
        return (short) (Short.toUnsignedInt(left) / Short.toUnsignedInt(right));
    }

    public static int udiv(int left, int right) {
        return Integer.divideUnsigned(left, right);
    }

    public static long udiv(long left, long right) {
        return Long.divideUnsigned(left, right);
    }

    public static byte urem(byte left, byte right) {
        return (byte) (Byte.toUnsignedInt(left) % Byte.toUnsignedInt(right));
    }

    public static short urem(short left, short right) {
        return (short) (Short.toUnsignedInt(left) % Short.toUnsignedInt(right));
    }

    public static int urem(int left, int right) {
        return Integer.remainderUnsigned(left, right);
    }

    public static long urem(long left, long right) {
        return Long.remainderUnsigned(left, right);
    }

    // Floating point comparison

    public static boolean oeq(double val1, double val2) {
        return val1 == val2;
    }
    public static boolean oeq(float val1, float val2) {
        return val1 == val2;
    }

    public static boolean oge(double val1, double val2) {
        return val1 >= val2;
    }
    public static boolean oge(float val1, float val2) {
        return val1 >= val2;
    }

    public static boolean ogt(double val1, double val2) {
        return val1 >= val2;
    }
    public static boolean ogt(float val1, float val2) {
        return val1 >= val2;
    }

    public static boolean ole(double val1, double val2) {
        return val1 <= val2;
    }
    public static boolean ole(float val1, float val2) {
        return val1 <= val2;
    }

    public static boolean olt(double val1, double val2) {
        return val1 < val2;
    }
    public static boolean olt(float val1, float val2) {
        return val1 < val2;
    }

    public static boolean one(double val1, double val2) {
        return val1 != val2;
    }
    public static boolean one(float val1, float val2) {
        return val1 != val2;
    }

    public static boolean ord(double val1, double val2) {
        return !Double.isNaN(val1) && !Double.isNaN(val2);
    }
    public static boolean ord(float val1, float val2) {
        return !Float.isNaN(val1) && !Float.isNaN(val2);
    }

    public static boolean ueq(double val1, double val2) {
        return !(val1 != val2);
    }
    public static boolean ueq(float val1, float val2) {
        return !(val1 != val2);
    }

    public static boolean uge(double val1, double val2) {
        return !(val1 < val2);
    }
    public static boolean uge(float val1, float val2) {
        return !(val1 < val2);
    }

    public static boolean ugt(double val1, double val2) {
        return !(val1 <= val2);
    }
    public static boolean ugt(float val1, float val2) {
        return !(val1 <= val2);
    }

    public static boolean ule(double val1, double val2) {
        return !(val1 > val2);
    }
    public static boolean ule(float val1, float val2) {
        return !(val1 > val2);
    }

    public static boolean ult(double val1, double val2) {
        return !(val1 >= val2);
    }
    public static boolean ult(float val1, float val2) {
        return !(val1 >= val2);
    }

    public static boolean une(double val1, double val2) {
        return !(val1 == val2);
    }
    public static boolean une(float val1, float val2) {
        return !(val1 == val2);
    }

    public static boolean uno(double val1, double val2) {
        return Double.isNaN(val1) || Double.isNaN(val2);
    }
    public static boolean uno(float val1, float val2) {
        return Float.isNaN(val1) || Float.isNaN(val2);
    }

    public static int floatToIntBits(float value) {
        return Float.floatToIntBits(value);
    }

    public static float intBitsToFloat(int value) {
        return Float.intBitsToFloat(value);
    }

    public static long doubleToLongBits(double value) {
        return Double.doubleToLongBits(value);
    }

    public static double longBitsToDouble(long value) {
        return Double.longBitsToDouble(value);
    }

    public static int compareUnsigned(int val1, int val2) {
        return Integer.compareUnsigned(val1, val2);
    }

    public static int compareUnsigned(long val1, long val2) {
        return Long.compareUnsigned(val1, val2);
    }

    public static double uitofp_i1tod(boolean from) {
        return from ? 1 : 0;
    }

    public static double uitofp_i8tod(byte from) {
        return from & I8_MASK;
    }

    public static double uitofp_i16tod(short from) {
        return from & I16_MASK;
    }

    public static double uitofp_i32tod(int from) {
        return from & I32_MASK;
    }

    public static double uitofp_i64tod(long from) {
        double val = from & Long.MAX_VALUE;
        if (from < 0) {
            val += DOUBLE_LEADING_BIT;
        }
        return val;
    }

    public static float uitofp_i1tof(boolean from) {
        return from ? 1 : 0;
    }

    public static float uitofp_i8tof(byte from) {
        return from & I8_MASK;
    }

    public static float uitofp_i16tof(short from) {
        return from & I16_MASK;
    }

    public static float uitofp_i32tof(int from) {
        return from & I32_MASK;
    }

    public static float uitofp_i64tof(long from) {
        float val = from & Long.MAX_VALUE;
        if (from < 0) {
            val += FLOAT_LEADING_BIT;
        }
        return val;
    }

    public static boolean fptoui_dtoi1(double from) {
        return from != 0 ? true : false;
    }

    public static byte fptoui_dtoi8(double from) {
        return (byte) from;
    }

    public static short fptoui_dtoi16(double from) {
        return (short) from;
    }

    public static int fptoui_dtoi32(double from) {
        if (from < MAX_INT_AS_DOUBLE) {
            return (int) from;
        } else {
            return (int) (from + Integer.MIN_VALUE) - Integer.MIN_VALUE;
        }
    }

    public static long fptoui_dtoi64(double from) {
        if (from < MAX_LONG_AS_DOUBLE) {
            return (long) from;
        } else {
            return (long) (from + Long.MIN_VALUE) - Long.MIN_VALUE;
        }
    }

    public static boolean fptoui_ftoi1(float from) {
        return from != 0 ? true : false;
    }

    public static byte fptoui_ftoi8(float from) {
        return (byte) from;
    }

    public static short fptoui_ftoi16(float from) {
        return (short) from;
    }

    public static int fptoui_ftoi32(float from) {
        if (from < MAX_INT_AS_FLOAT) {
            return (int) from;
        } else {
            return (int) (from + Integer.MIN_VALUE) - Integer.MIN_VALUE;
        }
    }

    public static long fptoui_ftoi64(float from) {
        if (from < MAX_LONG_AS_FLOAT) {
            return (long) from;
        } else {
            return (long) (from + Long.MIN_VALUE) - Long.MIN_VALUE;
        }
    }

    public static long strlen(final long base) {
        long p = base;
        while (true) {
            byte b = UNSAFE.getByte(p);
            if (b == 0) {
                return p - base;
            }
            p++;
        }
    }

    public static int bcmp(final long base1, final long base2, long size) {
        for (long i = 0; i < size; i++) {
            int ret = getByte(base1 + i) - getByte(base2 + i);
            if (ret != 0) {
                return ret;
            }
        }
        return 0;
    }

    /**
     * xchg: *ptr = val
     * add: *ptr = *ptr + val
     * sub: *ptr = *ptr - val
     * and: *ptr = *ptr & val
     * nand: *ptr = ~(*ptr & val)
     * or: *ptr = *ptr | val
     * xor: *ptr = *ptr ^ val
     * max: *ptr = *ptr > val ? *ptr : val (using a signed comparison)
     * min: *ptr = *ptr < val ? *ptr : val (using a signed comparison)
     * umax: *ptr = *ptr > val ? *ptr : val (using an unsigned comparison)
     * umin: *ptr = *ptr < val ? *ptr : val (using an unsigned comparison)
     * fadd: *ptr = *ptr + val (using floating point arithmetic)
     * fsub: *ptr = *ptr - val (using floating point arithmetic)
     * @param address
     * @param value
     * @return
     */
    public static long atomic_xchg(long address, long value) {
        return UNSAFE.getAndSetLong(null, address, value);
    }

    public static int atomic_xchg(long address, int value) {
        return UNSAFE.getAndSetInt(null, address, value);
    }

    public static long atomic_add(long address, long value) {
        return UNSAFE.getAndAddLong(null, address, value);
    }

    public static int atomic_add(long address, int value) {
        return UNSAFE.getAndAddInt(null, address, value);
    }

    public static long atomic_sub(long address, long value) {
        long v;
        do {
            v = UNSAFE.getLongVolatile(null, address);
        } while (!UNSAFE.compareAndSwapLong(null, address, v, v - value));
        return v;
    }

    public static int atomic_sub(long address, int value) {
        int v;
        do {
            v = UNSAFE.getIntVolatile(null, address);
        } while (!UNSAFE.compareAndSwapInt(null, address, v, v - value));
        return v;
    }

    public static long atomic_and(long address, long value) {
        long v;
        do {
            v = UNSAFE.getLongVolatile(null, address);
        } while (!UNSAFE.compareAndSwapLong(null, address, v, v & value));
        return v;
    }

    public static int atomic_and(long address, int value) {
        int v;
        do {
            v = UNSAFE.getIntVolatile(null, address);
        } while (!UNSAFE.compareAndSwapInt(null, address, v, v & value));
        return v;
    }

    public static long atomic_or(long address, long value) {
        long v;
        do {
            v = UNSAFE.getLongVolatile(null, address);
        } while (!UNSAFE.compareAndSwapLong(null, address, v, v | value));
        return v;
    }

    public static int atomic_or(long address, int value) {
        int v;
        do {
            v = UNSAFE.getIntVolatile(null, address);
        } while (!UNSAFE.compareAndSwapInt(null, address, v, v | value));
        return v;
    }

    public static long atomic_xor(long address, long value) {
        long v;
        do {
            v = UNSAFE.getLongVolatile(null, address);
        } while (!UNSAFE.compareAndSwapLong(null, address, v, v ^ value));
        return v;
    }

    public static int atomic_xor(long address, int value) {
        int v;
        do {
            v = UNSAFE.getIntVolatile(null, address);
        } while (!UNSAFE.compareAndSwapInt(null, address, v, v ^ value));
        return v;
    }

    public static int cmpxchg(long address, int expect, int newValue) {
        return UNSAFE.compareAndSwapInt(null, address, expect, newValue) ? expect : newValue;
    }
}
