package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::NamespaceAliasDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface NamespaceAliasDecl extends NamedDecl {
    static NamespaceAliasDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (NamespaceAliasDecl) null);
    }

    NestedNameSpecifier getQualifier();
    NamespaceDecl getNamespace();
    NamedDecl getAliasedNamespace();

    Decl getPreviousDecl();
    Decl getMostRecentDecl();
}
