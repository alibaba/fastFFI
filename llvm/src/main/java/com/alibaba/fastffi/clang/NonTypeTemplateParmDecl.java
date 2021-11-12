package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::NonTypeTemplateParmDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface NonTypeTemplateParmDecl extends DeclaratorDecl {
    static NonTypeTemplateParmDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (NonTypeTemplateParmDecl) null);
    }
}
