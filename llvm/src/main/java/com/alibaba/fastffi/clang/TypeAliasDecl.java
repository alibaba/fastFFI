package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::TypeAliasDecl")
@CXXHead("clang/AST/Decl.h")
public interface TypeAliasDecl extends TypedefNameDecl {
    static TypeAliasDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (TypeAliasDecl) null);
    }
}
