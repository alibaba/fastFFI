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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

public class LoadStoreTest {
    static final int ARRAY_SIZE = 16;
    LoadStoreTestFFILibrary.Struct1 struct1;
    LoadStoreTestFFILibrary.Struct2 struct2;
    LoadStoreTestFFILibrary library;
    @Before
    public void setUp() {
        library = LoadStoreTestFFILibrary.INSTANCE;
        struct1 = LoadStoreTestFFILibrary.Struct1.factory.create();
        struct2 = LoadStoreTestFFILibrary.Struct2.factory.create();
    }

    @After
    public void tearDown() {
        struct1.delete();
        struct2.delete();
    }

    @Test
    public void testLoadStore() {
        testLoadStoreHelper((byte) 0, false, (short) 0, '\0', 0, 0L, 0.0F, 0.0D);
        testLoadStoreHelper(Byte.MIN_VALUE, false, Short.MIN_VALUE,
                Character.MIN_VALUE, Integer.MIN_VALUE, Long.MIN_VALUE, Float.MIN_VALUE, Double.MIN_VALUE);
        testLoadStoreHelper(Byte.MAX_VALUE, true, Short.MAX_VALUE,
                Character.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, Float.MAX_VALUE, Double.MAX_VALUE);

        int i = 100;
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        while (i-- > 0) {
            testLoadStoreHelper((byte) rand.nextInt(), rand.nextBoolean(), (short) rand.nextInt(), (char) rand.nextInt(),
                    rand.nextInt(), rand.nextLong(), rand.nextFloat(), rand.nextDouble());
        }
    }

