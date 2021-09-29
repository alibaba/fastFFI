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
#ifndef LLVM4JNI_TEST_HPP
#define LLVM4JNI_TEST_HPP

#include <jni.h>
#include <cstdint>
#include <string.h>

namespace llvm4jni {

inline bool and_bool(bool v1, bool v2) { return v1 & v2; }
inline bool or_bool(bool v1, bool v2) { return v1 | v2; }
inline bool xor_bool(bool v1, bool v2) { return v1 ^ v2; }
inline bool eq_bool(bool v1, bool v2) { return v1 == v2; }
inline bool gt_bool(bool v1, bool v2) { return v1 > v2; }
inline bool ge_bool(bool v1, bool v2) { return v1 >= v2; }
inline bool le_bool(bool v1, bool v2) { return v1 <= v2; }
inline bool lt_bool(bool v1, bool v2) { return v1 < v2; }
inline bool ne_bool(bool v1, bool v2) { return v1 != v2; }
inline bool select_eq_bool(bool v1, bool v2) { return v1 == v2 ? v1 : v2; }
inline bool select_gt_bool(bool v1, bool v2) { return v1 > v2 ? v1 : v2; }
inline bool select_ge_bool(bool v1, bool v2) { return v1 >= v2 ? v1 : v2; }
inline bool select_le_bool(bool v1, bool v2) { return v1 <= v2 ? v1 : v2; }
inline bool select_lt_bool(bool v1, bool v2) { return v1 < v2 ? v1 : v2; }
inline bool select_ne_bool(bool v1, bool v2) { return v1 != v2 ? v1 : v2; }

#define INTEGER_OPERATORS(TYPE) \
     inline TYPE neg_##TYPE(TYPE v1) { return -v1; }  \
     inline TYPE add_##TYPE(TYPE v1, TYPE v2) { return v1 + v2; }  \
     inline TYPE sub_##TYPE(TYPE v1, TYPE v2) { return v1 - v2; }  \
     inline TYPE mul_##TYPE(TYPE v1, TYPE v2) { return v1 * v2; }  \
     inline TYPE div_##TYPE(TYPE v1, TYPE v2) { return v1 / v2; }  \
     inline TYPE rem_##TYPE(TYPE v1, TYPE v2) { return v1 % v2; }  \
     inline TYPE and_##TYPE(TYPE v1, TYPE v2) { return v1 & v2; }  \
     inline TYPE or_##TYPE(TYPE v1, TYPE v2) { return v1 | v2; }  \
     inline TYPE xor_##TYPE(TYPE v1, TYPE v2) { return v1 ^ v2; }  \
     inline TYPE shl_##TYPE(TYPE v1, TYPE v2) { return v1 << v2; }  \
     inline TYPE shr_##TYPE(TYPE v1, TYPE v2) { return v1 >> v2; }  \
     inline bool eq_##TYPE(TYPE v1, TYPE v2) { return v1 == v2; }  \
     inline bool gt_##TYPE(TYPE v1, TYPE v2) { return v1 > v2; }  \
     inline bool ge_##TYPE(TYPE v1, TYPE v2) { return v1 >= v2; }  \
     inline bool le_##TYPE(TYPE v1, TYPE v2) { return v1 <= v2; }  \
     inline bool lt_##TYPE(TYPE v1, TYPE v2) { return v1 < v2; }  \
     inline bool ne_##TYPE(TYPE v1, TYPE v2) { return v1 != v2; }  \
     inline TYPE select_eq_##TYPE(TYPE v1, TYPE v2) { return v1 == v2 ? v1 : v2; }  \
     inline TYPE select_gt_##TYPE(TYPE v1, TYPE v2) { return v1 > v2 ? v1 : v2; }  \
     inline TYPE select_ge_##TYPE(TYPE v1, TYPE v2) { return v1 >= v2 ? v1 : v2; }  \
     inline TYPE select_le_##TYPE(TYPE v1, TYPE v2) { return v1 <= v2 ? v1 : v2; }  \
     inline TYPE select_lt_##TYPE(TYPE v1, TYPE v2) { return v1 < v2 ? v1 : v2; }  \
     inline TYPE select_ne_##TYPE(TYPE v1, TYPE v2) { return v1 != v2 ? v1 : v2; }  \
     \

#define FLOATING_OPERATORS(TYPE) \
     inline TYPE neg_##TYPE(TYPE v1) { return -v1; } \
     inline TYPE add_##TYPE(TYPE v1, TYPE v2) { return v1 + v2; } \
     inline TYPE sub_##TYPE(TYPE v1, TYPE v2) { return v1 - v2; } \
     inline TYPE mul_##TYPE(TYPE v1, TYPE v2) { return v1 * v2; } \
     inline TYPE div_##TYPE(TYPE v1, TYPE v2) { return v1 / v2; } \
     inline bool eq_##TYPE(TYPE v1, TYPE v2) { return v1 == v2; }  \
     inline bool gt_##TYPE(TYPE v1, TYPE v2) { return v1 > v2; }  \
     inline bool ge_##TYPE(TYPE v1, TYPE v2) { return v1 >= v2; }  \
     inline bool le_##TYPE(TYPE v1, TYPE v2) { return v1 <= v2; }  \
     inline bool lt_##TYPE(TYPE v1, TYPE v2) { return v1 < v2; }  \
     inline bool ne_##TYPE(TYPE v1, TYPE v2) { return v1 != v2; }  \
     inline TYPE select_eq_##TYPE(TYPE v1, TYPE v2) { return v1 == v2 ? v1 : v2; }  \
     inline TYPE select_gt_##TYPE(TYPE v1, TYPE v2) { return v1 > v2 ? v1 : v2; }  \
     inline TYPE select_ge_##TYPE(TYPE v1, TYPE v2) { return v1 >= v2 ? v1 : v2; }  \
     inline TYPE select_le_##TYPE(TYPE v1, TYPE v2) { return v1 <= v2 ? v1 : v2; }  \
     inline TYPE select_lt_##TYPE(TYPE v1, TYPE v2) { return v1 < v2 ? v1 : v2; }  \
     inline TYPE select_ne_##TYPE(TYPE v1, TYPE v2) { return v1 != v2 ? v1 : v2; }  \
     \


INTEGER_OPERATORS(jbyte)
INTEGER_OPERATORS(jshort)
INTEGER_OPERATORS(jint)
INTEGER_OPERATORS(jlong)
FLOATING_OPERATORS(jfloat)
FLOATING_OPERATORS(jdouble)

INTEGER_OPERATORS(int8_t)
INTEGER_OPERATORS(uint8_t)
INTEGER_OPERATORS(int16_t)
INTEGER_OPERATORS(uint16_t)
INTEGER_OPERATORS(int32_t)
INTEGER_OPERATORS(uint32_t)
INTEGER_OPERATORS(int64_t)
INTEGER_OPERATORS(uint64_t)

FLOATING_OPERATORS(float)
FLOATING_OPERATORS(double)

#define FOREACH_BASIC_TYPE(FUNCTOR) \
        FUNCTOR(bool)              \
        FUNCTOR(int8_t)            \
        FUNCTOR(int16_t)           \
        FUNCTOR(int32_t)           \
        FUNCTOR(int64_t)           \
        FUNCTOR(uint8_t)           \
        FUNCTOR(uint16_t)          \
        FUNCTOR(uint32_t)          \
        FUNCTOR(uint64_t)          \
        FUNCTOR(float)             \
        FUNCTOR(double)            \
        FUNCTOR(jbyte)             \
        FUNCTOR(jboolean)          \
        FUNCTOR(jchar)             \
        FUNCTOR(jshort)            \
        FUNCTOR(jint)              \
        FUNCTOR(jlong)             \
        FUNCTOR(jfloat)            \
        FUNCTOR(jdouble)           \
        \

#define ARRAY_SIZE 16
#define STRUCT_FIELD(TYPE) \
    TYPE value_##TYPE; \
    TYPE array_##TYPE[ARRAY_SIZE]; \
    \


#define READ_STRUCT_FIELD(STRUCT, TYPE)                              \
    inline TYPE get_##STRUCT##_##TYPE(STRUCT *s) {                          \
        return s->value_##TYPE;                                      \
    }                                                                \
    inline TYPE get_array_element_##STRUCT##_##TYPE(STRUCT *s, int index) { \
        return s->array_##TYPE[index];                               \
    }                                                                \
    inline void set_##STRUCT##_##TYPE(STRUCT *s, TYPE v) {                  \
        s->value_##TYPE = v;                                         \
    }                                                                \
    inline void set_array_element_##STRUCT##_##TYPE(STRUCT *s, int index, TYPE v) { \
        s->array_##TYPE[index] = v;                                  \
    }                                                                \
    \

