package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@FFITypeAlias("clang::NamespaceDecl")
@CXXHead("clang/AST/Decl.h")
public interface NamespaceDecl extends NamedDecl, DeclContext {
    static NamespaceDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (NamespaceDecl) null);
    }
    boolean isAnonymousNamespace();

    Decl getPreviousDecl();
    Decl getMostRecentDecl();
    boolean isFirstDecl();
}
