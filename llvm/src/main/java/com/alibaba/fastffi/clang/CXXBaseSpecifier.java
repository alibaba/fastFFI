package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::CXXBaseSpecifier")
@CXXHead("clang/AST/DeclCXX.h")
public interface CXXBaseSpecifier extends FFIPointer {

    boolean isVirtual();
    @CXXValue QualType getType();
}
