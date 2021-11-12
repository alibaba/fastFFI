package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::UnresolvedUsingValueDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface UnresolvedUsingValueDecl extends ValueDecl {
    static UnresolvedUsingValueDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (UnresolvedUsingValueDecl) null);
    }
}
