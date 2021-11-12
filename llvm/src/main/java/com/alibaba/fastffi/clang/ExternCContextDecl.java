package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::ExternCContextDecl")
@CXXHead("clang/AST/Decl.h")
public interface ExternCContextDecl extends Decl, DeclContext {
    static ExternCContextDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (ExternCContextDecl) null);
    }
}
