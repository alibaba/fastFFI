package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::TypeDecl")
@CXXHead("clang/AST/Decl.h")
public interface TypeDecl extends NamedDecl {
    static TypeDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (TypeDecl) null);
    }

    Type getTypeForDecl();
}
