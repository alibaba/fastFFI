package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::IndirectFieldDecl")
@CXXHead("clang/AST/Decl.h")
public interface IndirectFieldDecl extends ValueDecl {
    static IndirectFieldDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (IndirectFieldDecl) null);
    }
}
