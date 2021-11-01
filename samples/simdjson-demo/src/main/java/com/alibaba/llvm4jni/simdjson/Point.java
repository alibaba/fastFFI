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
package com.alibaba.llvm4jni.simdjson;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;

@FFIGen
@FFITypeAlias("simdjson::point")
@CXXHead("benchmarks.h")
public interface Point extends CXXPointer {

    Factory factory = FFITypeFactory.getFactory(Point.class);

    @FFIFactory
    interface Factory {
        Point create();
        Point create(double x, double y, double z);
        @CXXValue Point createStack();
        @CXXValue Point createStack(double x, double y, double z);
    }

    @FFIGetter double x();
    @FFIGetter double y();
    @FFIGetter double z();

    void set(double x, double y, double z);
}
