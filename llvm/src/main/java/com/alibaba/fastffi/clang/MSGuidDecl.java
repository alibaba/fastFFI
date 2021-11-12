package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::MSGuidDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface MSGuidDecl extends ValueDecl {
    static MSGuidDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (MSGuidDecl) null);
    }
}