#define READ_STRUCT1_FIELD(TYPE) READ_STRUCT_FIELD(Struct1, TYPE)
#define READ_STRUCT2_FIELD(TYPE) READ_STRUCT_FIELD(Struct2, TYPE)

#define READ_STRUCT_STRUCT_FIELD(STRUCT1, STRUCT2, TYPE)                             \
    inline TYPE get_##STRUCT1##_##STRUCT2##_##TYPE(STRUCT2 *s) {                            \
        return s->value_##STRUCT1.value_##TYPE;                                      \
    }                                                                                \
    inline TYPE get_##STRUCT1##_array_element_##STRUCT2##_##TYPE(STRUCT2 *s, int index) {   \
        return s->value_##STRUCT1.array_##TYPE[index];                               \
    }                                                                                \
    inline TYPE get_array_element_##STRUCT1##_##STRUCT2##_##TYPE(STRUCT2 *s, int index) {   \
        return s->array_##STRUCT1[index].value_##TYPE;                               \
    }                                                                                \
    inline TYPE get_array_element_##STRUCT1##_array_element_##STRUCT2##_##TYPE(STRUCT2 *s, int idx1, int idx2) {   \
        return s->array_##STRUCT1[idx1].array_##TYPE[idx2];                          \
    }                                                                                \
    inline void set_##STRUCT1##_##STRUCT2##_##TYPE(STRUCT2 *s, TYPE v) {                    \
        s->value_##STRUCT1.value_##TYPE = v;                                         \
    }                                                                                \
    inline void set_##STRUCT1##_array_element_##STRUCT2##_##TYPE(STRUCT2 *s, int index, TYPE v) {   \
        s->value_##STRUCT1.array_##TYPE[index] = v;                                  \
    }                                                                                \
    inline void set_array_element_##STRUCT1##_##STRUCT2##_##TYPE(STRUCT2 *s, int index, TYPE v) {   \
        s->array_##STRUCT1[index].value_##TYPE = v;                                  \
    }                                                                                \
    inline void set_array_element_##STRUCT1##_array_element_##STRUCT2##_##TYPE(STRUCT2 *s, int idx1, int idx2, TYPE v) {   \
        s->array_##STRUCT1[idx1].array_##TYPE[idx2] = v;                             \
    }                                                                                \
    \

