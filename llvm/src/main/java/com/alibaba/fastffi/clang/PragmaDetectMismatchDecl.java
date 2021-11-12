package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::PragmaDetectMismatchDecl")
@CXXHead("clang/AST/Decl.h")
public interface PragmaDetectMismatchDecl extends Decl {
    static PragmaDetectMismatchDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (PragmaDetectMismatchDecl) null);
    }
}
