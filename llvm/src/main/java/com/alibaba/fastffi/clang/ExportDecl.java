package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::ExportDecl")
@CXXHead("clang/AST/Decl.h")
public interface ExportDecl extends Decl, DeclContext {
    static ExportDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (ExportDecl) null);
    }
}