#define READ_STRUCT1_STRUCT2_FIELD(TYPE) READ_STRUCT_STRUCT_FIELD(Struct1, Struct2, TYPE)

struct Struct1 {
    FOREACH_BASIC_TYPE(STRUCT_FIELD)

};

struct Struct2 {
    FOREACH_BASIC_TYPE(STRUCT_FIELD)

    // a nested struct
    STRUCT_FIELD(Struct1);

};

FOREACH_BASIC_TYPE(READ_STRUCT1_FIELD)

FOREACH_BASIC_TYPE(READ_STRUCT2_FIELD)

FOREACH_BASIC_TYPE(READ_STRUCT1_STRUCT2_FIELD)

#define CAST_TWO_NUMBER(TYPE1, TYPE2) \
    inline TYPE2 from_##TYPE1##_to_##TYPE2(TYPE1 v) { return (TYPE2) v; } \

#define CAST_TO_ALL(TYPE1) \
            CAST_TWO_NUMBER(TYPE1, bool)            \
            CAST_TWO_NUMBER(TYPE1, int8_t)            \
            CAST_TWO_NUMBER(TYPE1, int16_t)           \
            CAST_TWO_NUMBER(TYPE1, int32_t)           \
            CAST_TWO_NUMBER(TYPE1, int64_t)           \
            CAST_TWO_NUMBER(TYPE1, uint8_t)           \
            CAST_TWO_NUMBER(TYPE1, uint16_t)          \
            CAST_TWO_NUMBER(TYPE1, uint32_t)          \
            CAST_TWO_NUMBER(TYPE1, uint64_t)          \
            CAST_TWO_NUMBER(TYPE1, float)             \
            CAST_TWO_NUMBER(TYPE1, double)            \
            CAST_TWO_NUMBER(TYPE1, jbyte)             \
            CAST_TWO_NUMBER(TYPE1, jchar)             \
            CAST_TWO_NUMBER(TYPE1, jshort)            \
            CAST_TWO_NUMBER(TYPE1, jint)              \
            CAST_TWO_NUMBER(TYPE1, jlong)             \
            CAST_TWO_NUMBER(TYPE1, jfloat)            \
            CAST_TWO_NUMBER(TYPE1, jdouble)           \

FOREACH_BASIC_TYPE(CAST_TO_ALL)

union BitCastHelper {
    jbyte _jbyte;
    jboolean _jboolean;
    jshort _jshort;
    jchar _jchar;
    jint _jint;
    jlong _jlong;
    jfloat _jfloat;
    jdouble _jdouble;

