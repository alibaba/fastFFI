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

/**
 * A lightweight
 *
 * <pre>
 * try (CXXStackObject begin = vec.begin(); CXXObject end = vec.end()) {
 *     while (!begin.eq(end)) {
 *         //
 *         begin = begin.inc();
 *     }
 * }
 * </pre>
 *
 * @param <T>
 */
public class CXXStackObject<T extends CXXPointer> implements AutoCloseable {

    private final T pointer;
    private boolean closed = false;

    public CXXStackObject(T pointer) {
        this.pointer = pointer;
    }

    public T get() {
        return this.pointer;
    }

    @Override
    public void close() {
        if (!closed) {
            pointer.delete();
            closed = true;
        } else {
            throw new IllegalStateException("Cannot delete a FFIPoitner twice: " + pointer);
        }
    }
}
