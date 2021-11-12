package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::ParmVarDecl")
@CXXHead("clang/AST/Decl.h")
public interface ParmVarDecl extends VarDecl {
    static ParmVarDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (ParmVarDecl) null);
    }
}
