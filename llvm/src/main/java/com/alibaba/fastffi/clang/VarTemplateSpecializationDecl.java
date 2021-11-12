package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::VarTemplateSpecializationDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface VarTemplateSpecializationDecl extends VarDecl {
    static VarTemplateSpecializationDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (VarTemplateSpecializationDecl) null);
    }
}
