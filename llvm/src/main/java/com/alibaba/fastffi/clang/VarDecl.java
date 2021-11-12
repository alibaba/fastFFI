package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::VarDecl")
@CXXHead("clang/AST/Decl.h")
public interface VarDecl extends DeclaratorDecl {
    static VarDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (VarDecl) null);
    }

    Decl getPreviousDecl();
    Decl getMostRecentDecl();
    boolean isFirstDecl();
    boolean isThisDeclarationADefinition();
}
