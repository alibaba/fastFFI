package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::RedeclarableTemplateDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface RedeclarableTemplateDecl extends TemplateDecl {
    static RedeclarableTemplateDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (RedeclarableTemplateDecl) null);
    }

    Decl getPreviousDecl();
    Decl getMostRecentDecl();
    boolean isFirstDecl();
}
