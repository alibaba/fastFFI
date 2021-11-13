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
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::RecordDecl")
@CXXHead("clang/AST/Decl.h")
public interface RecordDecl extends TagDecl {
    static RecordDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (RecordDecl) null);
    }

    @FFIGen
    @FFITypeAlias("clang::RecordDecl::field_iterator")
    @CXXHead("clang/AST/Decl.h")
    interface field_iterator extends FFIPointer {
        @CXXOperator("*")
        FieldDecl get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference field_iterator other);
    }

    @CXXValue field_iterator field_begin();
    @CXXValue field_iterator field_end();
    boolean field_empty();

    boolean isAnonymousStructOrUnion();
    boolean hasObjectMember();
    boolean isInjectedClassName();
    RecordDecl getDefinition();
}
