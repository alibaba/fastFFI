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
package com.alibaba.fastffi.llvm;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.StringProvider;
import com.alibaba.fastffi.StringReceiver;
import com.alibaba.fastffi.UnsafeHolder;

@FFIGen
@CXXHead(system = "string")
@FFITypeAlias("std::string")
public interface StdString extends LLVMPointer, CXXPointer, StringReceiver, StringProvider {

    static StdString create(String string) {
        return factory.create(string);
    }

    Factory factory = FFITypeFactory.getFactory(StdString.class);

    @FFIFactory
    interface Factory {
        StdString create();
        default StdString create(String string) {
            StdString std = create();
            std.fromJavaString(string);
            return std;
        }
        StdString create(@CXXReference StdString string);
    }

    long size();

    long data();

    void resize(long size);
}
