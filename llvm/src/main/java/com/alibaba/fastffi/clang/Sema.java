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
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.SmallVectorImpl;

@FFIGen
@CXXHead("clang/Sema/Sema.h")
@FFITypeAlias("clang::Sema")
public interface Sema extends CXXPointer {
    @CXXValue QualType SubstAutoType(@CXXValue QualType TypeWithAuto, @CXXValue QualType Replacement);
    @CXXValue QualType ReplaceAutoType(@CXXValue QualType TypeWithAuto, @CXXValue QualType Replacement);

    boolean inTemplateInstantiation();

    @CXXValue QualType SubstType(
            @CXXValue QualType T,
            @CXXReference  MultiLevelTemplateArgumentList TemplateArgs,
            @CXXValue SourceLocation Loc,
            @CXXValue DeclarationName Entity);
    Decl SubstDecl(Decl D, DeclContext Owner, @CXXReference MultiLevelTemplateArgumentList TemplateArgs);
    FunctionDecl SubstSpaceshipAsEqualEqual(CXXRecordDecl RD, FunctionDecl Spaceship);
}
