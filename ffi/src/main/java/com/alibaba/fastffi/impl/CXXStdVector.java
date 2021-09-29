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
package com.alibaba.fastffi.impl;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFIVector;


/**
 * To support new templates (e.g., {@code Bean}, simply define a new class:
 * <pre>
 * &#64;FFIGen
 * &#64;FFITypeAlias("std::vector")
 * &#64;CXXTemplate(cxx = "bean", java = "Bean")
 * class BeanVector&#60;E&#62; extends CXXStdVector&#60;E&#62; {
 *   &#64;FFIFactory
 *   interface Factory&#60;E&#62; {
 *     BeanVector&#60;E&#62; create();
 *   }
 * }
 *
 * This class is mainly used by FFIMirror, where all type mapping are predefined.
 * In that case, we can infer the type mapping from Java type system
 * without any CXXTemplate annotation.
 * </pre>
 * @param <E>
 */
@FFIGen
@FFITypeAlias("std::vector")
@CXXHead(system = "vector")
public interface CXXStdVector<E> extends FFIVector<E> {

    @FFIFactory
    interface Factory<E> extends FFIVector.Factory<E> {
        CXXStdVector<E> create();
        CXXStdVector<E> create(int capacity);
    }

    long size();
    boolean empty();
    void push_back(@CXXReference E e);

    @CXXOperator("[]")
    @CXXReference E get(long index);

    @CXXOperator("[]")
    void set(long index, @CXXReference E value);

    long data();
    long capacity();
    void reserve(long cap);
    void resize(long size);
    void clear();

    @CXXOperator("delete")
    void dispose();
}
