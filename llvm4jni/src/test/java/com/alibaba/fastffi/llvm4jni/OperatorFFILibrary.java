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

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeFactory;

@FFIGen(library = "llvm4jni-test")
@CXXHead("llvm4jni_test.hpp")
@FFILibrary(value = "llvm4jni::operators", namespace = "llvm4jni")
public interface OperatorFFILibrary {

    OperatorFFILibrary INSTANCE = FFITypeFactory.getLibrary(OperatorFFILibrary.class);

    byte add_jbyte(byte v1, byte v2);
    short add_jshort(short v1, short v2);
    int add_jint(int v1, int v2);
    long add_jlong(long v1, long v2);
    float add_jfloat(float v1, float v2);
    double add_jdouble(double v1, double v2);
    byte add_int8_t(byte v1, byte v2);
    byte add_uint8_t(byte v1, byte v2);
    short add_int16_t(short v1, short v2);
    short add_uint16_t(short v1, short v2);
    int add_int32_t(int v1, int v2);
    int add_uint32_t(int v1, int v2);
    long add_int64_t(long v1, long v2);
    long add_uint64_t(long v1, long v2);
    float add_float(float v1, float v2);
    double add_double(double v1, double v2);

    byte sub_jbyte(byte v1, byte v2);
    short sub_jshort(short v1, short v2);
    int sub_jint(int v1, int v2);
    long sub_jlong(long v1, long v2);
    float sub_jfloat(float v1, float v2);
    double sub_jdouble(double v1, double v2);
    byte sub_int8_t(byte v1, byte v2);
    byte sub_uint8_t(byte v1, byte v2);
    short sub_int16_t(short v1, short v2);
    short sub_uint16_t(short v1, short v2);
    int sub_int32_t(int v1, int v2);
    int sub_uint32_t(int v1, int v2);
    long sub_int64_t(long v1, long v2);
    long sub_uint64_t(long v1, long v2);
    float sub_float(float v1, float v2);
    double sub_double(double v1, double v2);

    byte mul_jbyte(byte v1, byte v2);
    short mul_jshort(short v1, short v2);
    int mul_jint(int v1, int v2);
    long mul_jlong(long v1, long v2);
    float mul_jfloat(float v1, float v2);
    double mul_jdouble(double v1, double v2);
    byte mul_int8_t(byte v1, byte v2);
    byte mul_uint8_t(byte v1, byte v2);
    short mul_int16_t(short v1, short v2);
    short mul_uint16_t(short v1, short v2);
    int mul_int32_t(int v1, int v2);
    int mul_uint32_t(int v1, int v2);
    long mul_int64_t(long v1, long v2);
    long mul_uint64_t(long v1, long v2);
    float mul_float(float v1, float v2);
    double mul_double(double v1, double v2);

    byte div_jbyte(byte v1, byte v2);
    short div_jshort(short v1, short v2);
    int div_jint(int v1, int v2);
    long div_jlong(long v1, long v2);
    float div_jfloat(float v1, float v2);
    double div_jdouble(double v1, double v2);
    byte div_int8_t(byte v1, byte v2);
    byte div_uint8_t(byte v1, byte v2);
    short div_int16_t(short v1, short v2);
    short div_uint16_t(short v1, short v2);
    int div_int32_t(int v1, int v2);
    int div_uint32_t(int v1, int v2);
    long div_int64_t(long v1, long v2);
    long div_uint64_t(long v1, long v2);
    float div_float(float v1, float v2);
    double div_double(double v1, double v2);

    byte rem_jbyte(byte v1, byte v2);
    short rem_jshort(short v1, short v2);
    int rem_jint(int v1, int v2);
    long rem_jlong(long v1, long v2);
    byte rem_int8_t(byte v1, byte v2);
    byte rem_uint8_t(byte v1, byte v2);
    short rem_int16_t(short v1, short v2);
    short rem_uint16_t(short v1, short v2);
    int rem_int32_t(int v1, int v2);
    int rem_uint32_t(int v1, int v2);
    long rem_int64_t(long v1, long v2);
    long rem_uint64_t(long v1, long v2);