    public void testLoadStoreHelper(
            byte _byte,
            boolean _boolean,
            short _short,
            char _char,
            int _int,
            long _long,
            float _float,
            double _double) {

        library.set_Struct1_jchar(struct1, _char);
        library.set_Struct1_jboolean(struct1, _boolean);
        library.set_Struct1_bool(struct1, _boolean);
        library.set_Struct1_int8_t(struct1, _byte);
        library.set_Struct1_uint8_t(struct1, _byte);
        library.set_Struct1_jbyte(struct1, _byte);
        library.set_Struct1_int16_t(struct1, _short);
        library.set_Struct1_uint16_t(struct1, _short);
        library.set_Struct1_jshort(struct1, _short);
        library.set_Struct1_int32_t(struct1, _int);
        library.set_Struct1_uint32_t(struct1, _int);
        library.set_Struct1_jint(struct1, _int);
        library.set_Struct1_int64_t(struct1, _long);
        library.set_Struct1_uint64_t(struct1, _long);
        library.set_Struct1_jlong(struct1, _long);
        library.set_Struct1_float(struct1, _float);
        library.set_Struct1_jfloat(struct1, _float);
        library.set_Struct1_double(struct1, _double);
        library.set_Struct1_jdouble(struct1, _double);

        assertTrue(library.get_Struct1_jchar(struct1) == _char);
        assertTrue(library.get_Struct1_jboolean(struct1) == _boolean);
        assertTrue(library.get_Struct1_bool(struct1) == _boolean);
        assertTrue(library.get_Struct1_int8_t(struct1) == _byte);
        assertTrue(library.get_Struct1_uint8_t(struct1) == _byte);
        assertTrue(library.get_Struct1_jbyte(struct1) == _byte);
        assertTrue(library.get_Struct1_int16_t(struct1) == _short);
        assertTrue(library.get_Struct1_uint16_t(struct1) == _short);
        assertTrue(library.get_Struct1_jshort(struct1) == _short);
        assertTrue(library.get_Struct1_int32_t(struct1) == _int);
        assertTrue(library.get_Struct1_uint32_t(struct1) == _int);
        assertTrue(library.get_Struct1_jint(struct1) == _int);
        assertTrue(library.get_Struct1_int64_t(struct1) == _long);
        assertTrue(library.get_Struct1_uint64_t(struct1) == _long);
        assertTrue(library.get_Struct1_jlong(struct1) == _long);
        assertTrue(library.get_Struct1_float(struct1) == _float);
        assertTrue(library.get_Struct1_jfloat(struct1) == _float);
        assertTrue(library.get_Struct1_double(struct1) == _double);
        assertTrue(library.get_Struct1_jdouble(struct1) == _double);

        library.set_Struct2_jchar(struct2, _char);
        library.set_Struct2_jboolean(struct2, _boolean);
        library.set_Struct2_bool(struct2, _boolean);
        library.set_Struct2_int8_t(struct2, _byte);
        library.set_Struct2_uint8_t(struct2, _byte);
        library.set_Struct2_jbyte(struct2, _byte);
        library.set_Struct2_int16_t(struct2, _short);
        library.set_Struct2_uint16_t(struct2, _short);
        library.set_Struct2_jshort(struct2, _short);
        library.set_Struct2_int32_t(struct2, _int);
        library.set_Struct2_uint32_t(struct2, _int);
        library.set_Struct2_jint(struct2, _int);
        library.set_Struct2_int64_t(struct2, _long);
        library.set_Struct2_uint64_t(struct2, _long);
        library.set_Struct2_jlong(struct2, _long);
        library.set_Struct2_float(struct2, _float);
        library.set_Struct2_jfloat(struct2, _float);
        library.set_Struct2_double(struct2, _double);
        library.set_Struct2_jdouble(struct2, _double);

        assertTrue(library.get_Struct2_jchar(struct2) == _char);
        assertTrue(library.get_Struct2_jboolean(struct2) == _boolean);
        assertTrue(library.get_Struct2_bool(struct2) == _boolean);
        assertTrue(library.get_Struct2_int8_t(struct2) == _byte);
        assertTrue(library.get_Struct2_uint8_t(struct2) == _byte);
        assertTrue(library.get_Struct2_jbyte(struct2) == _byte);
        assertTrue(library.get_Struct2_int16_t(struct2) == _short);
        assertTrue(library.get_Struct2_uint16_t(struct2) == _short);
        assertTrue(library.get_Struct2_jshort(struct2) == _short);
        assertTrue(library.get_Struct2_int32_t(struct2) == _int);
        assertTrue(library.get_Struct2_uint32_t(struct2) == _int);
        assertTrue(library.get_Struct2_jint(struct2) == _int);
        assertTrue(library.get_Struct2_int64_t(struct2) == _long);
        assertTrue(library.get_Struct2_uint64_t(struct2) == _long);
        assertTrue(library.get_Struct2_jlong(struct2) == _long);
        assertTrue(library.get_Struct2_float(struct2) == _float);
        assertTrue(library.get_Struct2_jfloat(struct2) == _float);
        assertTrue(library.get_Struct2_double(struct2) == _double);
        assertTrue(library.get_Struct2_jdouble(struct2) == _double);
    }

