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
package com.alibaba.llvm4jni.simdjson.stdcxx;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIStringProvider;
import com.alibaba.fastffi.FFIStringReceiver;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;

@FFIGen
@CXXHead(system = "string")
@FFITypeAlias("std::string")
public interface StdString extends CXXPointer, FFIStringReceiver, FFIStringProvider {

    static StdString create(String from) {
        return factory.create(from);
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
    }

    long size();
    long data();
    long c_str();
    void resize(long size);

    int compare(@CXXReference StdString str);
}
