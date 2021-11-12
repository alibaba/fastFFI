package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::FriendTemplateDecl")
@CXXHead("clang/AST/DeclTemplate.h")
public interface FriendTemplateDecl extends Decl {
    static FriendTemplateDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (FriendTemplateDecl) null);
    }
}
