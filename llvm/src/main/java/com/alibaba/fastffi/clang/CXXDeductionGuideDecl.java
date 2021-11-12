package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::CXXDeductionGuideDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface CXXDeductionGuideDecl extends FunctionDecl {
    static CXXDeductionGuideDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (CXXDeductionGuideDecl) null);
    }
}
