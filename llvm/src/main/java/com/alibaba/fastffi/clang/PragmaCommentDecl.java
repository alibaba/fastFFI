package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@FFITypeAlias("clang::PragmaCommentDecl")
@CXXHead("clang/AST/Decl.h")
public interface PragmaCommentDecl extends Decl {
    static PragmaCommentDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (PragmaCommentDecl) null);
    }
}
