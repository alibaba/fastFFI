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

import com.alibaba.fastffi.llvm.Value;
import com.alibaba.fastffi.llvm4jni.type.FieldTypeDef;
import com.alibaba.fastffi.llvm4jni.type.MethodTypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDef;

/**
 * Mapping a LLVM Value to a JavaTag.
 * Type Annotation.
 * See @LLVMToBytecode.javaKind
 *
 */
public class JavaTag {

    public static JavaTag jbyteArray(Value value) {
        return new JavaTag(value, TagType.jbyteArray);
    }

    public static JavaTag jbooleanArray(Value value) {
        return new JavaTag(value, TagType.jbooleanArray);
    }

    public static JavaTag jshortArray(Value value) {
        return new JavaTag(value, TagType.jshortArray);
    }

    public static JavaTag jcharArray(Value value) {
        return new JavaTag(value, TagType.jcharArray);
    }

    public static JavaTag jintArray(Value value) {
        return new JavaTag(value, TagType.jintArray);
    }

    public static JavaTag jfloatArray(Value value) {
        return new JavaTag(value, TagType.jfloatArray);
    }

    public static JavaTag jlongArray(Value value) {
        return new JavaTag(value, TagType.jlongArray);
    }

    public static JavaTag jdoubleArray(Value value) {
        return new JavaTag(value, TagType.jdoubleArray);
    }

    public static JavaTag jfieldID(Value value, FieldTypeDef fieldTypeDef) {
        return new JavaTag(value, TagType.jfieldID, fieldTypeDef);
    }

    public static JavaTag jmethodID(Value value, MethodTypeDef methodTypeDef) {
        return new JavaTag(value, TagType.jmethodID, methodTypeDef);
    }

    public static JavaTag objectArray(Value value, TypeDef elementTypeDef) {
        return new JavaTag(value, TagType.jobjectArray, elementTypeDef);
    }

    /**
     *
     * @param value
     * @param typeDef must be java.lang.Class
     * @return
     */
    public static JavaTag jclass(Value value, TypeDef typeDef) {
        return new JavaTag(value, TagType.jclass, typeDef);
    }

    public static JavaTag tag(Value value, TagType tagType, Object data) {
        return new JavaTag(value, tagType, data);
    }

    public static JavaTag jobject(Value value, TypeDef typeDef) {
        return new JavaTag(value, TagType.jobject, typeDef);
    }

    public static JavaTag jstring(Value value, TypeDef typeDef) {
        return new JavaTag(value, TagType.jstring, typeDef);
    }

    public static JavaTag jniEnv(Value value) {
        return new JavaTag(value, TagType.JNIEnv, null);
    }

    public static JavaTag jniNativeInterface(Value value) {
        return new JavaTag(value, TagType.JNINativeInterface, null);
    }

    public static JavaTag jniEnvFun(Value value, JNIEnvFun jniEnvFun) {
        return new JavaTag(value, TagType.JNIEnvFun, jniEnvFun);
    }

    /**
     * class._jarray
     * class._jbooleanArray
     * class._jbyteArray
     * class._jcharArray
     * class._jclass
     * class._jdoubleArray
     * class._jfloatArray
     * class._jintArray
     * class._jlongArray
     * class._jobject
     * class._jobjectArray
     * class._jshortArray
     * class._jstring
     * class._jthrowable
     */
    public enum TagType {
        jobject, // a generic object which we do not know its type;
        jstring, // must be a string
        jclass, // must be a class: the data is either null or a String of the object type name.
        jthrowable, // must be a Java throwable

        jobjectArray,
        jbooleanArray,
        jbyteArray,
        jcharArray,
        jshortArray,
        jintArray,
        jfloatArray,
        jlongArray,
        jdoubleArray,

        jmethodID,
        jfieldID,

        /**
        jboolean,
        jbyte,
        jchar,
        jshort,
        jint,
        jfloat,
        jlong,
        jdouble,
        **/

        /**
         * JNIEnv is a struct type whose only element is a pointer to a JNINativeInterface struct.
         * JNINativeInterface is a struct whose elements are JNI function pointers.
         */
        JNIEnv,
        JNINativeInterface,
        JNIEnvFun,

        // we do not know that it is
        illegal;

        public boolean isArray() {
            return ordinal() >= jobjectArray.ordinal() && ordinal() <= jdoubleArray.ordinal();
        }

        public boolean isAccessible() {
            switch (this) {
                case jfieldID:
                case jmethodID:
                case JNIEnv:
                case JNIEnvFun:
                case JNINativeInterface:
                case illegal:
                    return false;
            }
            return true;
        }
    }

    private Value value;
    private TagType type;
    private Object data;

    public JavaTag(Value value, TagType type) {
        this(value, type, null);
    }

    public JavaTag(Value value, TagType type, Object data) {
        this.value = value;
        this.type = type;
        this.data = data;
    }

    public boolean isArray() {
        return type.isArray();
    }

    /**
     * Can be accessed by Java bytecode
     * @return
     */
    public boolean isAccessible() {
        return type.isAccessible();
    }

    public TagType getType() {
        return type;
    }

    /**
     * <ul>
     * <li>jobject: data -> TypeDef or null;
     * <li>jclass: data -> TypeDef or null;
     * <li>jmethodId: data -> MethodTypeDef or null;
     * <li>jfieldId: data -> FieldTypeDef or null;
     * <li>jobjectArray: data -> TypeDef of the component type or null;
     * </ul>
     */
    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "JavaTag{" +
                "value=" + value +
                ", type=" + type +
                ", data=" + data +
                '}';
    }
}
