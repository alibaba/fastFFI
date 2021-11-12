package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::TypeAliasTemplateDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface TypeAliasTemplateDecl extends RedeclarableTemplateDecl {
    static TypeAliasTemplateDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (TypeAliasTemplateDecl) null);
    }
}
