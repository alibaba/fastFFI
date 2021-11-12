package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::FieldDecl")
@CXXHead("clang/AST/Decl.h")
public interface FieldDecl extends DeclaratorDecl {
    static FieldDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (FieldDecl) null);
    }
}
