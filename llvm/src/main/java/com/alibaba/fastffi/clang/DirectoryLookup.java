package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@FFITypeAlias("clang::DirectoryLookup")
@CXXHead("clang/Lex/DirectoryLookup.h")
public interface DirectoryLookup extends FFIPointer {

    @CXXValue StringRef getName();

    boolean isNormalDir();
    boolean isFramework();
    boolean isSystemHeaderDirectory();
}
