package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::UnresolvedUsingTypenameDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface UnresolvedUsingTypenameDecl extends TypeDecl {
    static UnresolvedUsingTypenameDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (UnresolvedUsingTypenameDecl) null);
    }
}
