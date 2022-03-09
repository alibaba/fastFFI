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
package com.alibaba.fastffi.clang.tooling;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.LLVMPointer;
import com.alibaba.fastffi.llvm.cl.OptionCategory;
import com.alibaba.fastffi.stdcxx.StdString;
import com.alibaba.fastffi.stdcxx.StdVector;

@FFIGen
@CXXHead("clang/Tooling/CommonOptionsParser.h")
@CXXHead("clang/Tooling/Tooling.h")
@FFITypeAlias("clang::tooling::CommonOptionsParser")
public interface CommonOptionsParser extends CXXPointer, LLVMPointer {
    @FFIFactory
    interface Factory {
        CommonOptionsParser create(int argc, @FFITypeAlias("const char**") long argv,
                                   @CXXReference OptionCategory category);
    }

    @CXXReference CompilationDatabase getCompilations();
    @CXXReference StdVector<StdString> getSourcePathList();

    @FFIExpr("new clang::tooling::ClangTool({0}->getCompilations(), {0}->getSourcePathList())")
    ClangTool createClangTool();
    @FFIExpr("new clang::tooling::ClangTool({0}->getCompilations(), {1})")
    ClangTool createClangTool(@CXXReference StdVector<StdString> sourcePath);
}
