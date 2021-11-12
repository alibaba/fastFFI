package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::UsingPackDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface UsingPackDecl extends NamedDecl {
    static UsingPackDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (UsingPackDecl) null);
    }
}
