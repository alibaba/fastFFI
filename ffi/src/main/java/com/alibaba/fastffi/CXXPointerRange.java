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

public interface CXXPointerRange<T extends CXXPointerRangeElement> extends CXXRange<T> {

    default Iterable<T> locals() {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    T begin = (T) begin(); // no need to copy since we change address of the pointer.
                    long currentAddress = begin.getAddress();
                    long endAddress = end().getAddress();
                    long elementSize = begin.elementSize();
                    @Override
                    public boolean hasNext() {
                        return currentAddress != endAddress;
                    }

                    @Override
                    public T next() {
                        begin.moveToV(currentAddress);
                        currentAddress += elementSize;
                        return begin;
                    }
                };
            }
        };
    }

    default Iterator<T> iterator() {
        return new Iterator<T>() {
            T begin = begin();
            long currentAddress = begin.getAddress();
            long endAddress = end().getAddress();
            long elementSize = begin.elementSize();
            @Override
            public boolean hasNext() {
                return currentAddress != endAddress;
            }

            @Override
            public T next() {
                T val = (T) begin.moveTo(currentAddress);
                currentAddress += elementSize;
                return val;
            }
        };
    }
}