    byte and_jbyte(byte v1, byte v2);
    short and_jshort(short v1, short v2);
    int and_jint(int v1, int v2);
    long and_jlong(long v1, long v2);
    byte and_int8_t(byte v1, byte v2);
    byte and_uint8_t(byte v1, byte v2);
    short and_int16_t(short v1, short v2);
    short and_uint16_t(short v1, short v2);
    int and_int32_t(int v1, int v2);
    int and_uint32_t(int v1, int v2);
    long and_int64_t(long v1, long v2);
    long and_uint64_t(long v1, long v2);

    byte or_jbyte(byte v1, byte v2);
    short or_jshort(short v1, short v2);
    int or_jint(int v1, int v2);
    long or_jlong(long v1, long v2);
    byte or_int8_t(byte v1, byte v2);
    byte or_uint8_t(byte v1, byte v2);
    short or_int16_t(short v1, short v2);
    short or_uint16_t(short v1, short v2);
    int or_int32_t(int v1, int v2);
    int or_uint32_t(int v1, int v2);
    long or_int64_t(long v1, long v2);
    long or_uint64_t(long v1, long v2);

    byte xor_jbyte(byte v1, byte v2);
    short xor_jshort(short v1, short v2);
    int xor_jint(int v1, int v2);
    long xor_jlong(long v1, long v2);
    byte xor_int8_t(byte v1, byte v2);
    byte xor_uint8_t(byte v1, byte v2);
    short xor_int16_t(short v1, short v2);
    short xor_uint16_t(short v1, short v2);
    int xor_int32_t(int v1, int v2);
    int xor_uint32_t(int v1, int v2);
    long xor_int64_t(long v1, long v2);
    long xor_uint64_t(long v1, long v2);

    byte shr_jbyte(byte v1, byte v2);
    short shr_jshort(short v1, short v2);
    int shr_jint(int v1, int v2);
    long shr_jlong(long v1, long v2);
    byte shr_int8_t(byte v1, byte v2);
    byte shr_uint8_t(byte v1, byte v2);
    short shr_int16_t(short v1, short v2);
    short shr_uint16_t(short v1, short v2);
    int shr_int32_t(int v1, int v2);
    int shr_uint32_t(int v1, int v2);
    long shr_int64_t(long v1, long v2);
    long shr_uint64_t(long v1, long v2);

    byte shl_jbyte(byte v1, byte v2);
    short shl_jshort(short v1, short v2);
    int shl_jint(int v1, int v2);
    long shl_jlong(long v1, long v2);
    byte shl_int8_t(byte v1, byte v2);
    byte shl_uint8_t(byte v1, byte v2);
    short shl_int16_t(short v1, short v2);
    short shl_uint16_t(short v1, short v2);
    int shl_int32_t(int v1, int v2);
    int shl_uint32_t(int v1, int v2);
    long shl_int64_t(long v1, long v2);
    long shl_uint64_t(long v1, long v2);

    byte neg_jbyte(byte v1);
    short neg_jshort(short v1);
    int neg_jint(int v1);
    long neg_jlong(long v1);
    float neg_float(float v1);
    double neg_double(double v1);

    byte neg_int8_t(byte v1);
    byte neg_uint8_t(byte v1);
    short neg_int16_t(short v1);
    short neg_uint16_t(short v1);
    int neg_int32_t(int v1);
    int neg_uint32_t(int v1);
    long neg_int64_t(long v1);
    long neg_uint64_t(long v1);
    float neg_jfloat(float v1);
    double neg_jdouble(double v1);


