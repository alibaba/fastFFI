package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::DependentSizedExtVectorType")
public interface DependentSizedExtVectorType extends Type {
    static DependentSizedExtVectorType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (DependentSizedExtVectorType) null);
    }
}
