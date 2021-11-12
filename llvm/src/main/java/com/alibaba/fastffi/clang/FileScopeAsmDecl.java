package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::FileScopeAsmDecl")
@CXXHead("clang/AST/Decl.h")
public interface FileScopeAsmDecl extends Decl {
    static FileScopeAsmDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (FileScopeAsmDecl) null);
    }
}