    // comparator
    boolean eq_jbyte(byte v1, byte v2);
    boolean eq_jshort(short v1, short v2);
    boolean eq_jint(int v1, int v2);
    boolean eq_jlong(long v1, long v2);
    boolean eq_jfloat(float v1, float v2);
    boolean eq_jdouble(double v1, double v2);
    boolean eq_int8_t(byte v1, byte v2);
    boolean eq_uint8_t(byte v1, byte v2);
    boolean eq_int16_t(short v1, short v2);
    boolean eq_uint16_t(short v1, short v2);
    boolean eq_int32_t(int v1, int v2);
    boolean eq_uint32_t(int v1, int v2);
    boolean eq_int64_t(long v1, long v2);
    boolean eq_uint64_t(long v1, long v2);
    boolean eq_float(float v1, float v2);
    boolean eq_double(double v1, double v2);

    boolean gt_jbyte(byte v1, byte v2);
    boolean gt_jshort(short v1, short v2);
    boolean gt_jint(int v1, int v2);
    boolean gt_jlong(long v1, long v2);
    boolean gt_jfloat(float v1, float v2);
    boolean gt_jdouble(double v1, double v2);
    boolean gt_int8_t(byte v1, byte v2);
    boolean gt_uint8_t(byte v1, byte v2);
    boolean gt_int16_t(short v1, short v2);
    boolean gt_uint16_t(short v1, short v2);
    boolean gt_int32_t(int v1, int v2);
    boolean gt_uint32_t(int v1, int v2);
    boolean gt_int64_t(long v1, long v2);
    boolean gt_uint64_t(long v1, long v2);
    boolean gt_float(float v1, float v2);
    boolean gt_double(double v1, double v2);

    boolean ge_jbyte(byte v1, byte v2);
    boolean ge_jshort(short v1, short v2);
    boolean ge_jint(int v1, int v2);
    boolean ge_jlong(long v1, long v2);
    boolean ge_jfloat(float v1, float v2);
    boolean ge_jdouble(double v1, double v2);
    boolean ge_int8_t(byte v1, byte v2);
    boolean ge_uint8_t(byte v1, byte v2);
    boolean ge_int16_t(short v1, short v2);
    boolean ge_uint16_t(short v1, short v2);
    boolean ge_int32_t(int v1, int v2);
    boolean ge_uint32_t(int v1, int v2);
    boolean ge_int64_t(long v1, long v2);
    boolean ge_uint64_t(long v1, long v2);
    boolean ge_float(float v1, float v2);
    boolean ge_double(double v1, double v2);

    boolean le_jbyte(byte v1, byte v2);
    boolean le_jshort(short v1, short v2);
    boolean le_jint(int v1, int v2);
    boolean le_jlong(long v1, long v2);
    boolean le_jfloat(float v1, float v2);
    boolean le_jdouble(double v1, double v2);
    boolean le_int8_t(byte v1, byte v2);
    boolean le_uint8_t(byte v1, byte v2);
    boolean le_int16_t(short v1, short v2);
    boolean le_uint16_t(short v1, short v2);
    boolean le_int32_t(int v1, int v2);
    boolean le_uint32_t(int v1, int v2);
    boolean le_int64_t(long v1, long v2);
    boolean le_uint64_t(long v1, long v2);
    boolean le_float(float v1, float v2);
    boolean le_double(double v1, double v2);

    boolean lt_jbyte(byte v1, byte v2);
    boolean lt_jshort(short v1, short v2);
    boolean lt_jint(int v1, int v2);
    boolean lt_jlong(long v1, long v2);
    boolean lt_jfloat(float v1, float v2);
    boolean lt_jdouble(double v1, double v2);
    boolean lt_int8_t(byte v1, byte v2);
    boolean lt_uint8_t(byte v1, byte v2);
    boolean lt_int16_t(short v1, short v2);
    boolean lt_uint16_t(short v1, short v2);
    boolean lt_int32_t(int v1, int v2);
    boolean lt_uint32_t(int v1, int v2);
    boolean lt_int64_t(long v1, long v2);
    boolean lt_uint64_t(long v1, long v2);
    boolean lt_float(float v1, float v2);
    boolean lt_double(double v1, double v2);

