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
package com.alibaba.fastffi;

import java.util.*;
import java.util.stream.Collectors;

public class FFIUtils {


    /**
     * https://docs.oracle.com/javase/1.5.0/docs/guide/jni/spec/design.html
     * Dynamic linkers resolve entries based on their names.
     * A native method name is concatenated from the following components:
     * <ul>
     *   <li>the prefix Java_
     *   <li>a mangled fully-qualified class name
     *   <li>an underscore (“_”) separator
     *   <li>a mangled method name
     *   <li>for overloaded native methods, two underscores (“__”) followed by the mangled argument signature
     * </ul>
     * Escape Sequence
     * Denotes
     * _0XXXX
     * a Unicode character XXXX.
     * Note that lower case is used
     * to represent non-ASCII
     * Unicode characters, e.g.,
     * _0abcd as opposed to
     * _0ABCD.
     * _1
     * the character “_”
     * _2
     * the character “;” in signatures
     * _3
     * the character “[“ in signatures
     * @return
     */
    public static String[] decodeNativeMethodName(String str) {
        String name = str;
        if (!name.startsWith("Java_")) {
            throw new IllegalArgumentException("Illegal native function name: " + str);
        }
        name = name.substring(5);
        String methodDesc = null;
        int index = name.lastIndexOf("__");
        if (index != -1) {
            methodDesc = decode(name.substring(index + 2)).replace('_', '/');
            name = decode(name.substring(0, index));
        }
        index = name.lastIndexOf("_");
        if (index == -1) {
            throw new IllegalArgumentException("Illegal native function name: " + str);
        }
        String className = name.substring(0, index).replace('_', '/');
        String methodName = name.substring(index + 1);
        return new String[] { className, methodName, methodDesc};
    }

    public static String encodeNativeMethodName(String[] names) {
        return encodeNativeMethodName(names[0], names[1], names[2]);
    }

    public static String encodeClassName(String className) {
        return Arrays.stream(className.split("[.]")).map(seg -> encode(seg)).collect(Collectors.joining("_"));
    }

    public static String encodeNativeMethodName(String className, String methodName, String methodDesc) {
        StringBuilder sb = new StringBuilder();
        sb.append("Java_").append(encodeClassName(className)).append('_').append(encode(methodName));
        if (methodDesc != null) {
            if (!methodDesc.startsWith("(")) {
                throw new IllegalArgumentException("Invalid method descriptor: " + methodDesc);
            }
            int index = methodDesc.indexOf(')');
            sb.append("__").append(encode(methodDesc.substring(1, index)));
        }
        return sb.toString();
    }

    public static String encode(String str) {
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        int i = 0;
        while (i < chars.length) {
            char c = chars[i++];
            if (c <= 0x7f && Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else {
                if (c == '_') {
                    sb.append("_1");
                } else if (c == '/') {
                    sb.append("_");
                } else if (c == ';') {
                    sb.append("_2");
                } else if (c == '[') {
                    sb.append("_3");
                } else {
                    sb.append("_0").append(String.format("%04x", (int)c));
                }
            }
        }
        return sb.toString();
    }

    public static String decode(String str) {
        String desc = str.replace("_1", "_").replace("_2", ";").replace("_3", "[");
        if (desc.indexOf("_0") == -1) {
            return desc;
        }
        char[] chars = desc.toCharArray();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < chars.length) {
            char c = chars[i];
            if (c == '_') {
                if (i + 1 < chars.length) {
                    throw new IllegalArgumentException("Invalid input: " + str);
                }
                if (chars[i + 1] == '0') {
                    if (i + 1 + 4 < chars.length) {
                        throw new IllegalArgumentException("Invalid input: " + str);
                    }
                    String unicode = new String(chars, i + 2, 4);
                    sb.append((char) Short.parseShort(unicode));
                    i += 6;
                } else {
                    sb.append(c);
                    i++;
                }
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }

    public static <K,V> void addToMapList(Map<K, List<V>> map, K key, V value) {
        List<V> values = map.get(key);
        if (values == null) {
            map.put(key, Collections.singletonList(value));
            return;
        } else if (values.size() == 1) {
            List<V> newValues = new ArrayList<>(3);
            newValues.add(values.get(0));
            newValues.add(value);
            map.put(key, newValues);
            return;
        } else {
            values.add(value);
            return;
        }
    }
}
