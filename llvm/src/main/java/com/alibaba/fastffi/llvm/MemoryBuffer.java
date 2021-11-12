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
import com.alibaba.fastffi.CXXStackObject;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.stdcxx.StdString;
import com.alibaba.fastffi.stdcxx.UniquePtr;

@FFIGen
@CXXHead("llvm/Support/MemoryBuffer.h")
@FFITypeAlias("llvm::MemoryBuffer")
public interface MemoryBuffer extends LLVMPointer {

    static UniquePtr<MemoryBuffer> getMemBuffer(StringRef data) {
        return Library.INSTANCE.getMemBuffer(data);
    }

    static ErrorOr<UniquePtr<MemoryBuffer>> getFile(String data) {
        try (CXXStackObject<StdString> fileName = new CXXStackObject<StdString>(StdString.factory.create(data))) {
            return Library.INSTANCE.getFile(fileName.get());
        }
    }

    @FFIGen
    @CXXHead("llvm/Support/MemoryBuffer.h")
    @FFILibrary(value = "llvm::MemoryBuffer", namespace = "llvm::MemoryBuffer")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);

        @CXXValue
        UniquePtr<MemoryBuffer> getMemBuffer(@CXXValue StringRef data);

        @CXXValue
        ErrorOr<UniquePtr<MemoryBuffer>> getFile(@CXXReference StdString fileName);

    }

    @CXXValue
    MemoryBufferRef getMemBufferRef();
}
