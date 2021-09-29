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
#include <jni.h>
#include "smokeTest.hpp"

int Smoke::test(int i, int j) {
  int v1 = i + j;
  int v2 = i - j;
  int v3 = i * j;
  int v4 = i / j;
  return v1 + v2 + v3 + v4;
}

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_test(JNIEnv *, jobject, jint i, jint j) {
  return Smoke::test(i, j);
}

JNIEXPORT jobjectArray JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_removeNullElements(JNIEnv * env, jobject, jobjectArray array) {
  jsize size = env->GetArrayLength(array);
  jint count = 0;

  for (jint i = 0; i < size; i++) {
    jobject e = env->GetObjectArrayElement(array, i);
    if (e != NULL) {
      count++;
    }
  }

  jclass objectClass = env->FindClass("java/lang/Object");
  jobjectArray newArray = env->NewObjectArray(count, objectClass, NULL);

  count = 0;
  for (jint i = 0; i < size; i++) {
    jobject e = env->GetObjectArrayElement(array, i);
    if (e != NULL) {
      env->SetObjectArrayElement(newArray, count++, e);
    }
  }
  return newArray;
}

JNIEXPORT jboolean JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_isJavaUtilList(JNIEnv * env, jobject, jobject arg) {
  jclass objectClass = env->FindClass("java/util/List");
  return env->IsInstanceOf(arg, objectClass);
}

JNIEXPORT jboolean JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_isSameObject(JNIEnv * env, jclass, jobject obj1, jobject obj2) {
  return env->IsSameObject(obj1, obj2);
}

JNIEXPORT jbooleanArray JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_createBooleanArray(JNIEnv * env, jobject, jint size) {
  return env->NewBooleanArray(size);
}
JNIEXPORT jcharArray JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_createCharArray(JNIEnv * env, jobject, jint size) {
  return env->NewCharArray(size);
}
JNIEXPORT jfloatArray JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_createFloatArray(JNIEnv * env, jobject, jint size) {
  return env->NewFloatArray(size);
}
JNIEXPORT jdoubleArray JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_createDoubleArray(JNIEnv * env, jobject, jint size) {
  return env->NewDoubleArray(size);
}
JNIEXPORT jbyteArray JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_createByteArray(JNIEnv * env, jobject, jint size) {
  return env->NewByteArray(size);
}
JNIEXPORT jshortArray JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_createShortArray(JNIEnv * env, jobject, jint size) {
  return env->NewShortArray(size);
}
JNIEXPORT jintArray JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_createIntArray(JNIEnv * env, jobject, jint size) {
  return env->NewIntArray(size);
}
JNIEXPORT jlongArray JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_createLongArray(JNIEnv * env, jobject, jint size) {
  return env->NewLongArray(size);
}

