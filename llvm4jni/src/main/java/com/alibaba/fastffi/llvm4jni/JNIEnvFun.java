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

import java.util.HashMap;
import java.util.Map;

public enum JNIEnvFun {
    GetVersion(4),
    DefineClass(5),
    FindClass(6),
    FromReflectedMethod(7),
    FromReflectedField(8),
    ToReflectedMethod(9),
    GetSuperclass(10),
    IsAssignableFrom(11),
    ToReflectedField(12),
    Throw(13),
    ThrowNew(14),
    ExceptionOccurred(15),
    ExceptionDescribe(16),
    ExceptionClear(17),
    FatalError(18),
    PushLocalFrame(19),
    PopLocalFrame(20),
    NewGlobalRef(21),
    DeleteGlobalRef(22),
    DeleteLocalRef(23),
    IsSameObject(24),
    NewLocalRef(25),
    EnsureLocalCapacity(26),
    AllocObject(27),
    NewObject(28),
    NewObjectV(29),
    NewObjectA(30),
    GetObjectClass(31),
    IsInstanceOf(32),
    GetMethodID(33),
    CallObjectMethod(34),
    CallObjectMethodV(35),
    CallObjectMethodA(36),
    CallBooleanMethod(37),
    CallBooleanMethodV(38),
    CallBooleanMethodA(39),
    CallByteMethod(40),
    CallByteMethodV(41),
    CallByteMethodA(42),
    CallCharMethod(43),
    CallCharMethodV(44),
    CallCharMethodA(45),
    CallShortMethod(46),
    CallShortMethodV(47),
    CallShortMethodA(48),
    CallIntMethod(49),
    CallIntMethodV(50),
    CallIntMethodA(51),
    CallLongMethod(52),
    CallLongMethodV(53),
    CallLongMethodA(54),
    CallFloatMethod(55),
    CallFloatMethodV(56),
    CallFloatMethodA(57),
    CallDoubleMethod(58),
    CallDoubleMethodV(59),
    CallDoubleMethodA(60),
    CallVoidMethod(61),
    CallVoidMethodV(62),
    CallVoidMethodA(63),
    CallNonvirtualObjectMethod(64),
    CallNonvirtualObjectMethodV(65),
    CallNonvirtualObjectMethodA(66),
    CallNonvirtualBooleanMethod(67),
    CallNonvirtualBooleanMethodV(68),
    CallNonvirtualBooleanMethodA(69),
    CallNonvirtualByteMethod(70),
    CallNonvirtualByteMethodV(71),
    CallNonvirtualByteMethodA(72),
    CallNonvirtualCharMethod(73),
    CallNonvirtualCharMethodV(74),
    CallNonvirtualCharMethodA(75),
    CallNonvirtualShortMethod(76),
    CallNonvirtualShortMethodV(77),
    CallNonvirtualShortMethodA(78),
    CallNonvirtualIntMethod(79),
    CallNonvirtualIntMethodV(80),
    CallNonvirtualIntMethodA(81),
    CallNonvirtualLongMethod(82),
    CallNonvirtualLongMethodV(83),
    CallNonvirtualLongMethodA(84),
    CallNonvirtualFloatMethod(85),
    CallNonvirtualFloatMethodV(86),
    CallNonvirtualFloatMethodA(87),
    CallNonvirtualDoubleMethod(88),
    CallNonvirtualDoubleMethodV(89),
    CallNonvirtualDoubleMethodA(90),
    CallNonvirtualVoidMethod(91),
    CallNonvirtualVoidMethodV(92),
    CallNonvirtualVoidMethodA(93),
    GetFieldID(94),
    GetObjectField(95),
    GetBooleanField(96),
    GetByteField(97),
    GetCharField(98),
    GetShortField(99),
    GetIntField(100),
    GetLongField(101),
    GetFloatField(102),
    GetDoubleField(103),
    SetObjectField(104),
    SetBooleanField(105),
    SetByteField(106),
    SetCharField(107),
    SetShortField(108),
    SetIntField(109),
    SetLongField(110),
    SetFloatField(111),
    SetDoubleField(112),
    GetStaticMethodID(113),
    CallStaticObjectMethod(114),
    CallStaticObjectMethodV(115),
    CallStaticObjectMethodA(116),
    CallStaticBooleanMethod(117),
    CallStaticBooleanMethodV(118),
    CallStaticBooleanMethodA(119),
    CallStaticByteMethod(120),
    CallStaticByteMethodV(121),
    CallStaticByteMethodA(122),
    CallStaticCharMethod(123),
    CallStaticCharMethodV(124),
    CallStaticCharMethodA(125),
    CallStaticShortMethod(126),
    CallStaticShortMethodV(127),
    CallStaticShortMethodA(128),
    CallStaticIntMethod(129),
    CallStaticIntMethodV(130),
    CallStaticIntMethodA(131),
    CallStaticLongMethod(132),
    CallStaticLongMethodV(133),
    CallStaticLongMethodA(134),
    CallStaticFloatMethod(135),
    CallStaticFloatMethodV(136),
    CallStaticFloatMethodA(137),
    CallStaticDoubleMethod(138),
    CallStaticDoubleMethodV(139),
    CallStaticDoubleMethodA(140),
    CallStaticVoidMethod(141),
    CallStaticVoidMethodV(142),
    CallStaticVoidMethodA(143),
    GetStaticFieldID(144),
    GetStaticObjectField(145),
    GetStaticBooleanField(146),
    GetStaticByteField(147),
    GetStaticCharField(148),
    GetStaticShortField(149),
    GetStaticIntField(150),
    GetStaticLongField(151),
    GetStaticFloatField(152),
    GetStaticDoubleField(153),
    SetStaticObjectField(154),
    SetStaticBooleanField(155),
    SetStaticByteField(156),
    SetStaticCharField(157),
    SetStaticShortField(158),
    SetStaticIntField(159),
    SetStaticLongField(160),
    SetStaticFloatField(161),
    SetStaticDoubleField(162),
    NewString(163),
    GetStringLength(164),
    GetStringChars(165),
    ReleaseStringChars(166),
    NewStringUTF(167),
    GetStringUTFLength(168),
    GetStringUTFChars(169),
    ReleaseStringUTFChars(170),
    GetArrayLength(171),
    NewObjectArray(172),
    GetObjectArrayElement(173),
    SetObjectArrayElement(174),
    NewBooleanArray(175),
    NewByteArray(176),
    NewCharArray(177),
    NewShortArray(178),
    NewIntArray(179),
    NewLongArray(180),
    NewFloatArray(181),
    NewDoubleArray(182),
    GetBooleanArrayElements(183),
    GetByteArrayElements(184),
    GetCharArrayElements(185),
    GetShortArrayElements(186),
    GetIntArrayElements(187),
    GetLongArrayElements(188),
    GetFloatArrayElements(189),
    GetDoubleArrayElements(190),
    ReleaseBooleanArrayElements(191),
    ReleaseByteArrayElements(192),
    ReleaseCharArrayElements(193),
    ReleaseShortArrayElements(194),
    ReleaseIntArrayElements(195),
    ReleaseLongArrayElements(196),
    ReleaseFloatArrayElements(197),
    ReleaseDoubleArrayElements(198),
    GetBooleanArrayRegion(199),
    GetByteArrayRegion(200),
    GetCharArrayRegion(201),
    GetShortArrayRegion(202),
    GetIntArrayRegion(203),
    GetLongArrayRegion(204),
    GetFloatArrayRegion(205),
    GetDoubleArrayRegion(206),
    SetBooleanArrayRegion(207),
    SetByteArrayRegion(208),
    SetCharArrayRegion(209),
    SetShortArrayRegion(210),
    SetIntArrayRegion(211),
    SetLongArrayRegion(212),
    SetFloatArrayRegion(213),
    SetDoubleArrayRegion(214),
    RegisterNatives(215),
    UnregisterNatives(216),
    MonitorEnter(217),
    MonitorExit(218),
    GetJavaVM(219),
    GetStringRegion(220),
    GetStringUTFRegion(221),
    GetPrimitiveArrayCritical(222),
    ReleasePrimitiveArrayCritical(223),
    GetStringCritical(224),
    ReleaseStringCritical(225),
    NewWeakGlobalRef(226),
    DeleteWeakGlobalRef(227),
    ExceptionCheck(228),
    NewDirectByteBuffer(229),
    GetDirectBufferAddress(230),
    GetDirectBufferCapacity(231),
    GetObjectRefType(232);

