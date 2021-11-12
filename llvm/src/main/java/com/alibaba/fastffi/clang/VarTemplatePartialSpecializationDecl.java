package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::VarTemplatePartialSpecializationDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface VarTemplatePartialSpecializationDecl extends VarTemplateSpecializationDecl {
    static VarTemplatePartialSpecializationDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (VarTemplatePartialSpecializationDecl) null);
    }
}
