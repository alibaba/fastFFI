package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::UsingDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface UsingDecl extends NamedDecl {
    static UsingDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (UsingDecl) null);
    }
}
