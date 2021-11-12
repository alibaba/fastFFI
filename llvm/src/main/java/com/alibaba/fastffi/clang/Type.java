package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeRefiner;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::Type")
@FFITypeRefiner("com.alibaba.fastffi.clang.TypeRefiner.refine")
public interface Type extends FFIPointer {
    TypeClass getTypeClass();
    boolean isFromAST();
    @CXXValue QualType getCanonicalTypeInternal();
}
