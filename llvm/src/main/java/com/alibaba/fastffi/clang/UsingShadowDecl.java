package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::UsingShadowDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface UsingShadowDecl extends NamedDecl {
    static UsingShadowDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (UsingShadowDecl) null);
    }
    Decl getPreviousDecl();
    Decl getMostRecentDecl();
    boolean isFirstDecl();

    NamedDecl getTargetDecl();
    UsingDecl getUsingDecl();
}