    boolean ne_jbyte(byte v1, byte v2);
    boolean ne_jshort(short v1, short v2);
    boolean ne_jint(int v1, int v2);
    boolean ne_jlong(long v1, long v2);
    boolean ne_jfloat(float v1, float v2);
    boolean ne_jdouble(double v1, double v2);
    boolean ne_int8_t(byte v1, byte v2);
    boolean ne_uint8_t(byte v1, byte v2);
    boolean ne_int16_t(short v1, short v2);
    boolean ne_uint16_t(short v1, short v2);
    boolean ne_int32_t(int v1, int v2);
    boolean ne_uint32_t(int v1, int v2);
    boolean ne_int64_t(long v1, long v2);
    boolean ne_uint64_t(long v1, long v2);
    boolean ne_float(float v1, float v2);
    boolean ne_double(double v1, double v2);

    // select
    byte select_eq_jbyte(byte v1, byte v2);
    short select_eq_jshort(short v1, short v2);
    int select_eq_jint(int v1, int v2);
    long select_eq_jlong(long v1, long v2);
    float select_eq_jfloat(float v1, float v2);
    double select_eq_jdouble(double v1, double v2);
    byte select_eq_int8_t(byte v1, byte v2);
    byte select_eq_uint8_t(byte v1, byte v2);
    short select_eq_int16_t(short v1, short v2);
    short select_eq_uint16_t(short v1, short v2);
    int select_eq_int32_t(int v1, int v2);
    int select_eq_uint32_t(int v1, int v2);
    long select_eq_int64_t(long v1, long v2);
    long select_eq_uint64_t(long v1, long v2);
    float select_eq_float(float v1, float v2);
    double select_eq_double(double v1, double v2);

    byte select_gt_jbyte(byte v1, byte v2);
    short select_gt_jshort(short v1, short v2);
    int select_gt_jint(int v1, int v2);
    long select_gt_jlong(long v1, long v2);
    float select_gt_jfloat(float v1, float v2);
    double select_gt_jdouble(double v1, double v2);
    byte select_gt_int8_t(byte v1, byte v2);
    byte select_gt_uint8_t(byte v1, byte v2);
    short select_gt_int16_t(short v1, short v2);
    short select_gt_uint16_t(short v1, short v2);
    int select_gt_int32_t(int v1, int v2);
    int select_gt_uint32_t(int v1, int v2);
    long select_gt_int64_t(long v1, long v2);
    long select_gt_uint64_t(long v1, long v2);
    float select_gt_float(float v1, float v2);
    double select_gt_double(double v1, double v2);

    byte select_ge_jbyte(byte v1, byte v2);
    short select_ge_jshort(short v1, short v2);
    int select_ge_jint(int v1, int v2);
    long select_ge_jlong(long v1, long v2);
    float select_ge_jfloat(float v1, float v2);
    double select_ge_jdouble(double v1, double v2);
    byte select_ge_int8_t(byte v1, byte v2);
    byte select_ge_uint8_t(byte v1, byte v2);
    short select_ge_int16_t(short v1, short v2);
    short select_ge_uint16_t(short v1, short v2);
    int select_ge_int32_t(int v1, int v2);
    int select_ge_uint32_t(int v1, int v2);
    long select_ge_int64_t(long v1, long v2);
    long select_ge_uint64_t(long v1, long v2);
    float select_ge_float(float v1, float v2);
    double select_ge_double(double v1, double v2);

