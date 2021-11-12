package com.alibaba.fastffi.clang.tooling;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.clang.ASTUnit;
import com.alibaba.fastffi.stdcxx.StdVectorLite;
import com.alibaba.fastffi.stdcxx.UniquePtr;

@FFIGen
@CXXHead("clang/Tooling/Tooling.h")
@FFITypeAlias("clang::tooling::ClangTool")
public interface ClangTool extends CXXPointer {
    int buildASTs(@CXXReference StdVectorLite<UniquePtr<ASTUnit>> ASTs);
}
