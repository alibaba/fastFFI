package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::VarTemplateDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface VarTemplateDecl extends RedeclarableTemplateDecl {
    static VarTemplateDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (VarTemplateDecl) null);
    }

    VarTemplateDecl getDefinition();
    VarDecl getTemplatedDecl();
    boolean isThisDeclarationADefinition();
}
