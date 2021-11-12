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
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("llvm/Support/Error.h")
@CXXHead("llvm/Object/Binary.h")
@CXXHead("llvm/IR/Module.h")
@CXXHead("clang/Tooling/CommonOptionsParser.h")
@FFITypeAlias("llvm::Expected")
@CXXTemplate(
        cxx = "llvm::object::OwningBinary<llvm::object::Binary>",
        java = "OwningBinary<Binary>"
)
@CXXTemplate(
        cxx = "llvm::StringRef",
        java = "StringRef"
)
@CXXTemplate(
        cxx = "std::unique_ptr<llvm::Module>",
        java = "com.alibaba.fastffi.stdcxx.UniquePtr<Module>"
)
@CXXTemplate(
        cxx = "std::unique_ptr<llvm::MemoryBuffer>",
        java = "com.alibaba.fastffi.stdcxx.UniquePtr<MemoryBuffer>"
)
@CXXTemplate(
        cxx = "clang::tooling::CommonOptionsParser",
        java = "com.alibaba.fastffi.clang.tooling.CommonOptionsParser"
)
public interface Expected<T> extends LLVMPointer {
    @CXXReference
    T get();

    @CXXOperator("*&")
    boolean bool();
}