    byte select_le_jbyte(byte v1, byte v2);
    short select_le_jshort(short v1, short v2);
    int select_le_jint(int v1, int v2);
    long select_le_jlong(long v1, long v2);
    float select_le_jfloat(float v1, float v2);
    double select_le_jdouble(double v1, double v2);
    byte select_le_int8_t(byte v1, byte v2);
    byte select_le_uint8_t(byte v1, byte v2);
    short select_le_int16_t(short v1, short v2);
    short select_le_uint16_t(short v1, short v2);
    int select_le_int32_t(int v1, int v2);
    int select_le_uint32_t(int v1, int v2);
    long select_le_int64_t(long v1, long v2);
    long select_le_uint64_t(long v1, long v2);
    float select_le_float(float v1, float v2);
    double select_le_double(double v1, double v2);

    byte select_lt_jbyte(byte v1, byte v2);
    short select_lt_jshort(short v1, short v2);
    int select_lt_jint(int v1, int v2);
    long select_lt_jlong(long v1, long v2);
    float select_lt_jfloat(float v1, float v2);
    double select_lt_jdouble(double v1, double v2);
    byte select_lt_int8_t(byte v1, byte v2);
    byte select_lt_uint8_t(byte v1, byte v2);
    short select_lt_int16_t(short v1, short v2);
    short select_lt_uint16_t(short v1, short v2);
    int select_lt_int32_t(int v1, int v2);
    int select_lt_uint32_t(int v1, int v2);
    long select_lt_int64_t(long v1, long v2);
    long select_lt_uint64_t(long v1, long v2);
    float select_lt_float(float v1, float v2);
    double select_lt_double(double v1, double v2);

    byte select_ne_jbyte(byte v1, byte v2);
    short select_ne_jshort(short v1, short v2);
    int select_ne_jint(int v1, int v2);
    long select_ne_jlong(long v1, long v2);
    float select_ne_jfloat(float v1, float v2);
    double select_ne_jdouble(double v1, double v2);
    byte select_ne_int8_t(byte v1, byte v2);
    byte select_ne_uint8_t(byte v1, byte v2);
    short select_ne_int16_t(short v1, short v2);
    short select_ne_uint16_t(short v1, short v2);
    int select_ne_int32_t(int v1, int v2);
    int select_ne_uint32_t(int v1, int v2);
    long select_ne_int64_t(long v1, long v2);
    long select_ne_uint64_t(long v1, long v2);
    float select_ne_float(float v1, float v2);
    double select_ne_double(double v1, double v2);

    // bitcast
    byte from_jbyte_to_int8_t(byte v);
    short from_jbyte_to_int16_t(byte v);
    int from_jbyte_to_int32_t(byte v);
    long from_jbyte_to_int64_t(byte v);
    byte from_jbyte_to_uint8_t(byte v);
    short from_jbyte_to_uint16_t(byte v);
    int from_jbyte_to_uint32_t(byte v);
    long from_jbyte_to_uint64_t(byte v);
    float from_jbyte_to_float(byte v);
    double from_jbyte_to_double(byte v);
    byte from_jbyte_to_jbyte(byte v);
    boolean from_jbyte_to_bool(byte v);
    short from_jbyte_to_jshort(byte v);
    char from_jbyte_to_jchar(byte v);
    int from_jbyte_to_jint(byte v);
    long from_jbyte_to_jlong(byte v);
    float from_jbyte_to_jfloat(byte v);
    double from_jbyte_to_jdouble(byte v);

/*    byte from_jboolean_to_int8_t(boolean v);
    short from_jboolean_to_int16_t(boolean v);
    int from_jboolean_to_int32_t(boolean v);
    long from_jboolean_to_int64_t(boolean v);
    byte from_jboolean_to_uint8_t(boolean v);
    short from_jboolean_to_uint16_t(boolean v);
    int from_jboolean_to_uint32_t(boolean v);
    long from_jboolean_to_uint64_t(boolean v);
    float from_jboolean_to_float(boolean v);
    double from_jboolean_to_double(boolean v);
    byte from_jboolean_to_jbyte(boolean v);
    boolean from_jboolean_to_bool(boolean v);
    short from_jboolean_to_jshort(boolean v);
    char from_jboolean_to_jchar(boolean v);
    int from_jboolean_to_jint(boolean v);
    long from_jboolean_to_jlong(boolean v);
    float from_jboolean_to_jfloat(boolean v);
    double from_jboolean_to_jdouble(boolean v);*/

