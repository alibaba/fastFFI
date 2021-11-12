package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::ClassTemplateSpecializationDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface ClassTemplateSpecializationDecl extends CXXRecordDecl {
    static ClassTemplateSpecializationDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (ClassTemplateSpecializationDecl) null);
    }
}