    int8_t _int8_t;
    uint8_t _uint8_t;
    int16_t _int16_t;
    uint16_t _uint16_t;
    int32_t _int32_t;
    uint32_t _uint32_t;

    int64_t _int64_t;
    uint64_t _uint64_t;

    float _float;
    double _double;
};

#define BITCAST_TWO_NUMBER(TYPE1, TYPE2) \
    inline TYPE2 bitcast_##TYPE1##_to_##TYPE2(TYPE1 v) { BitCastHelper b; b._##TYPE1 = v; return b._##TYPE2; } \
    inline TYPE1 bitcast_##TYPE2##_to_##TYPE1(TYPE2 v) { BitCastHelper b; b._##TYPE2 = v; return b._##TYPE1; } \
    \


BITCAST_TWO_NUMBER(jbyte, int8_t)
BITCAST_TWO_NUMBER(jshort, int16_t)
BITCAST_TWO_NUMBER(jchar, uint16_t)
BITCAST_TWO_NUMBER(jint, float)
BITCAST_TWO_NUMBER(jint, int32_t)
BITCAST_TWO_NUMBER(jlong, double)
BITCAST_TWO_NUMBER(jlong, int64_t)


class VTableTestStruct {
public:
    VTableTestStruct() {}
    virtual ~VTableTestStruct() {}
    virtual int doCalc(int x, int y) {
        return x + y;
    }
};

#ifdef __clang__

#define COUNT_BITS(TYPE) \
    inline int test_ctlz_##TYPE(TYPE v) { return __builtin_clz(v); } \
    inline int test_cttz_##TYPE(TYPE v) { return __builtin_ctz(v); } \
    inline int test_ctpop_##TYPE(TYPE v) { return __builtin_popcount(v); } \

#else

#define COUNT_BITS(TYPE) \
    inline int test_ctlz_##TYPE(TYPE v) { return 0; } \
    inline int test_cttz_##TYPE(TYPE v) { return 0; } \
    inline int test_ctpop_##TYPE(TYPE v) { return 0; } \

#endif

COUNT_BITS(int8_t)
COUNT_BITS(int16_t)
COUNT_BITS(int32_t)
COUNT_BITS(int64_t)

struct SwitchTestStruct {
    int i;

    SwitchTestStruct() : i(0) {}
    ~SwitchTestStruct() {}
};

inline int test_switch_table(int i) {
    switch (i) {
        case 0:
            return 1;
        case 1:
            return 1;
        case 2:
            return 2;
        case 3:
            return 3;
        case 4:
            return 5;
        default:
            return 0;
    }
}

inline int test_switch(SwitchTestStruct* st, int i) {
    switch (i) {
        case 0:
            st->i = 0;
            return i + 1;
        case 1:
            st->i++;
            return i;
        case 2:
            st->i += 2;
            return i;
        case 3:
            st->i += 3;
            return i;
        case 4:
            st->i--;
            return i + 1;
        default:
            st->i += i;
            return 0;
    }
}

struct MemoryOperationTestStruct {
    char array[10];
public:
    MemoryOperationTestStruct() : array { 0 } {}
    ~MemoryOperationTestStruct() {}
    int test_sum() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            ret += array[i];
        }
        return ret;
    }

    void test_memset(char v) {
        memset(array, v, sizeof(array));
    }

    void test_memcpy(MemoryOperationTestStruct & other) {
        memcpy(array, other.array, sizeof(array));
    }

    void test_memmove(MemoryOperationTestStruct & other) {
        memmove(array, other.array, sizeof(array));
    }
};

struct IndirectTestStruct {
    int i;

    IndirectTestStruct() : i(0) {}
    ~IndirectTestStruct() {}
    IndirectTestStruct* getAndAdd(int mode) {
        if (mode == 1) {
            this->i++;
            return this;
        } else {
            // assume this is a slowpath
            return new IndirectTestStruct();
        }
    }
};

inline int test_alloca(MemoryOperationTestStruct * st, int i, int j) {
    MemoryOperationTestStruct s;
    memcpy(&s.array[0], &st->array[5], sizeof(char) * 5);

    return s.array[j] + s.array[i];
}

inline void test_localconst(MemoryOperationTestStruct * st, int i) {
    const char text[] = "alibaba";
    static_assert(sizeof(text) <= 10, "must not exceed 10");
    memcpy(&st->array[0], &text[i], sizeof(text) - i - 1);
}

} // end of namespace

#endif