package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::ClassScopeFunctionSpecializationDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface ClassScopeFunctionSpecializationDecl extends Decl {
    static ClassScopeFunctionSpecializationDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (ClassScopeFunctionSpecializationDecl) null);
    }
}
