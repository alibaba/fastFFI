package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::TemplateDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface TemplateDecl extends NamedDecl {
    static TemplateDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (TemplateDecl) null);
    }

    TemplateParameterList getTemplateParameters();
}
