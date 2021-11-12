package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::CXXConstructorDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface CXXConstructorDecl extends CXXMethodDecl {
    static CXXConstructorDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (CXXConstructorDecl) null);
    }
}
