package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::ConceptDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface ConceptDecl extends TemplateDecl {
    static ConceptDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (ConceptDecl) null);
    }
}
