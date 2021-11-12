package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@FFITypeAlias("clang::UsingDirectiveDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface UsingDirectiveDecl extends NamedDecl {
    static UsingDirectiveDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (UsingDirectiveDecl) null);
    }
    @FFIExpr("((clang::NamedDecl*){0})->getName()")
    @CXXValue StringRef getName();
}
