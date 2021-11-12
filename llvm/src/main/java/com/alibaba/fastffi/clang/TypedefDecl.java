package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::TypedefDecl")
@CXXHead("clang/AST/Decl.h")
public interface TypedefDecl extends TypedefNameDecl {
    static TypedefDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (TypedefDecl) null);
    }
}
