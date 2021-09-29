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
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;

@FFIGen(library = "llvm4jni-test")
@FFILibrary(value = "llvm4jni::loadstore", namespace = "llvm4jni")
@CXXHead("llvm4jni_test.hpp")
public interface LoadStoreTestFFILibrary {

    LoadStoreTestFFILibrary INSTANCE = FFITypeFactory.getLibrary(LoadStoreTestFFILibrary.class);

    @FFIGen(library = "llvm4jni-test")
    @CXXHead("llvm4jni_test.hpp")
    @FFITypeAlias("llvm4jni::Struct1")
    interface Struct1 extends CXXPointer {
        Factory factory = FFITypeFactory.getFactory(Factory.class, Struct1.class);
        @FFIFactory
        interface Factory {
            Struct1 create();
        }
    }

    @FFIGen(library = "llvm4jni-test")
    @CXXHead("llvm4jni_test.hpp")
    @FFITypeAlias("llvm4jni::Struct2")
    interface Struct2 extends CXXPointer {
        Factory factory = FFITypeFactory.getFactory(Factory.class, Struct2.class);
        @FFIFactory
        interface Factory {
            Struct2 create();
        }
    }

    boolean get_Struct1_bool(Struct1 s);

    boolean get_array_element_Struct1_bool(Struct1 s, int index);

    void set_Struct1_bool(Struct1 s, boolean v);

    void set_array_element_Struct1_bool(Struct1 s, int index, boolean v);

    byte get_Struct1_int8_t(Struct1 s);

    byte get_array_element_Struct1_int8_t(Struct1 s, int index);

    void set_Struct1_int8_t(Struct1 s, byte v);

    void set_array_element_Struct1_int8_t(Struct1 s, int index, byte v);

    short get_Struct1_int16_t(Struct1 s);

    short get_array_element_Struct1_int16_t(Struct1 s, int index);

    void set_Struct1_int16_t(Struct1 s, short v);

    void set_array_element_Struct1_int16_t(Struct1 s, int index, short v);

    int get_Struct1_int32_t(Struct1 s);

    int get_array_element_Struct1_int32_t(Struct1 s, int index);

    void set_Struct1_int32_t(Struct1 s, int v);

    void set_array_element_Struct1_int32_t(Struct1 s, int index, int v);

    long get_Struct1_int64_t(Struct1 s);

    long get_array_element_Struct1_int64_t(Struct1 s, int index);

    void set_Struct1_int64_t(Struct1 s, long v);

    void set_array_element_Struct1_int64_t(Struct1 s, int index, long v);

    byte get_Struct1_uint8_t(Struct1 s);

    byte get_array_element_Struct1_uint8_t(Struct1 s, int index);

    void set_Struct1_uint8_t(Struct1 s, byte v);

    void set_array_element_Struct1_uint8_t(Struct1 s, int index, byte v);

    short get_Struct1_uint16_t(Struct1 s);

    short get_array_element_Struct1_uint16_t(Struct1 s, int index);

    void set_Struct1_uint16_t(Struct1 s, short v);

    void set_array_element_Struct1_uint16_t(Struct1 s, int index, short v);

    int get_Struct1_uint32_t(Struct1 s);

    int get_array_element_Struct1_uint32_t(Struct1 s, int index);

    void set_Struct1_uint32_t(Struct1 s, int v);

    void set_array_element_Struct1_uint32_t(Struct1 s, int index, int v);

    long get_Struct1_uint64_t(Struct1 s);

    long get_array_element_Struct1_uint64_t(Struct1 s, int index);

    void set_Struct1_uint64_t(Struct1 s, long v);

    void set_array_element_Struct1_uint64_t(Struct1 s, int index, long v);

    float get_Struct1_float(Struct1 s);

    float get_array_element_Struct1_float(Struct1 s, int index);

    void set_Struct1_float(Struct1 s, float v);

    void set_array_element_Struct1_float(Struct1 s, int index, float v);

