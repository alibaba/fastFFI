package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.GlobalAlias;
import com.alibaba.fastffi.llvm.Value;
import com.alibaba.fastffi.llvm.ValueCasting;

@FFIGen
@FFITypeAlias("clang::AccessSpecDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface AccessSpecDecl extends Decl {
    static AccessSpecDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (AccessSpecDecl) null);
    }
}
