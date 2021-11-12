package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::FriendDecl")
@CXXHead("clang/AST/DeclFriend.h")
public interface FriendDecl extends Decl {
    static FriendDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (FriendDecl) null);
    }
}