    double get_Struct1_double(Struct1 s);

    double get_array_element_Struct1_double(Struct1 s, int index);

    void set_Struct1_double(Struct1 s, double v);

    void set_array_element_Struct1_double(Struct1 s, int index, double v);

    byte get_Struct1_jbyte(Struct1 s);

    byte get_array_element_Struct1_jbyte(Struct1 s, int index);

    void set_Struct1_jbyte(Struct1 s, byte v);

    void set_array_element_Struct1_jbyte(Struct1 s, int index, byte v);

    boolean get_Struct1_jboolean(Struct1 s);

    boolean get_array_element_Struct1_jboolean(Struct1 s, int index);

    void set_Struct1_jboolean(Struct1 s, boolean v);

    void set_array_element_Struct1_jboolean(Struct1 s, int index, boolean v);

    char get_Struct1_jchar(Struct1 s);

    char get_array_element_Struct1_jchar(Struct1 s, int index);

    void set_Struct1_jchar(Struct1 s, char v);

    void set_array_element_Struct1_jchar(Struct1 s, int index, char v);

    short get_Struct1_jshort(Struct1 s);

    short get_array_element_Struct1_jshort(Struct1 s, int index);

    void set_Struct1_jshort(Struct1 s, short v);

    void set_array_element_Struct1_jshort(Struct1 s, int index, short v);

    int get_Struct1_jint(Struct1 s);

    int get_array_element_Struct1_jint(Struct1 s, int index);

    void set_Struct1_jint(Struct1 s, int v);

    void set_array_element_Struct1_jint(Struct1 s, int index, int v);

    long get_Struct1_jlong(Struct1 s);

    long get_array_element_Struct1_jlong(Struct1 s, int index);

    void set_Struct1_jlong(Struct1 s, long v);

    void set_array_element_Struct1_jlong(Struct1 s, int index, long v);

    float get_Struct1_jfloat(Struct1 s);

    float get_array_element_Struct1_jfloat(Struct1 s, int index);

    void set_Struct1_jfloat(Struct1 s, float v);

    void set_array_element_Struct1_jfloat(Struct1 s, int index, float v);

    double get_Struct1_jdouble(Struct1 s);

    double get_array_element_Struct1_jdouble(Struct1 s, int index);

    void set_Struct1_jdouble(Struct1 s, double v);

    void set_array_element_Struct1_jdouble(Struct1 s, int index, double v);



    boolean get_Struct2_bool(Struct2 s);

    boolean get_array_element_Struct2_bool(Struct2 s, int index);

    void set_Struct2_bool(Struct2 s, boolean v);

    void set_array_element_Struct2_bool(Struct2 s, int index, boolean v);

    byte get_Struct2_int8_t(Struct2 s);

    byte get_array_element_Struct2_int8_t(Struct2 s, int index);

    void set_Struct2_int8_t(Struct2 s, byte v);

    void set_array_element_Struct2_int8_t(Struct2 s, int index, byte v);

    short get_Struct2_int16_t(Struct2 s);

    short get_array_element_Struct2_int16_t(Struct2 s, int index);

    void set_Struct2_int16_t(Struct2 s, short v);

    void set_array_element_Struct2_int16_t(Struct2 s, int index, short v);

    int get_Struct2_int32_t(Struct2 s);

    int get_array_element_Struct2_int32_t(Struct2 s, int index);

    void set_Struct2_int32_t(Struct2 s, int v);

    void set_array_element_Struct2_int32_t(Struct2 s, int index, int v);

    long get_Struct2_int64_t(Struct2 s);

    long get_array_element_Struct2_int64_t(Struct2 s, int index);

    void set_Struct2_int64_t(Struct2 s, long v);

    void set_array_element_Struct2_int64_t(Struct2 s, int index, long v);

    byte get_Struct2_uint8_t(Struct2 s);

