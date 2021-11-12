package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.stdcxx.StdString;

import javax.annotation.Nonnull;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::QualType")
public interface QualType extends FFIPointer {
    @Nonnull Type getTypePtr();
    Type getTypePtrOrNull();
    boolean isCanonical();
    boolean isNull();
    boolean isConstQualified();
    boolean isLocalConstQualified();
    @CXXValue StdString getAsString();
    IdentifierInfo getBaseTypeIdentifier();
}
