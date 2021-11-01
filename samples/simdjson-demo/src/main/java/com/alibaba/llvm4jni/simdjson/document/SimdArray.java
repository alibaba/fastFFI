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
package com.alibaba.llvm4jni.simdjson.document;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;

@FFIGen
@FFITypeAlias("simdjson::dom::array")
@CXXHead("simdjson.h")
public interface SimdArray extends FFIPointer, Iterable<SimdElement> {

    Factory factory = FFITypeFactory.getFactory(SimdArray.class);

    static SimdArray create() {
        return factory.create();
    }

    @FFIFactory
    interface Factory {
        SimdArray create();
    }

    @FFIGen
    @FFITypeAlias("simdjson::dom::array::iterator")
    @CXXHead("simdjson.h")
    interface Iterator extends FFIPointer {
        @CXXValue
        @CXXOperator("*")
        SimdElement get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEqual(@CXXReference Iterator other);
    }

    default java.util.Iterator<SimdElement> iterator() {
        return new java.util.Iterator<SimdElement>() {
            Iterator current = begin();
            Iterator end = end();

            @Override
            public boolean hasNext() {
                return current.notEqual(end);
            }

            @Override
            public SimdElement next() {
                SimdElement v = current.get();
                current.next();
                return v;
            }
        };
    }

    @CXXValue Iterator begin();
    @CXXValue Iterator end();
}