    @Test
    public void testLoadStoreArrayElement() {

        ThreadLocalRandom rand = ThreadLocalRandom.current();
        IntStream.range(0, 100).forEach( i -> {
            testLoadStoreArrayElementHelper((byte) 0, false, (short) 0, '\0', 0, 0L,
                    0.0F, 0.0D, rand.nextInt(ARRAY_SIZE));
            testLoadStoreArrayElementHelper(Byte.MIN_VALUE, false, Short.MIN_VALUE,
                    Character.MIN_VALUE, Integer.MIN_VALUE, Long.MIN_VALUE, Float.MIN_VALUE,
                    Double.MIN_VALUE, rand.nextInt(ARRAY_SIZE));
            testLoadStoreArrayElementHelper(Byte.MAX_VALUE, true, Short.MAX_VALUE,
                    Character.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, Float.MAX_VALUE,
                    Double.MAX_VALUE, rand.nextInt(ARRAY_SIZE));
        });

        IntStream.range(0, 100).forEach( i -> {
            testLoadStoreArrayElementHelper((byte) 0, false, (short) 0, '\0', 0, 0L,
                    0.0F, 0.0D, rand.nextInt(ARRAY_SIZE));
            testLoadStoreArrayElementHelper(Byte.MIN_VALUE, false, Short.MIN_VALUE,
                    Character.MIN_VALUE, Integer.MIN_VALUE, Long.MIN_VALUE, Float.MIN_VALUE,
                    Double.MIN_VALUE, rand.nextInt(ARRAY_SIZE));
            testLoadStoreArrayElementHelper(Byte.MAX_VALUE, true, Short.MAX_VALUE,
                    Character.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, Float.MAX_VALUE,
                    Double.MAX_VALUE, rand.nextInt(ARRAY_SIZE));
        });

        int i = 100;
        while (i-- > 0) {
            IntStream.range(0, 100).forEach( idx -> {
                testLoadStoreArrayElementHelper((byte) rand.nextInt(), rand.nextBoolean(), (short) rand.nextInt(), (char) rand.nextInt(),
                        rand.nextInt(), rand.nextLong(), rand.nextFloat(), rand.nextDouble(), rand.nextInt(ARRAY_SIZE));
            });
        }
    }

