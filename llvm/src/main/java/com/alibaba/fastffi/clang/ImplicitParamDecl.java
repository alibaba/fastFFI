package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::ImplicitParamDecl")
@CXXHead("clang/AST/Decl.h")
public interface ImplicitParamDecl extends VarDecl {
    static ImplicitParamDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (ImplicitParamDecl) null);
    }
}
