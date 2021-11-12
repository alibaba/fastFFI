package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.LLVMPointer;
import com.alibaba.fastffi.llvm.StringRef;
import com.alibaba.fastffi.stdcxx.StdString;

@FFIGen
@CXXHead("clang/AST/DeclarationName.h")
@FFITypeAlias("clang::DeclarationName")
public interface DeclarationName extends LLVMPointer {
    @CXXValue StdString getAsString();
}
