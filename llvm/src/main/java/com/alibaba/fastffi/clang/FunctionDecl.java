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
@FFITypeAlias("clang::FunctionDecl")
@CXXHead("clang/AST/Decl.h")
public interface FunctionDecl extends DeclaratorDecl, DeclContext {
    static FunctionDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (FunctionDecl) null);
    }

    Decl getPreviousDecl();
    Decl getMostRecentDecl();
    boolean isFirstDecl();
    FunctionDecl getDefinition();

    FunctionTemplateDecl getDescribedFunctionTemplate();

    boolean hasBody();
    boolean isDefined();
    boolean isVariadic();
    boolean isTrivial();
    boolean isDefaulted();
    boolean isExplicitlyDefaulted();
    boolean isUserProvided();
    boolean isDeleted();
    boolean isExternC();
    boolean isThisDeclarationADefinition();

    boolean isPure();

    boolean isOverloadedOperator();
    @CXXValue OverloadedOperatorKind getOverloadedOperator();

    @FFIGen
    @FFITypeAlias("clang::FunctionDecl::param_iterator")
    @CXXHead("clang/AST/Decl.h")
    interface param_iterator extends FFIPointer {
        @CXXOperator("*")
        ParmVarDecl get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference param_iterator other);
    }

    @CXXValue param_iterator param_begin();
    @CXXValue param_iterator param_end();
    long param_size();

    @CXXValue QualType getReturnType();
    int getNumParams();
    ParmVarDecl getParamDecl(int idx);

    int getMinRequiredArguments();
}
