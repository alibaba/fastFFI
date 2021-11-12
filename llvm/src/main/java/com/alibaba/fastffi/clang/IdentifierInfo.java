package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.LLVMPointer;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@CXXHead("clang/Basic/IdentifierTable.h")
@FFITypeAlias("clang::IdentifierInfo")
public interface IdentifierInfo extends LLVMPointer {
    @CXXValue StringRef getName();
}
