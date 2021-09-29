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

import org.junit.Test;

import java.util.Arrays;

import static com.alibaba.fastffi.llvm4jni.JNINaming.decodeNativeMethodName;
import static com.alibaba.fastffi.llvm4jni.JNINaming.encodeNativeMethodName;
import static org.junit.Assert.assertTrue;

public class JNINamingTest {

    static void test(String encoded) {
        String[] decoded = decodeNativeMethodName(encoded);
        if (decoded[2] != null) {
            decoded[2] = "(" + decoded[2] + ")";
        }
        assertTrue(encodeNativeMethodName(decoded).equals(encoded));
    }

    @Test
    public void testNaming() {
        String[] encoded = new String[] {
                "Java_Native_test__I",
                "Java_Native_test__Ljava_lang_String_2",
                "Java_Native_test__ILjava_lang_String_2"
        };
        Arrays.stream(encoded).forEach(JNINamingTest::test);
    }
}
