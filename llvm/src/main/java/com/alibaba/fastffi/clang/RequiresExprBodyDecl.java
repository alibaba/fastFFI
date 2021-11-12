package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::RequiresExprBodyDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface RequiresExprBodyDecl extends Decl, DeclContext {
    static RequiresExprBodyDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (RequiresExprBodyDecl) null);
    }
}
