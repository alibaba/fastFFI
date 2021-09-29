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
import com.alibaba.fastffi.FFIByteString;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.StringProvider;
import com.alibaba.fastffi.StringReceiver;

@FFIGen
@FFITypeAlias("std::string")
@CXXHead(system = "string")
public interface CXXStdString extends FFIByteString, StringReceiver, StringProvider {

    Factory factory = FFITypeFactory.getFactory(CXXStdString.class);

    @FFIFactory
    interface Factory extends FFIByteString.Factory {
        CXXStdString create();
    }

    long data();
    void resize(long size);
    void reserve(long cap);
    void clear();

    @CXXOperator("[]")
    byte byteAt(long index);

    @Override
    long size();

    default void copyFrom(String string) {
        fromJavaString(string);
    }
}
