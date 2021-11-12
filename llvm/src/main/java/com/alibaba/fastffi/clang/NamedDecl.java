package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;
import com.alibaba.fastffi.stdcxx.StdString;

@FFIGen
@FFITypeAlias("clang::NamedDecl")
@CXXHead("clang/AST/Decl.h")
public interface NamedDecl extends Decl {
    @CXXValue StringRef getName();
    @CXXValue StdString getNameAsString();
    IdentifierInfo getIdentifier();
    @CXXValue DeclarationName getDeclName();
}
