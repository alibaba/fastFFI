package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::StaticAssertDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface StaticAssertDecl extends Decl {
    static StaticAssertDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (StaticAssertDecl) null);
    }
}
