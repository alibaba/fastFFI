package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::TemplateTypeParmDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface TemplateTypeParmDecl extends TypeDecl {
    static TemplateTypeParmDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (TemplateTypeParmDecl) null);
    }
    boolean hasDefaultArgument();
}
