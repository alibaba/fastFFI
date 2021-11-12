package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::ClassTemplateDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface ClassTemplateDecl extends RedeclarableTemplateDecl {
    static ClassTemplateDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (ClassTemplateDecl) null);
    }

    CXXRecordDecl getTemplatedDecl();
}
