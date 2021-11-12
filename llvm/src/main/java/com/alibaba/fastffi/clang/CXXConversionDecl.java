package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::CXXConversionDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface CXXConversionDecl extends CXXMethodDecl {
    static CXXConversionDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (CXXConversionDecl) null);
    }
}
