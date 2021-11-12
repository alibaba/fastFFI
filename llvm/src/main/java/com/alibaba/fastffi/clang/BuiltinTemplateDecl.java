package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::BuiltinTemplateDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface BuiltinTemplateDecl extends TemplateDecl {
    static BuiltinTemplateDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (BuiltinTemplateDecl) null);
    }
}
