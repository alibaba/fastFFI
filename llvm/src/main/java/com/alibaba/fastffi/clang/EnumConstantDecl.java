package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::EnumConstantDecl")
@CXXHead("clang/AST/Decl.h")
public interface EnumConstantDecl extends ValueDecl {
    static EnumConstantDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (EnumConstantDecl) null);
    }
}
