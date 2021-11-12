package com.alibaba.fastffi.clang.tooling;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/Tooling/CompilationDatabase.h")
@FFITypeAlias("clang::tooling::CompilationDatabase")
public interface CompilationDatabase extends CXXPointer {
}
