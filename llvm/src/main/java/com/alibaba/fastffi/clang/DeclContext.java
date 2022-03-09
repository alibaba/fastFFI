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
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.LLVMPointer;

@FFIGen
@FFITypeAlias("clang::DeclContext")
@CXXHead("clang/AST/DeclBase.h")
public interface DeclContext extends LLVMPointer {

    static Decl cast(DeclContext context) {
        return DeclCasting.INSTANCE.cast(context, (Decl) null);
    }

    @FFIGen
    @FFITypeAlias("clang::DeclContext::decl_iterator")
    @CXXHead("clang/AST/DeclBase.h")
    interface decl_iterator extends LLVMPointer {
        @CXXOperator("*")
        Decl get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference decl_iterator other);
    }

    Decl.Kind getDeclKind();

    @CXXValue decl_iterator decls_begin();
    @CXXValue decl_iterator decls_end();
    boolean decls_empty();

    DeclContext getParent();
    DeclContext getLexicalParent();
    DeclContext getLookupParent();

    DeclContext getPrimaryContext();
    DeclContext getRedeclContext();

    @CXXReference ASTContext getParentASTContext();
}