    byte from_jshort_to_int8_t(short v);
    short from_jshort_to_int16_t(short v);
    int from_jshort_to_int32_t(short v);
    long from_jshort_to_int64_t(short v);
    byte from_jshort_to_uint8_t(short v);
    short from_jshort_to_uint16_t(short v);
    int from_jshort_to_uint32_t(short v);
    long from_jshort_to_uint64_t(short v);
    float from_jshort_to_float(short v);
    double from_jshort_to_double(short v);
    byte from_jshort_to_jbyte(short v);
    boolean from_jshort_to_bool(short v);
    short from_jshort_to_jshort(short v);
    char from_jshort_to_jchar(short v);
    int from_jshort_to_jint(short v);
    long from_jshort_to_jlong(short v);
    float from_jshort_to_jfloat(short v);
    double from_jshort_to_jdouble(short v);

    byte from_jchar_to_int8_t(char v);
    short from_jchar_to_int16_t(char v);
    int from_jchar_to_int32_t(char v);
    long from_jchar_to_int64_t(char v);
    byte from_jchar_to_uint8_t(char v);
    short from_jchar_to_uint16_t(char v);
    int from_jchar_to_uint32_t(char v);
    long from_jchar_to_uint64_t(char v);
    float from_jchar_to_float(char v);
    double from_jchar_to_double(char v);
    byte from_jchar_to_jbyte(char v);
    boolean from_jchar_to_bool(char v);
    short from_jchar_to_jshort(char v);
    char from_jchar_to_jchar(char v);
    int from_jchar_to_jint(char v);
    long from_jchar_to_jlong(char v);
    float from_jchar_to_jfloat(char v);
    double from_jchar_to_jdouble(char v);

    byte from_jint_to_int8_t(int v);
    short from_jint_to_int16_t(int v);
    int from_jint_to_int32_t(int v);
    long from_jint_to_int64_t(int v);
    byte from_jint_to_uint8_t(int v);
    short from_jint_to_uint16_t(int v);
    int from_jint_to_uint32_t(int v);
    long from_jint_to_uint64_t(int v);
    float from_jint_to_float(int v);
    double from_jint_to_double(int v);
    byte from_jint_to_jbyte(int v);
    boolean from_jint_to_bool(int v);
    short from_jint_to_jshort(int v);
    char from_jint_to_jchar(int v);
    int from_jint_to_jint(int v);
    long from_jint_to_jlong(int v);
    float from_jint_to_jfloat(int v);
    double from_jint_to_jdouble(int v);

    byte from_jlong_to_int8_t(long v);
    short from_jlong_to_int16_t(long v);
    int from_jlong_to_int32_t(long v);
    long from_jlong_to_int64_t(long v);
    byte from_jlong_to_uint8_t(long v);
    short from_jlong_to_uint16_t(long v);
    int from_jlong_to_uint32_t(long v);
    long from_jlong_to_uint64_t(long v);
    float from_jlong_to_float(long v);
    double from_jlong_to_double(long v);
    byte from_jlong_to_jbyte(long v);
    boolean from_jlong_to_bool(long v);
    short from_jlong_to_jshort(long v);
    char from_jlong_to_jchar(long v);
    int from_jlong_to_jint(long v);
    long from_jlong_to_jlong(long v);
    float from_jlong_to_jfloat(long v);
    double from_jlong_to_jdouble(long v);

