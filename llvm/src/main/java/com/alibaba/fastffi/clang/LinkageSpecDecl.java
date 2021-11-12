package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::LinkageSpecDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface LinkageSpecDecl extends Decl, DeclContext {
    static LinkageSpecDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (LinkageSpecDecl) null);
    }
}
