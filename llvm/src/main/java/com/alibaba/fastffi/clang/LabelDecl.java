package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@FFITypeAlias("clang::LabelDecl")
@CXXHead("clang/AST/Decl.h")
public interface LabelDecl extends NamedDecl {
    static LabelDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (LabelDecl) null);
    }
}
