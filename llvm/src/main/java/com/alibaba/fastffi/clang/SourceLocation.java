package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/Basic/SourceLocation.h")
@FFITypeAlias("clang::SourceLocation")
public interface SourceLocation extends FFIPointer {
}
