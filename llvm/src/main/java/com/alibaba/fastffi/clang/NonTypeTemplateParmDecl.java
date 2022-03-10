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
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::NonTypeTemplateParmDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface NonTypeTemplateParmDecl extends DeclaratorDecl {
    static NonTypeTemplateParmDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (NonTypeTemplateParmDecl) null);
    }

    @CXXValue SourceRange getSourceRange();
    boolean hasDefaultArgument();
    Expr getDefaultArgument();
    @CXXValue SourceLocation getDefaultArgumentLoc();
    boolean defaultArgumentWasInherited();
    void setDefaultArgument(Expr DefArg);
    void setInheritedDefaultArgument(@CXXReference ASTContext C, NonTypeTemplateParmDecl Parm);
    void removeDefaultArgument();
    boolean isParameterPack();
    boolean isPackExpansion();
    boolean isExpandedParameterPack();
    int getNumExpansionTypes();
    @CXXValue QualType getExpansionType(int I);
    TypeSourceInfo getExpansionTypeSourceInfo(int I);
    Expr getPlaceholderTypeConstraint();
    void setPlaceholderTypeConstraint(Expr E);
    boolean hasPlaceholderTypeConstraint();
    int getDepth();
    int getPosition();
    int getIndex();
}