    byte from_jfloat_to_int8_t(float v);
    short from_jfloat_to_int16_t(float v);
    int from_jfloat_to_int32_t(float v);
    long from_jfloat_to_int64_t(float v);
    byte from_jfloat_to_uint8_t(float v);
    short from_jfloat_to_uint16_t(float v);
    int from_jfloat_to_uint32_t(float v);
    long from_jfloat_to_uint64_t(float v);
    float from_jfloat_to_float(float v);
    double from_jfloat_to_double(float v);
    byte from_jfloat_to_jbyte(float v);
    boolean from_jfloat_to_bool(float v);
    short from_jfloat_to_jshort(float v);
    char from_jfloat_to_jchar(float v);
    int from_jfloat_to_jint(float v);
    long from_jfloat_to_jlong(float v);
    float from_jfloat_to_jfloat(float v);
    double from_jfloat_to_jdouble(float v);

    byte from_jdouble_to_int8_t(double v);
    short from_jdouble_to_int16_t(double v);
    int from_jdouble_to_int32_t(double v);
    long from_jdouble_to_int64_t(double v);
    byte from_jdouble_to_uint8_t(double v);
    short from_jdouble_to_uint16_t(double v);
    int from_jdouble_to_uint32_t(double v);
    long from_jdouble_to_uint64_t(double v);
    float from_jdouble_to_float(double v);
    double from_jdouble_to_double(double v);
    byte from_jdouble_to_jbyte(double v);
    boolean from_jdouble_to_bool(double v);
    short from_jdouble_to_jshort(double v);
    char from_jdouble_to_jchar(double v);
    int from_jdouble_to_jint(double v);
    long from_jdouble_to_jlong(double v);
    float from_jdouble_to_jfloat(double v);
    double from_jdouble_to_jdouble(double v);

    byte from_uint8_t_to_int8_t(byte v);
    short from_uint8_t_to_int16_t(byte v);
    int from_uint8_t_to_int32_t(byte v);
    long from_uint8_t_to_int64_t(byte v);
    byte from_uint8_t_to_uint8_t(byte v);
    short from_uint8_t_to_uint16_t(byte v);
    int from_uint8_t_to_uint32_t(byte v);
    long from_uint8_t_to_uint64_t(byte v);
    float from_uint8_t_to_float(byte v);
    double from_uint8_t_to_double(byte v);
    byte from_uint8_t_to_jbyte(byte v);
    boolean from_uint8_t_to_bool(byte v);
    short from_uint8_t_to_jshort(byte v);
    char from_uint8_t_to_jchar(byte v);
    int from_uint8_t_to_jint(byte v);
    long from_uint8_t_to_jlong(byte v);
    float from_uint8_t_to_jfloat(byte v);
    double from_uint8_t_to_jdouble(byte v);

    byte from_uint16_t_to_int8_t(short v);
    short from_uint16_t_to_int16_t(short v);
    int from_uint16_t_to_int32_t(short v);
    long from_uint16_t_to_int64_t(short v);
    byte from_uint16_t_to_uint8_t(short v);
    short from_uint16_t_to_uint16_t(short v);
    int from_uint16_t_to_uint32_t(short v);
    long from_uint16_t_to_uint64_t(short v);
    float from_uint16_t_to_float(short v);
    double from_uint16_t_to_double(short v);
    byte from_uint16_t_to_jbyte(short v);
    boolean from_uint16_t_to_bool(short v);
    short from_uint16_t_to_jshort(short v);
    char from_uint16_t_to_jchar(short v);
    int from_uint16_t_to_jint(short v);
    long from_uint16_t_to_jlong(short v);
    float from_uint16_t_to_jfloat(short v);
    double from_uint16_t_to_jdouble(short v);

    byte from_uint32_t_to_int8_t(int v);
    short from_uint32_t_to_int16_t(int v);
    int from_uint32_t_to_int32_t(int v);
    long from_uint32_t_to_int64_t(int v);
    byte from_uint32_t_to_uint8_t(int v);
    short from_uint32_t_to_uint16_t(int v);
    int from_uint32_t_to_uint32_t(int v);
    long from_uint32_t_to_uint64_t(int v);
    float from_uint32_t_to_float(int v);
    double from_uint32_t_to_double(int v);
    byte from_uint32_t_to_jbyte(int v);
    boolean from_uint32_t_to_bool(int v);
    short from_uint32_t_to_jshort(int v);
    char from_uint32_t_to_jchar(int v);
    int from_uint32_t_to_jint(int v);
    long from_uint32_t_to_jlong(int v);
    float from_uint32_t_to_jfloat(int v);
    double from_uint32_t_to_jdouble(int v);

