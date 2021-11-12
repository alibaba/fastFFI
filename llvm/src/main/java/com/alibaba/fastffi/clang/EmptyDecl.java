package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::EmptyDecl")
@CXXHead("clang/AST/Decl.h")
public interface EmptyDecl extends Decl {
    static EmptyDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (EmptyDecl) null);
    }
}
