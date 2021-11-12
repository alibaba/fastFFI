package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::DependentSizedMatrixType")
public interface DependentSizedMatrixType extends MatrixType {
    static DependentSizedMatrixType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (DependentSizedMatrixType) null);
    }
}
