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
@FFITypeAlias("clang::Qualifiers")
public interface Qualifiers extends FFIPointer {
    boolean hasConst();
    boolean hasOnlyConst();
    void removeConst();
    void addConst();

    boolean hasVolatile();
    boolean hasOnlyVolatile();
    void removeVolatile();
    void addVolatile();

    boolean hasRestrict();
    boolean hasOnlyRestrict();
    void removeRestrict();
    void addRestrict();

    @CXXValue StdString getAsString();
}
