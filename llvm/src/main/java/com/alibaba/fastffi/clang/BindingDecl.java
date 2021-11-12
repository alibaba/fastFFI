package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::BindingDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface BindingDecl extends ValueDecl {
    static BindingDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (BindingDecl) null);
    }
}