#define DEFINE_FIELD(JNITYPE, JAVATYPE, DESCRIPTOR, FIELD_NAME) \
JNIEXPORT JNITYPE JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_getStatic##JAVATYPE##Field(JNIEnv * env, jclass) { \
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest"); \
  jfieldID field = env->GetStaticFieldID(cls, "static" FIELD_NAME "Field", DESCRIPTOR); \
  return env->GetStatic##JAVATYPE##Field(cls, field); \
} \
JNIEXPORT void JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_setStatic##JAVATYPE##Field(JNIEnv * env, jclass, JNITYPE value) { \
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest"); \
  jfieldID field = env->GetStaticFieldID(cls, "static" FIELD_NAME "Field", DESCRIPTOR); \
  env->SetStatic##JAVATYPE##Field(cls, field, value); \
} \
JNIEXPORT JNITYPE JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_get##JAVATYPE##Field(JNIEnv * env, jobject obj) { \
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest"); \
  jfieldID field = env->GetFieldID(cls, "instance" FIELD_NAME "Field", DESCRIPTOR); \
  return env->Get##JAVATYPE##Field(obj, field); \
} \
JNIEXPORT void JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_set##JAVATYPE##Field(JNIEnv * env, jobject obj, JNITYPE value) { \
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest"); \
  jfieldID field = env->GetFieldID(cls, "instance" FIELD_NAME "Field", DESCRIPTOR); \
  env->Set##JAVATYPE##Field(obj, field, value); \
} \
\
\
JNIEXPORT JNITYPE JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_getStatic##JAVATYPE(JNIEnv * env, jclass) { \
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest"); \
  jmethodID method = env->GetStaticMethodID(cls, "getStatic" FIELD_NAME "Field", "()" DESCRIPTOR); \
  return env->CallStatic##JAVATYPE##Method(cls, method); \
} \
JNIEXPORT void JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_setStatic##JAVATYPE(JNIEnv * env, jclass, JNITYPE value) { \
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest"); \
  jmethodID method = env->GetStaticMethodID(cls, "setStatic" FIELD_NAME "Field", "(" DESCRIPTOR ")V"); \
  env->CallStaticVoidMethod(cls, method, value); \
} \
JNIEXPORT JNITYPE JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_get##JAVATYPE(JNIEnv * env, jobject obj) { \
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest"); \
  jmethodID method = env->GetMethodID(cls, "get" FIELD_NAME "Field", "()" DESCRIPTOR); \
  return env->Call##JAVATYPE##Method(obj, method); \
} \
JNIEXPORT void JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_set##JAVATYPE(JNIEnv * env, jobject obj, JNITYPE value) { \
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest"); \
  jmethodID method = env->GetMethodID(cls, "set" FIELD_NAME "Field", "(" DESCRIPTOR ")V"); \
  env->CallVoidMethod(obj, method, value); \
} \
JNIEXPORT JNITYPE JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_getNonvirtual##JAVATYPE(JNIEnv * env, jobject obj) { \
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest"); \
  jmethodID method = env->GetMethodID(cls, "get" FIELD_NAME "Field", "()" DESCRIPTOR); \
  return env->CallNonvirtual##JAVATYPE##Method(obj, cls, method); \
} \
JNIEXPORT void JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_setNonvirtual##JAVATYPE(JNIEnv * env, jobject obj, JNITYPE value) { \
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest"); \
  jmethodID method = env->GetMethodID(cls, "set" FIELD_NAME "Field", "(" DESCRIPTOR ")V"); \
  env->CallNonvirtualVoidMethod(obj, cls, method, value); \
} \

DEFINE_FIELD(jboolean, Boolean, "Z", "Boolean")
DEFINE_FIELD(jbyte, Byte, "B", "Byte")
DEFINE_FIELD(jchar, Char, "C", "Char")
DEFINE_FIELD(jshort, Short, "S", "Short")
DEFINE_FIELD(jint, Int, "I", "Int")
DEFINE_FIELD(jlong, Long, "J", "Long")
DEFINE_FIELD(jfloat, Float, "F", "Float")
DEFINE_FIELD(jdouble, Double, "D", "Double")
DEFINE_FIELD(jobject, Object, "Ljava/lang/Object;", "Object")


JNIEXPORT jobject JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_newObject(JNIEnv * env, jclass) {
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest$ObjectTest");
  jmethodID ctor = env->GetMethodID(cls, "<init>", "()V");
  return env->NewObject(cls, ctor);
}

JNIEXPORT jobject JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_allocObject(JNIEnv * env, jclass) {
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest$ObjectTest");
  return env->AllocObject(cls);
}

JNIEXPORT jobject JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_newObject2(JNIEnv * env, jclass, jobject obj) {
  jclass cls = env->FindClass("com/alibaba/fastffi/llvm4jni/SmokeTest$ObjectTest");
  jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/Object;)V");
  return env->NewObject(cls, ctor, obj);
}

JNIEXPORT jclass JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_getObjectClass(JNIEnv * env, jclass, jobject obj) {
  return env->GetObjectClass(obj);
}

JNIEXPORT jclass JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_getObjectSuperClass(JNIEnv * env, jclass) {
  jclass cls = env->FindClass("java/lang/Object");
  return env->GetSuperclass(cls);
}

JNIEXPORT jclass JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_getStringSuperClass(JNIEnv * env, jclass) {
  jclass cls = env->FindClass("java/lang/String");
  return env->GetSuperclass(cls);
}

JNIEXPORT jclass JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_getSuperClass(JNIEnv * env, jclass, jclass cls) {
  return env->GetSuperclass(cls);
}

JNIEXPORT jint JNICALL Java_com_alibaba_fastffi_llvm4jni_SmokeTest_getJNIVersion(JNIEnv * env, jclass) {
  return env->GetVersion();
}

#ifdef __cplusplus
}
#endif

