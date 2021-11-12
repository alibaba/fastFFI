package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::ClassTemplatePartialSpecializationDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface ClassTemplatePartialSpecializationDecl extends ClassTemplateSpecializationDecl {
    static ClassTemplatePartialSpecializationDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (ClassTemplatePartialSpecializationDecl) null);
    }
}
