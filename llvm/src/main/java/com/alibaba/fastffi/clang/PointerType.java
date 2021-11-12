package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::PointerType")
public interface PointerType extends Type {
    static PointerType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (PointerType) null);
    }

    @CXXValue QualType getPointeeType();
}
