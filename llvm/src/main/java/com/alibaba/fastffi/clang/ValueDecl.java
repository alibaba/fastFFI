package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@FFITypeAlias("clang::ValueDecl")
@CXXHead("clang/AST/Decl.h")
public interface ValueDecl extends NamedDecl {
    static ValueDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (ValueDecl) null);
    }
}