    byte get_array_element_Struct2_uint8_t(Struct2 s, int index);

    void set_Struct2_uint8_t(Struct2 s, byte v);

    void set_array_element_Struct2_uint8_t(Struct2 s, int index, byte v);

    short get_Struct2_uint16_t(Struct2 s);

    short get_array_element_Struct2_uint16_t(Struct2 s, int index);

    void set_Struct2_uint16_t(Struct2 s, short v);

    void set_array_element_Struct2_uint16_t(Struct2 s, int index, short v);

    int get_Struct2_uint32_t(Struct2 s);

    int get_array_element_Struct2_uint32_t(Struct2 s, int index);

    void set_Struct2_uint32_t(Struct2 s, int v);

    void set_array_element_Struct2_uint32_t(Struct2 s, int index, int v);

    long get_Struct2_uint64_t(Struct2 s);

    long get_array_element_Struct2_uint64_t(Struct2 s, int index);

    void set_Struct2_uint64_t(Struct2 s, long v);

    void set_array_element_Struct2_uint64_t(Struct2 s, int index, long v);

    float get_Struct2_float(Struct2 s);

    float get_array_element_Struct2_float(Struct2 s, int index);

    void set_Struct2_float(Struct2 s, float v);

    void set_array_element_Struct2_float(Struct2 s, int index, float v);

    double get_Struct2_double(Struct2 s);

    double get_array_element_Struct2_double(Struct2 s, int index);

    void set_Struct2_double(Struct2 s, double v);

    void set_array_element_Struct2_double(Struct2 s, int index, double v);

    byte get_Struct2_jbyte(Struct2 s);

    byte get_array_element_Struct2_jbyte(Struct2 s, int index);

    void set_Struct2_jbyte(Struct2 s, byte v);

    void set_array_element_Struct2_jbyte(Struct2 s, int index, byte v);

    boolean get_Struct2_jboolean(Struct2 s);

    boolean get_array_element_Struct2_jboolean(Struct2 s, int index);

    void set_Struct2_jboolean(Struct2 s, boolean v);

    void set_array_element_Struct2_jboolean(Struct2 s, int index, boolean v);

    char get_Struct2_jchar(Struct2 s);

    char get_array_element_Struct2_jchar(Struct2 s, int index);

    void set_Struct2_jchar(Struct2 s, char v);

    void set_array_element_Struct2_jchar(Struct2 s, int index, char v);

    short get_Struct2_jshort(Struct2 s);

    short get_array_element_Struct2_jshort(Struct2 s, int index);

    void set_Struct2_jshort(Struct2 s, short v);

    void set_array_element_Struct2_jshort(Struct2 s, int index, short v);

    int get_Struct2_jint(Struct2 s);

    int get_array_element_Struct2_jint(Struct2 s, int index);

    void set_Struct2_jint(Struct2 s, int v);

    void set_array_element_Struct2_jint(Struct2 s, int index, int v);

    long get_Struct2_jlong(Struct2 s);

    long get_array_element_Struct2_jlong(Struct2 s, int index);

    void set_Struct2_jlong(Struct2 s, long v);

    void set_array_element_Struct2_jlong(Struct2 s, int index, long v);

    float get_Struct2_jfloat(Struct2 s);

    float get_array_element_Struct2_jfloat(Struct2 s, int index);

    void set_Struct2_jfloat(Struct2 s, float v);

    void set_array_element_Struct2_jfloat(Struct2 s, int index, float v);

    double get_Struct2_jdouble(Struct2 s);

    double get_array_element_Struct2_jdouble(Struct2 s, int index);

    void set_Struct2_jdouble(Struct2 s, double v);

    void set_array_element_Struct2_jdouble(Struct2 s, int index, double v);



    byte get_Struct1_Struct2_int8_t(Struct2 s);

    byte get_Struct1_array_element_Struct2_int8_t(Struct2 s, int index);

    byte get_array_element_Struct1_Struct2_int8_t(Struct2 s, int index);

