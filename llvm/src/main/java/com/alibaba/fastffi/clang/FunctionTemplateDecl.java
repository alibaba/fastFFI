package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::FunctionTemplateDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface FunctionTemplateDecl extends RedeclarableTemplateDecl {
    static FunctionTemplateDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (FunctionTemplateDecl) null);
    }

    FunctionDecl getTemplatedDecl();
    boolean isThisDeclarationADefinition();
}
