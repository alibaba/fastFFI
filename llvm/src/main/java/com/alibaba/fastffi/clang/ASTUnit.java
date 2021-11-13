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
package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@CXXHead("clang/Frontend/ASTUnit.h")
@FFITypeAlias("clang::ASTUnit")
public interface ASTUnit extends CXXPointer {

    @FFIGen
    @CXXHead(system = "vector")
    @CXXHead("clang/AST/DeclBase.h")
    @FFITypeAlias("std::vector<clang::Decl*>::iterator")
    interface top_level_iterator extends FFIPointer {
        @CXXOperator("*")
        Decl get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference top_level_iterator other);
    }

    @CXXReference ASTContext getASTContext();

    @CXXValue top_level_iterator top_level_begin();
    @CXXValue top_level_iterator top_level_end();
    long top_level_size();
    boolean top_level_empty();

    @CXXValue StringRef getOriginalSourceFileName();
    @CXXValue StringRef getMainFileName();
    @CXXValue StringRef getASTFileName();
    @CXXReference DiagnosticsEngine getDiagnostics();
    @CXXReference Preprocessor getPreprocessor();
}