    int index;
    JNIEnvFun(int index) {
        this.index = index;
    }
    static Map<Integer, JNIEnvFun> indexToJNIEnvFun = new HashMap<>();
    static Map<String, JNIEnvFun> cxxNameToJNIEnvFun;
    static {
        for (JNIEnvFun value : values()) {
            indexToJNIEnvFun.put(value.index, value);
        }
        cxxNameToJNIEnvFun = initializeCxxNameToJNIEnvFun();
    }

    static Map<String, JNIEnvFun> initializeCxxNameToJNIEnvFun() {
        Map<String, JNIEnvFun> map = new HashMap<>();
        map.put("_ZN7JNIEnv_10GetVersionEv", JNIEnvFun.GetVersion);
        map.put("_ZN7JNIEnv_11DefineClassEPKcP8_jobjectPKai", JNIEnvFun.DefineClass);
        map.put("_ZN7JNIEnv_9FindClassEPKc", JNIEnvFun.FindClass);
        map.put("_ZN7JNIEnv_19FromReflectedMethodEP8_jobject", JNIEnvFun.FromReflectedMethod);
        map.put("_ZN7JNIEnv_18FromReflectedFieldEP8_jobject", JNIEnvFun.FromReflectedField);
        map.put("_ZN7JNIEnv_17ToReflectedMethodEP7_jclassP10_jmethodIDh", JNIEnvFun.ToReflectedMethod);
        map.put("_ZN7JNIEnv_13GetSuperclassEP7_jclass", JNIEnvFun.GetSuperclass);
        map.put("_ZN7JNIEnv_16IsAssignableFromEP7_jclassS1_", JNIEnvFun.IsAssignableFrom);
        map.put("_ZN7JNIEnv_16ToReflectedFieldEP7_jclassP9_jfieldIDh", JNIEnvFun.ToReflectedField);
        map.put("_ZN7JNIEnv_5ThrowEP11_jthrowable", JNIEnvFun.Throw);
        map.put("_ZN7JNIEnv_8ThrowNewEP7_jclassPKc", JNIEnvFun.ThrowNew);
        map.put("_ZN7JNIEnv_17ExceptionOccurredEv", JNIEnvFun.ExceptionOccurred);
        map.put("_ZN7JNIEnv_17ExceptionDescribeEv", JNIEnvFun.ExceptionDescribe);
        map.put("_ZN7JNIEnv_14ExceptionClearEv", JNIEnvFun.ExceptionClear);
        map.put("_ZN7JNIEnv_10FatalErrorEPKc", JNIEnvFun.FatalError);
        map.put("_ZN7JNIEnv_14PushLocalFrameEi", JNIEnvFun.PushLocalFrame);
        map.put("_ZN7JNIEnv_13PopLocalFrameEP8_jobject", JNIEnvFun.PopLocalFrame);
        map.put("_ZN7JNIEnv_12NewGlobalRefEP8_jobject", JNIEnvFun.NewGlobalRef);
        map.put("_ZN7JNIEnv_15DeleteGlobalRefEP8_jobject", JNIEnvFun.DeleteGlobalRef);
        map.put("_ZN7JNIEnv_14DeleteLocalRefEP8_jobject", JNIEnvFun.DeleteLocalRef);
        map.put("_ZN7JNIEnv_12IsSameObjectEP8_jobjectS1_", JNIEnvFun.IsSameObject);
        map.put("_ZN7JNIEnv_11NewLocalRefEP8_jobject", JNIEnvFun.NewLocalRef);
        map.put("_ZN7JNIEnv_19EnsureLocalCapacityEi", JNIEnvFun.EnsureLocalCapacity);
        map.put("_ZN7JNIEnv_11AllocObjectEP7_jclass", JNIEnvFun.AllocObject);
        map.put("_ZN7JNIEnv_9NewObjectEP7_jclassP10_jmethodIDz", JNIEnvFun.NewObject);
        map.put("_ZN7JNIEnv_10NewObjectVEP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.NewObjectV);
        map.put("_ZN7JNIEnv_10NewObjectAEP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.NewObjectA);
        map.put("_ZN7JNIEnv_14GetObjectClassEP8_jobject", JNIEnvFun.GetObjectClass);
        map.put("_ZN7JNIEnv_12IsInstanceOfEP8_jobjectP7_jclass", JNIEnvFun.IsInstanceOf);
        map.put("_ZN7JNIEnv_11GetMethodIDEP7_jclassPKcS3_", JNIEnvFun.GetMethodID);
        map.put("_ZN7JNIEnv_16CallObjectMethodEP8_jobjectP10_jmethodIDz", JNIEnvFun.CallObjectMethod);
        map.put("_ZN7JNIEnv_17CallObjectMethodVEP8_jobjectP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallObjectMethodV);
        map.put("_ZN7JNIEnv_17CallObjectMethodAEP8_jobjectP10_jmethodIDPK6jvalue", JNIEnvFun.CallObjectMethodA);
        map.put("_ZN7JNIEnv_17CallBooleanMethodEP8_jobjectP10_jmethodIDz", JNIEnvFun.CallBooleanMethod);
        map.put("_ZN7JNIEnv_18CallBooleanMethodVEP8_jobjectP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallBooleanMethodV);
        map.put("_ZN7JNIEnv_18CallBooleanMethodAEP8_jobjectP10_jmethodIDPK6jvalue", JNIEnvFun.CallBooleanMethodA);
        map.put("_ZN7JNIEnv_14CallByteMethodEP8_jobjectP10_jmethodIDz", JNIEnvFun.CallByteMethod);
        map.put("_ZN7JNIEnv_15CallByteMethodVEP8_jobjectP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallByteMethodV);
        map.put("_ZN7JNIEnv_15CallByteMethodAEP8_jobjectP10_jmethodIDPK6jvalue", JNIEnvFun.CallByteMethodA);
        map.put("_ZN7JNIEnv_14CallCharMethodEP8_jobjectP10_jmethodIDz", JNIEnvFun.CallCharMethod);
        map.put("_ZN7JNIEnv_15CallCharMethodVEP8_jobjectP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallCharMethodV);
        map.put("_ZN7JNIEnv_15CallCharMethodAEP8_jobjectP10_jmethodIDPK6jvalue", JNIEnvFun.CallCharMethodA);
        map.put("_ZN7JNIEnv_15CallShortMethodEP8_jobjectP10_jmethodIDz", JNIEnvFun.CallShortMethod);
        map.put("_ZN7JNIEnv_16CallShortMethodVEP8_jobjectP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallShortMethodV);
        map.put("_ZN7JNIEnv_16CallShortMethodAEP8_jobjectP10_jmethodIDPK6jvalue", JNIEnvFun.CallShortMethodA);
        map.put("_ZN7JNIEnv_13CallIntMethodEP8_jobjectP10_jmethodIDz", JNIEnvFun.CallIntMethod);
        map.put("_ZN7JNIEnv_14CallIntMethodVEP8_jobjectP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallIntMethodV);
        map.put("_ZN7JNIEnv_14CallIntMethodAEP8_jobjectP10_jmethodIDPK6jvalue", JNIEnvFun.CallIntMethodA);
        map.put("_ZN7JNIEnv_14CallLongMethodEP8_jobjectP10_jmethodIDz", JNIEnvFun.CallLongMethod);
        map.put("_ZN7JNIEnv_15CallLongMethodVEP8_jobjectP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallLongMethodV);
        map.put("_ZN7JNIEnv_15CallLongMethodAEP8_jobjectP10_jmethodIDPK6jvalue", JNIEnvFun.CallLongMethodA);
        map.put("_ZN7JNIEnv_15CallFloatMethodEP8_jobjectP10_jmethodIDz", JNIEnvFun.CallFloatMethod);
        map.put("_ZN7JNIEnv_16CallFloatMethodVEP8_jobjectP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallFloatMethodV);
        map.put("_ZN7JNIEnv_16CallFloatMethodAEP8_jobjectP10_jmethodIDPK6jvalue", JNIEnvFun.CallFloatMethodA);
        map.put("_ZN7JNIEnv_16CallDoubleMethodEP8_jobjectP10_jmethodIDz", JNIEnvFun.CallDoubleMethod);
        map.put("_ZN7JNIEnv_17CallDoubleMethodVEP8_jobjectP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallDoubleMethodV);
        map.put("_ZN7JNIEnv_17CallDoubleMethodAEP8_jobjectP10_jmethodIDPK6jvalue", JNIEnvFun.CallDoubleMethodA);
        map.put("_ZN7JNIEnv_14CallVoidMethodEP8_jobjectP10_jmethodIDz", JNIEnvFun.CallVoidMethod);
        map.put("_ZN7JNIEnv_15CallVoidMethodVEP8_jobjectP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallVoidMethodV);
        map.put("_ZN7JNIEnv_15CallVoidMethodAEP8_jobjectP10_jmethodIDPK6jvalue", JNIEnvFun.CallVoidMethodA);
        map.put("_ZN7JNIEnv_26CallNonvirtualObjectMethodEP8_jobjectP7_jclassP10_jmethodIDz", JNIEnvFun.CallNonvirtualObjectMethod);
        map.put("_ZN7JNIEnv_27CallNonvirtualObjectMethodVEP8_jobjectP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallNonvirtualObjectMethodV);
        map.put("_ZN7JNIEnv_27CallNonvirtualObjectMethodAEP8_jobjectP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallNonvirtualObjectMethodA);
        map.put("_ZN7JNIEnv_27CallNonvirtualBooleanMethodEP8_jobjectP7_jclassP10_jmethodIDz", JNIEnvFun.CallNonvirtualBooleanMethod);
        map.put("_ZN7JNIEnv_28CallNonvirtualBooleanMethodVEP8_jobjectP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallNonvirtualBooleanMethodV);
        map.put("_ZN7JNIEnv_28CallNonvirtualBooleanMethodAEP8_jobjectP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallNonvirtualBooleanMethodA);
        map.put("_ZN7JNIEnv_24CallNonvirtualByteMethodEP8_jobjectP7_jclassP10_jmethodIDz", JNIEnvFun.CallNonvirtualByteMethod);
        map.put("_ZN7JNIEnv_25CallNonvirtualByteMethodVEP8_jobjectP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallNonvirtualByteMethodV);
        map.put("_ZN7JNIEnv_25CallNonvirtualByteMethodAEP8_jobjectP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallNonvirtualByteMethodA);
        map.put("_ZN7JNIEnv_24CallNonvirtualCharMethodEP8_jobjectP7_jclassP10_jmethodIDz", JNIEnvFun.CallNonvirtualCharMethod);
        map.put("_ZN7JNIEnv_25CallNonvirtualCharMethodVEP8_jobjectP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallNonvirtualCharMethodV);
        map.put("_ZN7JNIEnv_25CallNonvirtualCharMethodAEP8_jobjectP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallNonvirtualCharMethodA);
        map.put("_ZN7JNIEnv_25CallNonvirtualShortMethodEP8_jobjectP7_jclassP10_jmethodIDz", JNIEnvFun.CallNonvirtualShortMethod);
        map.put("_ZN7JNIEnv_26CallNonvirtualShortMethodVEP8_jobjectP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallNonvirtualShortMethodV);
        map.put("_ZN7JNIEnv_26CallNonvirtualShortMethodAEP8_jobjectP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallNonvirtualShortMethodA);
        map.put("_ZN7JNIEnv_23CallNonvirtualIntMethodEP8_jobjectP7_jclassP10_jmethodIDz", JNIEnvFun.CallNonvirtualIntMethod);
        map.put("_ZN7JNIEnv_24CallNonvirtualIntMethodVEP8_jobjectP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallNonvirtualIntMethodV);
        map.put("_ZN7JNIEnv_24CallNonvirtualIntMethodAEP8_jobjectP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallNonvirtualIntMethodA);
        map.put("_ZN7JNIEnv_24CallNonvirtualLongMethodEP8_jobjectP7_jclassP10_jmethodIDz", JNIEnvFun.CallNonvirtualLongMethod);
        map.put("_ZN7JNIEnv_25CallNonvirtualLongMethodVEP8_jobjectP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallNonvirtualLongMethodV);
        map.put("_ZN7JNIEnv_25CallNonvirtualLongMethodAEP8_jobjectP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallNonvirtualLongMethodA);
        map.put("_ZN7JNIEnv_25CallNonvirtualFloatMethodEP8_jobjectP7_jclassP10_jmethodIDz", JNIEnvFun.CallNonvirtualFloatMethod);
        map.put("_ZN7JNIEnv_26CallNonvirtualFloatMethodVEP8_jobjectP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallNonvirtualFloatMethodV);
        map.put("_ZN7JNIEnv_26CallNonvirtualFloatMethodAEP8_jobjectP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallNonvirtualFloatMethodA);
        map.put("_ZN7JNIEnv_26CallNonvirtualDoubleMethodEP8_jobjectP7_jclassP10_jmethodIDz", JNIEnvFun.CallNonvirtualDoubleMethod);
        map.put("_ZN7JNIEnv_27CallNonvirtualDoubleMethodVEP8_jobjectP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallNonvirtualDoubleMethodV);
        map.put("_ZN7JNIEnv_27CallNonvirtualDoubleMethodAEP8_jobjectP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallNonvirtualDoubleMethodA);
        map.put("_ZN7JNIEnv_24CallNonvirtualVoidMethodEP8_jobjectP7_jclassP10_jmethodIDz", JNIEnvFun.CallNonvirtualVoidMethod);
        map.put("_ZN7JNIEnv_25CallNonvirtualVoidMethodVEP8_jobjectP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallNonvirtualVoidMethodV);
        map.put("_ZN7JNIEnv_25CallNonvirtualVoidMethodAEP8_jobjectP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallNonvirtualVoidMethodA);
        map.put("_ZN7JNIEnv_10GetFieldIDEP7_jclassPKcS3_", JNIEnvFun.GetFieldID);
        map.put("_ZN7JNIEnv_14GetObjectFieldEP8_jobjectP9_jfieldID", JNIEnvFun.GetObjectField);
        map.put("_ZN7JNIEnv_15GetBooleanFieldEP8_jobjectP9_jfieldID", JNIEnvFun.GetBooleanField);
        map.put("_ZN7JNIEnv_12GetByteFieldEP8_jobjectP9_jfieldID", JNIEnvFun.GetByteField);
        map.put("_ZN7JNIEnv_12GetCharFieldEP8_jobjectP9_jfieldID", JNIEnvFun.GetCharField);
        map.put("_ZN7JNIEnv_13GetShortFieldEP8_jobjectP9_jfieldID", JNIEnvFun.GetShortField);
        map.put("_ZN7JNIEnv_11GetIntFieldEP8_jobjectP9_jfieldID", JNIEnvFun.GetIntField);
        map.put("_ZN7JNIEnv_12GetLongFieldEP8_jobjectP9_jfieldID", JNIEnvFun.GetLongField);
        map.put("_ZN7JNIEnv_13GetFloatFieldEP8_jobjectP9_jfieldID", JNIEnvFun.GetFloatField);
        map.put("_ZN7JNIEnv_14GetDoubleFieldEP8_jobjectP9_jfieldID", JNIEnvFun.GetDoubleField);
        map.put("_ZN7JNIEnv_14SetObjectFieldEP8_jobjectP9_jfieldIDS1_", JNIEnvFun.SetObjectField);
        map.put("_ZN7JNIEnv_15SetBooleanFieldEP8_jobjectP9_jfieldIDh", JNIEnvFun.SetBooleanField);
        map.put("_ZN7JNIEnv_12SetByteFieldEP8_jobjectP9_jfieldIDa", JNIEnvFun.SetByteField);
        map.put("_ZN7JNIEnv_12SetCharFieldEP8_jobjectP9_jfieldIDt", JNIEnvFun.SetCharField);
        map.put("_ZN7JNIEnv_13SetShortFieldEP8_jobjectP9_jfieldIDs", JNIEnvFun.SetShortField);
        map.put("_ZN7JNIEnv_11SetIntFieldEP8_jobjectP9_jfieldIDi", JNIEnvFun.SetIntField);
        map.put("_ZN7JNIEnv_12SetLongFieldEP8_jobjectP9_jfieldIDl", JNIEnvFun.SetLongField);
        map.put("_ZN7JNIEnv_13SetFloatFieldEP8_jobjectP9_jfieldIDf", JNIEnvFun.SetFloatField);
        map.put("_ZN7JNIEnv_14SetDoubleFieldEP8_jobjectP9_jfieldIDd", JNIEnvFun.SetDoubleField);
        map.put("_ZN7JNIEnv_17GetStaticMethodIDEP7_jclassPKcS3_", JNIEnvFun.GetStaticMethodID);
        map.put("_ZN7JNIEnv_22CallStaticObjectMethodEP7_jclassP10_jmethodIDz", JNIEnvFun.CallStaticObjectMethod);
        map.put("_ZN7JNIEnv_23CallStaticObjectMethodVEP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallStaticObjectMethodV);
        map.put("_ZN7JNIEnv_23CallStaticObjectMethodAEP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallStaticObjectMethodA);
        map.put("_ZN7JNIEnv_23CallStaticBooleanMethodEP7_jclassP10_jmethodIDz", JNIEnvFun.CallStaticBooleanMethod);
        map.put("_ZN7JNIEnv_24CallStaticBooleanMethodVEP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallStaticBooleanMethodV);
        map.put("_ZN7JNIEnv_24CallStaticBooleanMethodAEP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallStaticBooleanMethodA);
        map.put("_ZN7JNIEnv_20CallStaticByteMethodEP7_jclassP10_jmethodIDz", JNIEnvFun.CallStaticByteMethod);
        map.put("_ZN7JNIEnv_21CallStaticByteMethodVEP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallStaticByteMethodV);
        map.put("_ZN7JNIEnv_21CallStaticByteMethodAEP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallStaticByteMethodA);
        map.put("_ZN7JNIEnv_20CallStaticCharMethodEP7_jclassP10_jmethodIDz", JNIEnvFun.CallStaticCharMethod);
        map.put("_ZN7JNIEnv_21CallStaticCharMethodVEP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallStaticCharMethodV);
        map.put("_ZN7JNIEnv_21CallStaticCharMethodAEP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallStaticCharMethodA);
        map.put("_ZN7JNIEnv_21CallStaticShortMethodEP7_jclassP10_jmethodIDz", JNIEnvFun.CallStaticShortMethod);
        map.put("_ZN7JNIEnv_22CallStaticShortMethodVEP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallStaticShortMethodV);
        map.put("_ZN7JNIEnv_22CallStaticShortMethodAEP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallStaticShortMethodA);
        map.put("_ZN7JNIEnv_19CallStaticIntMethodEP7_jclassP10_jmethodIDz", JNIEnvFun.CallStaticIntMethod);
        map.put("_ZN7JNIEnv_20CallStaticIntMethodVEP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallStaticIntMethodV);
        map.put("_ZN7JNIEnv_20CallStaticIntMethodAEP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallStaticIntMethodA);
        map.put("_ZN7JNIEnv_20CallStaticLongMethodEP7_jclassP10_jmethodIDz", JNIEnvFun.CallStaticLongMethod);
        map.put("_ZN7JNIEnv_21CallStaticLongMethodVEP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallStaticLongMethodV);
        map.put("_ZN7JNIEnv_21CallStaticLongMethodAEP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallStaticLongMethodA);
        map.put("_ZN7JNIEnv_21CallStaticFloatMethodEP7_jclassP10_jmethodIDz", JNIEnvFun.CallStaticFloatMethod);
        map.put("_ZN7JNIEnv_22CallStaticFloatMethodVEP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallStaticFloatMethodV);
        map.put("_ZN7JNIEnv_22CallStaticFloatMethodAEP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallStaticFloatMethodA);
        map.put("_ZN7JNIEnv_22CallStaticDoubleMethodEP7_jclassP10_jmethodIDz", JNIEnvFun.CallStaticDoubleMethod);
        map.put("_ZN7JNIEnv_23CallStaticDoubleMethodVEP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallStaticDoubleMethodV);
        map.put("_ZN7JNIEnv_23CallStaticDoubleMethodAEP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallStaticDoubleMethodA);
        map.put("_ZN7JNIEnv_20CallStaticVoidMethodEP7_jclassP10_jmethodIDz", JNIEnvFun.CallStaticVoidMethod);
        map.put("_ZN7JNIEnv_21CallStaticVoidMethodVEP7_jclassP10_jmethodIDP13__va_list_tag", JNIEnvFun.CallStaticVoidMethodV);
        map.put("_ZN7JNIEnv_21CallStaticVoidMethodAEP7_jclassP10_jmethodIDPK6jvalue", JNIEnvFun.CallStaticVoidMethodA);
        map.put("_ZN7JNIEnv_16GetStaticFieldIDEP7_jclassPKcS3_", JNIEnvFun.GetStaticFieldID);
        map.put("_ZN7JNIEnv_20GetStaticObjectFieldEP7_jclassP9_jfieldID", JNIEnvFun.GetStaticObjectField);
        map.put("_ZN7JNIEnv_21GetStaticBooleanFieldEP7_jclassP9_jfieldID", JNIEnvFun.GetStaticBooleanField);
        map.put("_ZN7JNIEnv_18GetStaticByteFieldEP7_jclassP9_jfieldID", JNIEnvFun.GetStaticByteField);
        map.put("_ZN7JNIEnv_18GetStaticCharFieldEP7_jclassP9_jfieldID", JNIEnvFun.GetStaticCharField);
        map.put("_ZN7JNIEnv_19GetStaticShortFieldEP7_jclassP9_jfieldID", JNIEnvFun.GetStaticShortField);
        map.put("_ZN7JNIEnv_17GetStaticIntFieldEP7_jclassP9_jfieldID", JNIEnvFun.GetStaticIntField);
        map.put("_ZN7JNIEnv_18GetStaticLongFieldEP7_jclassP9_jfieldID", JNIEnvFun.GetStaticLongField);
        map.put("_ZN7JNIEnv_19GetStaticFloatFieldEP7_jclassP9_jfieldID", JNIEnvFun.GetStaticFloatField);
        map.put("_ZN7JNIEnv_20GetStaticDoubleFieldEP7_jclassP9_jfieldID", JNIEnvFun.GetStaticDoubleField);
        map.put("_ZN7JNIEnv_20SetStaticObjectFieldEP7_jclassP9_jfieldIDP8_jobject", JNIEnvFun.SetStaticObjectField);
        map.put("_ZN7JNIEnv_21SetStaticBooleanFieldEP7_jclassP9_jfieldIDh", JNIEnvFun.SetStaticBooleanField);
        map.put("_ZN7JNIEnv_18SetStaticByteFieldEP7_jclassP9_jfieldIDa", JNIEnvFun.SetStaticByteField);
        map.put("_ZN7JNIEnv_18SetStaticCharFieldEP7_jclassP9_jfieldIDt", JNIEnvFun.SetStaticCharField);
        map.put("_ZN7JNIEnv_19SetStaticShortFieldEP7_jclassP9_jfieldIDs", JNIEnvFun.SetStaticShortField);
        map.put("_ZN7JNIEnv_17SetStaticIntFieldEP7_jclassP9_jfieldIDi", JNIEnvFun.SetStaticIntField);
        map.put("_ZN7JNIEnv_18SetStaticLongFieldEP7_jclassP9_jfieldIDl", JNIEnvFun.SetStaticLongField);
        map.put("_ZN7JNIEnv_19SetStaticFloatFieldEP7_jclassP9_jfieldIDf", JNIEnvFun.SetStaticFloatField);
        map.put("_ZN7JNIEnv_20SetStaticDoubleFieldEP7_jclassP9_jfieldIDd", JNIEnvFun.SetStaticDoubleField);
        map.put("_ZN7JNIEnv_9NewStringEPKti", JNIEnvFun.NewString);
        map.put("_ZN7JNIEnv_15GetStringLengthEP8_jstring", JNIEnvFun.GetStringLength);
        map.put("_ZN7JNIEnv_14GetStringCharsEP8_jstringPh", JNIEnvFun.GetStringChars);
        map.put("_ZN7JNIEnv_18ReleaseStringCharsEP8_jstringPKt", JNIEnvFun.ReleaseStringChars);
        map.put("_ZN7JNIEnv_12NewStringUTFEPKc", JNIEnvFun.NewStringUTF);
        map.put("_ZN7JNIEnv_18GetStringUTFLengthEP8_jstring", JNIEnvFun.GetStringUTFLength);
        map.put("_ZN7JNIEnv_17GetStringUTFCharsEP8_jstringPh", JNIEnvFun.GetStringUTFChars);
        map.put("_ZN7JNIEnv_21ReleaseStringUTFCharsEP8_jstringPKc", JNIEnvFun.ReleaseStringUTFChars);
        map.put("_ZN7JNIEnv_14GetArrayLengthEP7_jarray", JNIEnvFun.GetArrayLength);
        map.put("_ZN7JNIEnv_14NewObjectArrayEiP7_jclassP8_jobject", JNIEnvFun.NewObjectArray);
        map.put("_ZN7JNIEnv_21GetObjectArrayElementEP13_jobjectArrayi", JNIEnvFun.GetObjectArrayElement);
        map.put("_ZN7JNIEnv_21SetObjectArrayElementEP13_jobjectArrayiP8_jobject", JNIEnvFun.SetObjectArrayElement);
        map.put("_ZN7JNIEnv_15NewBooleanArrayEi", JNIEnvFun.NewBooleanArray);
        map.put("_ZN7JNIEnv_12NewByteArrayEi", JNIEnvFun.NewByteArray);
        map.put("_ZN7JNIEnv_12NewCharArrayEi", JNIEnvFun.NewCharArray);
        map.put("_ZN7JNIEnv_13NewShortArrayEi", JNIEnvFun.NewShortArray);
        map.put("_ZN7JNIEnv_11NewIntArrayEi", JNIEnvFun.NewIntArray);
        map.put("_ZN7JNIEnv_12NewLongArrayEi", JNIEnvFun.NewLongArray);
        map.put("_ZN7JNIEnv_13NewFloatArrayEi", JNIEnvFun.NewFloatArray);
        map.put("_ZN7JNIEnv_14NewDoubleArrayEi", JNIEnvFun.NewDoubleArray);
        map.put("_ZN7JNIEnv_23GetBooleanArrayElementsEP14_jbooleanArrayPh", JNIEnvFun.GetBooleanArrayElements);
        map.put("_ZN7JNIEnv_20GetByteArrayElementsEP11_jbyteArrayPh", JNIEnvFun.GetByteArrayElements);
        map.put("_ZN7JNIEnv_20GetCharArrayElementsEP11_jcharArrayPh", JNIEnvFun.GetCharArrayElements);
        map.put("_ZN7JNIEnv_21GetShortArrayElementsEP12_jshortArrayPh", JNIEnvFun.GetShortArrayElements);
        map.put("_ZN7JNIEnv_19GetIntArrayElementsEP10_jintArrayPh", JNIEnvFun.GetIntArrayElements);
        map.put("_ZN7JNIEnv_20GetLongArrayElementsEP11_jlongArrayPh", JNIEnvFun.GetLongArrayElements);
        map.put("_ZN7JNIEnv_21GetFloatArrayElementsEP12_jfloatArrayPh", JNIEnvFun.GetFloatArrayElements);
        map.put("_ZN7JNIEnv_22GetDoubleArrayElementsEP13_jdoubleArrayPh", JNIEnvFun.GetDoubleArrayElements);
        map.put("_ZN7JNIEnv_27ReleaseBooleanArrayElementsEP14_jbooleanArrayPhi", JNIEnvFun.ReleaseBooleanArrayElements);
        map.put("_ZN7JNIEnv_24ReleaseByteArrayElementsEP11_jbyteArrayPai", JNIEnvFun.ReleaseByteArrayElements);
        map.put("_ZN7JNIEnv_24ReleaseCharArrayElementsEP11_jcharArrayPti", JNIEnvFun.ReleaseCharArrayElements);
        map.put("_ZN7JNIEnv_25ReleaseShortArrayElementsEP12_jshortArrayPsi", JNIEnvFun.ReleaseShortArrayElements);
        map.put("_ZN7JNIEnv_23ReleaseIntArrayElementsEP10_jintArrayPii", JNIEnvFun.ReleaseIntArrayElements);
        map.put("_ZN7JNIEnv_24ReleaseLongArrayElementsEP11_jlongArrayPli", JNIEnvFun.ReleaseLongArrayElements);
        map.put("_ZN7JNIEnv_25ReleaseFloatArrayElementsEP12_jfloatArrayPfi", JNIEnvFun.ReleaseFloatArrayElements);
        map.put("_ZN7JNIEnv_26ReleaseDoubleArrayElementsEP13_jdoubleArrayPdi", JNIEnvFun.ReleaseDoubleArrayElements);
        map.put("_ZN7JNIEnv_21GetBooleanArrayRegionEP14_jbooleanArrayiiPh", JNIEnvFun.GetBooleanArrayRegion);
        map.put("_ZN7JNIEnv_18GetByteArrayRegionEP11_jbyteArrayiiPa", JNIEnvFun.GetByteArrayRegion);
        map.put("_ZN7JNIEnv_18GetCharArrayRegionEP11_jcharArrayiiPt", JNIEnvFun.GetCharArrayRegion);
        map.put("_ZN7JNIEnv_19GetShortArrayRegionEP12_jshortArrayiiPs", JNIEnvFun.GetShortArrayRegion);
        map.put("_ZN7JNIEnv_17GetIntArrayRegionEP10_jintArrayiiPi", JNIEnvFun.GetIntArrayRegion);
        map.put("_ZN7JNIEnv_18GetLongArrayRegionEP11_jlongArrayiiPl", JNIEnvFun.GetLongArrayRegion);
        map.put("_ZN7JNIEnv_19GetFloatArrayRegionEP12_jfloatArrayiiPf", JNIEnvFun.GetFloatArrayRegion);
        map.put("_ZN7JNIEnv_20GetDoubleArrayRegionEP13_jdoubleArrayiiPd", JNIEnvFun.GetDoubleArrayRegion);
        map.put("_ZN7JNIEnv_21SetBooleanArrayRegionEP14_jbooleanArrayiiPKh", JNIEnvFun.SetBooleanArrayRegion);
        map.put("_ZN7JNIEnv_18SetByteArrayRegionEP11_jbyteArrayiiPKa", JNIEnvFun.SetByteArrayRegion);
        map.put("_ZN7JNIEnv_18SetCharArrayRegionEP11_jcharArrayiiPKt", JNIEnvFun.SetCharArrayRegion);
        map.put("_ZN7JNIEnv_19SetShortArrayRegionEP12_jshortArrayiiPKs", JNIEnvFun.SetShortArrayRegion);
        map.put("_ZN7JNIEnv_17SetIntArrayRegionEP10_jintArrayiiPKi", JNIEnvFun.SetIntArrayRegion);
        map.put("_ZN7JNIEnv_18SetLongArrayRegionEP11_jlongArrayiiPKl", JNIEnvFun.SetLongArrayRegion);
        map.put("_ZN7JNIEnv_19SetFloatArrayRegionEP12_jfloatArrayiiPKf", JNIEnvFun.SetFloatArrayRegion);
        map.put("_ZN7JNIEnv_20SetDoubleArrayRegionEP13_jdoubleArrayiiPKd", JNIEnvFun.SetDoubleArrayRegion);
        map.put("_ZN7JNIEnv_15RegisterNativesEP7_jclassPK15JNINativeMethodi", JNIEnvFun.RegisterNatives);
        map.put("_ZN7JNIEnv_17UnregisterNativesEP7_jclass", JNIEnvFun.UnregisterNatives);
        map.put("_ZN7JNIEnv_12MonitorEnterEP8_jobject", JNIEnvFun.MonitorEnter);
        map.put("_ZN7JNIEnv_11MonitorExitEP8_jobject", JNIEnvFun.MonitorExit);
        map.put("_ZN7JNIEnv_9GetJavaVMEPP7JavaVM_", JNIEnvFun.GetJavaVM);
        map.put("_ZN7JNIEnv_15GetStringRegionEP8_jstringiiPt", JNIEnvFun.GetStringRegion);
        map.put("_ZN7JNIEnv_18GetStringUTFRegionEP8_jstringiiPc", JNIEnvFun.GetStringUTFRegion);
        map.put("_ZN7JNIEnv_25GetPrimitiveArrayCriticalEP7_jarrayPh", JNIEnvFun.GetPrimitiveArrayCritical);
        map.put("_ZN7JNIEnv_29ReleasePrimitiveArrayCriticalEP7_jarrayPvi", JNIEnvFun.ReleasePrimitiveArrayCritical);
        map.put("_ZN7JNIEnv_17GetStringCriticalEP8_jstringPh", JNIEnvFun.GetStringCritical);
        map.put("_ZN7JNIEnv_21ReleaseStringCriticalEP8_jstringPKt", JNIEnvFun.ReleaseStringCritical);
        map.put("_ZN7JNIEnv_16NewWeakGlobalRefEP8_jobject", JNIEnvFun.NewWeakGlobalRef);
        map.put("_ZN7JNIEnv_19DeleteWeakGlobalRefEP8_jobject", JNIEnvFun.DeleteWeakGlobalRef);
        map.put("_ZN7JNIEnv_14ExceptionCheckEv", JNIEnvFun.ExceptionCheck);
        map.put("_ZN7JNIEnv_19NewDirectByteBufferEPvl", JNIEnvFun.NewDirectByteBuffer);
        map.put("_ZN7JNIEnv_22GetDirectBufferAddressEP8_jobject", JNIEnvFun.GetDirectBufferAddress);
        map.put("_ZN7JNIEnv_23GetDirectBufferCapacityEP8_jobject", JNIEnvFun.GetDirectBufferCapacity);
        map.put("_ZN7JNIEnv_16GetObjectRefTypeEP8_jobject", JNIEnvFun.GetObjectRefType);
        return map;
    }

    public static JNIEnvFun getJNIEnvFunByIndex(int index) {
        return indexToJNIEnvFun.get(index);
    }
    public static JNIEnvFun getJNIEnvFunByCxxName(String name) {
        return cxxNameToJNIEnvFun.get(name);
    }

}
