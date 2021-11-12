package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::BlockDecl")
@CXXHead("clang/AST/Decl.h")
public interface BlockDecl extends Decl, DeclContext {
    static BlockDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (BlockDecl) null);
    }
}
