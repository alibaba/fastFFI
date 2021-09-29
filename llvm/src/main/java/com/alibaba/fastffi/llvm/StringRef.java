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
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.StringProvider;
import com.alibaba.fastffi.UnsafeHolder;

@FFIGen
@CXXHead("llvm/ADT/StringRef.h")
@FFITypeAlias("llvm::StringRef")
public interface StringRef extends LLVMPointer, StringProvider {

    @FFIFactory
    interface Factory {
        StringRef create();
        StringRef create(CharPointer buf);
        StringRef create(CharPointer buf, long length);
        StringRef create(@CXXReference StdString stdString);
    }

    long data();
    long size();

    boolean equals(@CXXValue StringRef rhs);

    // Use implicit conversion from StdString to StringRef
    boolean equals(@CXXValue StdString rhs);
}
