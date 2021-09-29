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
package com.alibaba.fastffi.annotation.test;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen(library = "ffitest")
@CXXHead("ffitest.h")
@CXXTemplate(cxx = "int", java = "java.lang.Integer")
@CXXTemplate(cxx = "char", java = "java.lang.Byte")
@CXXTemplate(cxx = "test::TestTypeMappingGenericSuper<int>", java = "TestTypeMappingGenericSuper<Integer>")
@FFITypeAlias("test::TestTypeMappingGenericSuper")
public interface TestTypeMappingGenericSuper<T> extends CXXPointer {
    @FFIFactory
    interface Factory<T> {
        TestTypeMappingGenericSuper<T> create(@CXXValue T value);
    }
    @CXXValue T getValue();
}
