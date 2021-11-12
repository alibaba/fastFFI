package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::DeclaratorDecl")
@CXXHead("clang/AST/Decl.h")
public interface DeclaratorDecl extends ValueDecl {
    static DeclaratorDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (DeclaratorDecl) null);
    }

    TypeSourceInfo getTypeSourceInfo();
    NestedNameSpecifier getQualifier();
}
