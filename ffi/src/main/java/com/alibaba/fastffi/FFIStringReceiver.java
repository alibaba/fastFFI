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

import sun.misc.Unsafe;

public interface FFIStringReceiver {

    default void fromJavaString(String string) {
        byte[] bytes = string.getBytes(FFIStringProvider.UTF8);
        long size = bytes.length * Unsafe.ARRAY_BYTE_INDEX_SCALE;
        resize(size);
        FFIUnsafe.U.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, data(), size);
    }

    void resize(long size);
    long data();
}
