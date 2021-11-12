package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::TypedefNameDecl")
@CXXHead("clang/AST/Decl.h")
public interface TypedefNameDecl extends TypeDecl {
    static TypedefNameDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (TypedefNameDecl) null);
    }

    TypeSourceInfo getTypeSourceInfo();
    @CXXValue QualType getUnderlyingType();
    TypedefNameDecl getCanonicalDecl();

    Decl getPreviousDecl();
    Decl getMostRecentDecl();
    boolean isFirstDecl();
}
