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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Utils {

    public static <K,V> void addToMapList(Map<K, List<V>> map, K key, V v) {
        List<V> values = map.get(key);
        if (values == null) {
            map.put(key, Collections.singletonList(v));
        } else if (values.size() == 1) {
            values = new ArrayList<>(values);
            values.add(v);
            map.put(key, values);
        } else {
            values.add(v);
        }
    }

    public static String getBinaryName(Class<?> cls) {
        if (cls.isPrimitive() || cls.isArray()) {
            throw new IllegalArgumentException("TODO");
        }
        return cls.getName().replace('.', '/');
    }

    public static String getBinaryDescriptor(Class<?> cls) {
        if (cls.isPrimitive() || cls.isArray()) {
            throw new IllegalArgumentException("TODO");
        }
        return "L" + cls.getName().replace('.', '/') + ";";
    }

    public static boolean powerOfTwo(int value) {
        return (value & -value) == value;
    }

    /**
     * Allocated in reverse order
     * @param top
     * @param size
     * @param alignment
     * @return the address of the allocation
     */
    public static long allocateStack(long top, long size, int alignment) {
        if (!powerOfTwo(alignment)) {
            throw new IllegalArgumentException("Alignment must be power of 2, got " + alignment);
        }
        return (top - size) & -alignment;
    }
}