    byte get_array_element_Struct1_array_element_Struct2_int8_t(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_int8_t(Struct2 s, byte v);

    void set_Struct1_array_element_Struct2_int8_t(Struct2 s, int index, byte v);

    void set_array_element_Struct1_Struct2_int8_t(Struct2 s, int index, byte v);

    void set_array_element_Struct1_array_element_Struct2_int8_t(Struct2 s, int idx1, int idx2, byte v);

    short get_Struct1_Struct2_int16_t(Struct2 s);

    short get_Struct1_array_element_Struct2_int16_t(Struct2 s, int index);

    short get_array_element_Struct1_Struct2_int16_t(Struct2 s, int index);

    short get_array_element_Struct1_array_element_Struct2_int16_t(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_int16_t(Struct2 s, short v);

    void set_Struct1_array_element_Struct2_int16_t(Struct2 s, int index, short v);

    void set_array_element_Struct1_Struct2_int16_t(Struct2 s, int index, short v);

    void set_array_element_Struct1_array_element_Struct2_int16_t(Struct2 s, int idx1, int idx2, short v);

    int get_Struct1_Struct2_int32_t(Struct2 s);

    int get_Struct1_array_element_Struct2_int32_t(Struct2 s, int index);

    int get_array_element_Struct1_Struct2_int32_t(Struct2 s, int index);

    int get_array_element_Struct1_array_element_Struct2_int32_t(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_int32_t(Struct2 s, int v);

    void set_Struct1_array_element_Struct2_int32_t(Struct2 s, int index, int v);

    void set_array_element_Struct1_Struct2_int32_t(Struct2 s, int index, int v);

    void set_array_element_Struct1_array_element_Struct2_int32_t(Struct2 s, int idx1, int idx2, int v);

    long get_Struct1_Struct2_int64_t(Struct2 s);

    long get_Struct1_array_element_Struct2_int64_t(Struct2 s, int index);

    long get_array_element_Struct1_Struct2_int64_t(Struct2 s, int index);

    long get_array_element_Struct1_array_element_Struct2_int64_t(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_int64_t(Struct2 s, long v);

    void set_Struct1_array_element_Struct2_int64_t(Struct2 s, int index, long v);

    void set_array_element_Struct1_Struct2_int64_t(Struct2 s, int index, long v);

    void set_array_element_Struct1_array_element_Struct2_int64_t(Struct2 s, int idx1, int idx2, long v);

    byte get_Struct1_Struct2_uint8_t(Struct2 s);

    byte get_Struct1_array_element_Struct2_uint8_t(Struct2 s, int index);

    byte get_array_element_Struct1_Struct2_uint8_t(Struct2 s, int index);

    byte get_array_element_Struct1_array_element_Struct2_uint8_t(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_uint8_t(Struct2 s, byte v);

    void set_Struct1_array_element_Struct2_uint8_t(Struct2 s, int index, byte v);

    void set_array_element_Struct1_Struct2_uint8_t(Struct2 s, int index, byte v);

    void set_array_element_Struct1_array_element_Struct2_uint8_t(Struct2 s, int idx1, int idx2, byte v);

    short get_Struct1_Struct2_uint16_t(Struct2 s);

    short get_Struct1_array_element_Struct2_uint16_t(Struct2 s, int index);

    short get_array_element_Struct1_Struct2_uint16_t(Struct2 s, int index);

    short get_array_element_Struct1_array_element_Struct2_uint16_t(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_uint16_t(Struct2 s, short v);

    void set_Struct1_array_element_Struct2_uint16_t(Struct2 s, int index, short v);

    void set_array_element_Struct1_Struct2_uint16_t(Struct2 s, int index, short v);

    void set_array_element_Struct1_array_element_Struct2_uint16_t(Struct2 s, int idx1, int idx2, short v);

    int get_Struct1_Struct2_uint32_t(Struct2 s);

    int get_Struct1_array_element_Struct2_uint32_t(Struct2 s, int index);

    int get_array_element_Struct1_Struct2_uint32_t(Struct2 s, int index);

    int get_array_element_Struct1_array_element_Struct2_uint32_t(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_uint32_t(Struct2 s, int v);

    void set_Struct1_array_element_Struct2_uint32_t(Struct2 s, int index, int v);

    void set_array_element_Struct1_Struct2_uint32_t(Struct2 s, int index, int v);

    void set_array_element_Struct1_array_element_Struct2_uint32_t(Struct2 s, int idx1, int idx2, int v);

    long get_Struct1_Struct2_uint64_t(Struct2 s);

    long get_Struct1_array_element_Struct2_uint64_t(Struct2 s, int index);

    long get_array_element_Struct1_Struct2_uint64_t(Struct2 s, int index);

    long get_array_element_Struct1_array_element_Struct2_uint64_t(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_uint64_t(Struct2 s, long v);

    void set_Struct1_array_element_Struct2_uint64_t(Struct2 s, int index, long v);

    void set_array_element_Struct1_Struct2_uint64_t(Struct2 s, int index, long v);

    void set_array_element_Struct1_array_element_Struct2_uint64_t(Struct2 s, int idx1, int idx2, long v);

    float get_Struct1_Struct2_float(Struct2 s);

    float get_Struct1_array_element_Struct2_float(Struct2 s, int index);

    float get_array_element_Struct1_Struct2_float(Struct2 s, int index);

    float get_array_element_Struct1_array_element_Struct2_float(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_float(Struct2 s, float v);

    void set_Struct1_array_element_Struct2_float(Struct2 s, int index, float v);

    void set_array_element_Struct1_Struct2_float(Struct2 s, int index, float v);

    void set_array_element_Struct1_array_element_Struct2_float(Struct2 s, int idx1, int idx2, float v);

    double get_Struct1_Struct2_double(Struct2 s);

    double get_Struct1_array_element_Struct2_double(Struct2 s, int index);

    double get_array_element_Struct1_Struct2_double(Struct2 s, int index);

    double get_array_element_Struct1_array_element_Struct2_double(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_double(Struct2 s, double v);

    void set_Struct1_array_element_Struct2_double(Struct2 s, int index, double v);

    void set_array_element_Struct1_Struct2_double(Struct2 s, int index, double v);

    void set_array_element_Struct1_array_element_Struct2_double(Struct2 s, int idx1, int idx2, double v);

    byte get_Struct1_Struct2_jbyte(Struct2 s);

    byte get_Struct1_array_element_Struct2_jbyte(Struct2 s, int index);

    byte get_array_element_Struct1_Struct2_jbyte(Struct2 s, int index);

    byte get_array_element_Struct1_array_element_Struct2_jbyte(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_jbyte(Struct2 s, byte v);

    void set_Struct1_array_element_Struct2_jbyte(Struct2 s, int index, byte v);

    void set_array_element_Struct1_Struct2_jbyte(Struct2 s, int index, byte v);

    void set_array_element_Struct1_array_element_Struct2_jbyte(Struct2 s, int idx1, int idx2, byte v);

    boolean get_Struct1_Struct2_jboolean(Struct2 s);

    boolean get_Struct1_array_element_Struct2_jboolean(Struct2 s, int index);

    boolean get_array_element_Struct1_Struct2_jboolean(Struct2 s, int index);

    boolean get_array_element_Struct1_array_element_Struct2_jboolean(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_jboolean(Struct2 s, boolean v);

    void set_Struct1_array_element_Struct2_jboolean(Struct2 s, int index, boolean v);

    void set_array_element_Struct1_Struct2_jboolean(Struct2 s, int index, boolean v);

    void set_array_element_Struct1_array_element_Struct2_jboolean(Struct2 s, int idx1, int idx2, boolean v);

    char get_Struct1_Struct2_jchar(Struct2 s);

    char get_Struct1_array_element_Struct2_jchar(Struct2 s, int index);

    char get_array_element_Struct1_Struct2_jchar(Struct2 s, int index);

    char get_array_element_Struct1_array_element_Struct2_jchar(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_jchar(Struct2 s, char v);

    void set_Struct1_array_element_Struct2_jchar(Struct2 s, int index, char v);

    void set_array_element_Struct1_Struct2_jchar(Struct2 s, int index, char v);

    void set_array_element_Struct1_array_element_Struct2_jchar(Struct2 s, int idx1, int idx2, char v);

    short get_Struct1_Struct2_jshort(Struct2 s);

    short get_Struct1_array_element_Struct2_jshort(Struct2 s, int index);

    short get_array_element_Struct1_Struct2_jshort(Struct2 s, int index);

    short get_array_element_Struct1_array_element_Struct2_jshort(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_jshort(Struct2 s, short v);

    void set_Struct1_array_element_Struct2_jshort(Struct2 s, int index, short v);

    void set_array_element_Struct1_Struct2_jshort(Struct2 s, int index, short v);

    void set_array_element_Struct1_array_element_Struct2_jshort(Struct2 s, int idx1, int idx2, short v);

    int get_Struct1_Struct2_jint(Struct2 s);

    int get_Struct1_array_element_Struct2_jint(Struct2 s, int index);

    int get_array_element_Struct1_Struct2_jint(Struct2 s, int index);

    int get_array_element_Struct1_array_element_Struct2_jint(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_jint(Struct2 s, int v);

    void set_Struct1_array_element_Struct2_jint(Struct2 s, int index, int v);

    void set_array_element_Struct1_Struct2_jint(Struct2 s, int index, int v);

    void set_array_element_Struct1_array_element_Struct2_jint(Struct2 s, int idx1, int idx2, int v);

    long get_Struct1_Struct2_jlong(Struct2 s);

    long get_Struct1_array_element_Struct2_jlong(Struct2 s, int index);

    long get_array_element_Struct1_Struct2_jlong(Struct2 s, int index);

    long get_array_element_Struct1_array_element_Struct2_jlong(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_jlong(Struct2 s, long v);

    void set_Struct1_array_element_Struct2_jlong(Struct2 s, int index, long v);

    void set_array_element_Struct1_Struct2_jlong(Struct2 s, int index, long v);

    void set_array_element_Struct1_array_element_Struct2_jlong(Struct2 s, int idx1, int idx2, long v);

    float get_Struct1_Struct2_jfloat(Struct2 s);

    float get_Struct1_array_element_Struct2_jfloat(Struct2 s, int index);

    float get_array_element_Struct1_Struct2_jfloat(Struct2 s, int index);

    float get_array_element_Struct1_array_element_Struct2_jfloat(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_jfloat(Struct2 s, float v);

    void set_Struct1_array_element_Struct2_jfloat(Struct2 s, int index, float v);

    void set_array_element_Struct1_Struct2_jfloat(Struct2 s, int index, float v);

    void set_array_element_Struct1_array_element_Struct2_jfloat(Struct2 s, int idx1, int idx2, float v);

    double get_Struct1_Struct2_jdouble(Struct2 s);

    double get_Struct1_array_element_Struct2_jdouble(Struct2 s, int index);

    double get_array_element_Struct1_Struct2_jdouble(Struct2 s, int index);

    double get_array_element_Struct1_array_element_Struct2_jdouble(Struct2 s, int idx1, int idx2);

    void set_Struct1_Struct2_jdouble(Struct2 s, double v);

    void set_Struct1_array_element_Struct2_jdouble(Struct2 s, int index, double v);

    void set_array_element_Struct1_Struct2_jdouble(Struct2 s, int index, double v);

    void set_array_element_Struct1_array_element_Struct2_jdouble(Struct2 s, int idx1, int idx2, double v);


}
