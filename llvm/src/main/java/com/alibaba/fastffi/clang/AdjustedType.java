package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

import javax.annotation.Nonnull;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::AdjustedType")
public interface AdjustedType extends Type {
    static AdjustedType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (AdjustedType) null);
    }
}
