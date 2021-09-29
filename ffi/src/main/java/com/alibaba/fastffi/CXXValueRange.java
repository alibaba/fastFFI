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

import java.util.Iterator;

public interface CXXValueRange<T extends CXXValueRangeElement<T>> extends CXXRange<T> {

    default Iterable<T> locals() {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    T current = begin().copy();
                    T end = end();
                    Boolean hasNext = !current.eq(end);

                    @Override
                    public boolean hasNext() {
                        if (hasNext == null) {
                            current.inc();
                            hasNext = !current.eq(end);
                        }
                        return hasNext;
                    }

                    @Override
                    public T next() {
                        hasNext = null;
                        return current;
                    }
                };
            }
        };
    }

    default Iterator<T> iterator() {
        return new Iterator<T>() {
            T current = begin().copy();
            T end = end();

            @Override
            public boolean hasNext() {
                return !current.eq(end);
            }

            @Override
            public T next() {
                T val = current.copy();
                current.inc();
                return val;
            }
        };
    }
}