    public void testLoadStoreArrayElementHelper(
            byte _byte,
            boolean _boolean,
            short _short,
            char _char,
            int _int,
            long _long,
            float _float,
            double _double,
            int index) {

        library.set_array_element_Struct1_jchar(struct1, index, _char);
        library.set_array_element_Struct1_jboolean(struct1, index, _boolean);
        library.set_array_element_Struct1_bool(struct1, index, _boolean);
        library.set_array_element_Struct1_int8_t(struct1, index, _byte);
        library.set_array_element_Struct1_uint8_t(struct1, index, _byte);
        library.set_array_element_Struct1_jbyte(struct1, index, _byte);
        library.set_array_element_Struct1_int16_t(struct1, index, _short);
        library.set_array_element_Struct1_uint16_t(struct1, index, _short);
        library.set_array_element_Struct1_jshort(struct1, index, _short);
        library.set_array_element_Struct1_int32_t(struct1, index, _int);
        library.set_array_element_Struct1_uint32_t(struct1, index, _int);
        library.set_array_element_Struct1_jint(struct1, index, _int);
        library.set_array_element_Struct1_int64_t(struct1, index, _long);
        library.set_array_element_Struct1_uint64_t(struct1, index, _long);
        library.set_array_element_Struct1_jlong(struct1, index, _long);
        library.set_array_element_Struct1_float(struct1, index, _float);
        library.set_array_element_Struct1_jfloat(struct1, index, _float);
        library.set_array_element_Struct1_double(struct1, index, _double);
        library.set_array_element_Struct1_jdouble(struct1, index, _double);

        assertTrue(library.get_array_element_Struct1_jchar(struct1, index) == _char);
        assertTrue(library.get_array_element_Struct1_jboolean(struct1, index) == _boolean);
        assertTrue(library.get_array_element_Struct1_bool(struct1, index) == _boolean);
        assertTrue(library.get_array_element_Struct1_int8_t(struct1, index) == _byte);
        assertTrue(library.get_array_element_Struct1_uint8_t(struct1, index) == _byte);
        assertTrue(library.get_array_element_Struct1_jbyte(struct1, index) == _byte);
        assertTrue(library.get_array_element_Struct1_int16_t(struct1, index) == _short);
        assertTrue(library.get_array_element_Struct1_uint16_t(struct1, index) == _short);
        assertTrue(library.get_array_element_Struct1_jshort(struct1, index) == _short);
        assertTrue(library.get_array_element_Struct1_int32_t(struct1, index) == _int);
        assertTrue(library.get_array_element_Struct1_uint32_t(struct1, index) == _int);
        assertTrue(library.get_array_element_Struct1_jint(struct1, index) == _int);
        assertTrue(library.get_array_element_Struct1_int64_t(struct1, index) == _long);
        assertTrue(library.get_array_element_Struct1_uint64_t(struct1, index) == _long);
        assertTrue(library.get_array_element_Struct1_jlong(struct1, index) == _long);
        assertTrue(library.get_array_element_Struct1_float(struct1, index) == _float);
        assertTrue(library.get_array_element_Struct1_jfloat(struct1, index) == _float);
        assertTrue(library.get_array_element_Struct1_double(struct1, index) == _double);
        assertTrue(library.get_array_element_Struct1_jdouble(struct1, index) == _double);

        library.set_array_element_Struct2_jchar(struct2, index, _char);
        library.set_array_element_Struct2_jboolean(struct2, index, _boolean);
        library.set_array_element_Struct2_bool(struct2, index, _boolean);
        library.set_array_element_Struct2_int8_t(struct2, index, _byte);
        library.set_array_element_Struct2_uint8_t(struct2, index, _byte);
        library.set_array_element_Struct2_jbyte(struct2, index, _byte);
        library.set_array_element_Struct2_int16_t(struct2, index, _short);
        library.set_array_element_Struct2_uint16_t(struct2, index, _short);
        library.set_array_element_Struct2_jshort(struct2, index, _short);
        library.set_array_element_Struct2_int32_t(struct2, index, _int);
        library.set_array_element_Struct2_uint32_t(struct2, index, _int);
        library.set_array_element_Struct2_jint(struct2, index, _int);
        library.set_array_element_Struct2_int64_t(struct2, index, _long);
        library.set_array_element_Struct2_uint64_t(struct2, index, _long);
        library.set_array_element_Struct2_jlong(struct2, index, _long);
        library.set_array_element_Struct2_float(struct2, index, _float);
        library.set_array_element_Struct2_jfloat(struct2, index, _float);
        library.set_array_element_Struct2_double(struct2, index, _double);
        library.set_array_element_Struct2_jdouble(struct2, index, _double);

        assertTrue(library.get_array_element_Struct2_jchar(struct2, index) == _char);
        assertTrue(library.get_array_element_Struct2_jboolean(struct2, index) == _boolean);
        assertTrue(library.get_array_element_Struct2_bool(struct2, index) == _boolean);
        assertTrue(library.get_array_element_Struct2_int8_t(struct2, index) == _byte);
        assertTrue(library.get_array_element_Struct2_uint8_t(struct2, index) == _byte);
        assertTrue(library.get_array_element_Struct2_jbyte(struct2, index) == _byte);
        assertTrue(library.get_array_element_Struct2_int16_t(struct2, index) == _short);
        assertTrue(library.get_array_element_Struct2_uint16_t(struct2, index) == _short);
        assertTrue(library.get_array_element_Struct2_jshort(struct2, index) == _short);
        assertTrue(library.get_array_element_Struct2_int32_t(struct2, index) == _int);
        assertTrue(library.get_array_element_Struct2_uint32_t(struct2, index) == _int);
        assertTrue(library.get_array_element_Struct2_jint(struct2, index) == _int);
        assertTrue(library.get_array_element_Struct2_int64_t(struct2, index) == _long);
        assertTrue(library.get_array_element_Struct2_uint64_t(struct2, index) == _long);
        assertTrue(library.get_array_element_Struct2_jlong(struct2, index) == _long);
        assertTrue(library.get_array_element_Struct2_float(struct2, index) == _float);
        assertTrue(library.get_array_element_Struct2_jfloat(struct2, index) == _float);
        assertTrue(library.get_array_element_Struct2_double(struct2, index) == _double);
        assertTrue(library.get_array_element_Struct2_jdouble(struct2, index) == _double);
    }
}
