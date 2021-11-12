package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::CapturedDecl")
@CXXHead("clang/AST/Decl.h")
public interface CapturedDecl extends Decl, DeclContext {
    static CapturedDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (CapturedDecl) null);
    }
}