    byte from_uint64_t_to_int8_t(long v);
    short from_uint64_t_to_int16_t(long v);
    int from_uint64_t_to_int32_t(long v);
    long from_uint64_t_to_int64_t(long v);
    byte from_uint64_t_to_uint8_t(long v);
    short from_uint64_t_to_uint16_t(long v);
    int from_uint64_t_to_uint32_t(long v);
    long from_uint64_t_to_uint64_t(long v);
    float from_uint64_t_to_float(long v);
    double from_uint64_t_to_double(long v);
    byte from_uint64_t_to_jbyte(long v);
    boolean from_uint64_t_to_bool(long v);
    short from_uint64_t_to_jshort(long v);
    char from_uint64_t_to_jchar(long v);
    int from_uint64_t_to_jint(long v);
    long from_uint64_t_to_jlong(long v);
    float from_uint64_t_to_jfloat(long v);
    double from_uint64_t_to_jdouble(long v);

    byte bitcast_jbyte_to_int8_t(byte v);
    byte bitcast_int8_t_to_jbyte(byte v);

    short bitcast_jshort_to_int16_t(short v);
    short bitcast_int16_t_to_jshort(short v);

    char bitcast_jchar_to_uint16_t(char v);
    char bitcast_uint16_t_to_jchar(char v);

    int bitcast_jint_to_int32_t(int v);
    int bitcast_int32_t_to_jint(int v);

    float bitcast_jint_to_float(int v);
    int bitcast_float_to_jint(float v);

    long bitcast_jlong_to_int64_t(long v);
    long bitcast_int64_t_to_jlong(long v);

    double bitcast_jlong_to_double(long v);
    long bitcast_double_to_jlong(double v);

    boolean and_bool(boolean v1, boolean v2);
    boolean or_bool(boolean v1, boolean v2);
    boolean xor_bool(boolean v1, boolean v2);
    boolean eq_bool(boolean v1, boolean v2);
    boolean gt_bool(boolean v1, boolean v2);
    boolean ge_bool(boolean v1, boolean v2);
    boolean le_bool(boolean v1, boolean v2);
    boolean lt_bool(boolean v1, boolean v2);
    boolean ne_bool(boolean v1, boolean v2);
    boolean select_eq_bool(boolean v1, boolean v2);
    boolean select_gt_bool(boolean v1, boolean v2);
    boolean select_ge_bool(boolean v1, boolean v2);
    boolean select_le_bool(boolean v1, boolean v2);
    boolean select_lt_bool(boolean v1, boolean v2);
    boolean select_ne_bool(boolean v1, boolean v2);

    byte from_bool_to_int8_t(boolean v);
    short from_bool_to_int16_t(boolean v);
    int from_bool_to_int32_t(boolean v);
    long from_bool_to_int64_t(boolean v);
    byte from_bool_to_uint8_t(boolean v);
    short from_bool_to_uint16_t(boolean v);
    int from_bool_to_uint32_t(boolean v);
    long from_bool_to_uint64_t(boolean v);
    float from_bool_to_float(boolean v);
    double from_bool_to_double(boolean v);
    byte from_bool_to_jbyte(boolean v);
    boolean from_bool_to_bool(boolean v);
    short from_bool_to_jshort(boolean v);
    char from_bool_to_jchar(boolean v);
    int from_bool_to_jint(boolean v);
    long from_bool_to_jlong(boolean v);
    float from_bool_to_jfloat(boolean v);
    double from_bool_to_jdouble(boolean v);
}
