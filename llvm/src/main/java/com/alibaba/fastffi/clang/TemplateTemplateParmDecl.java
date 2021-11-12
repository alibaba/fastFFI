package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::TemplateTemplateParmDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface TemplateTemplateParmDecl extends TemplateDecl {
    static TemplateTemplateParmDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (TemplateTemplateParmDecl) null);
    }
}
