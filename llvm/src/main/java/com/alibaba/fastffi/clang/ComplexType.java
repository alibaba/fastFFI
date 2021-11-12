package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::ComplexType")
public interface ComplexType extends Type {
    static ComplexType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (ComplexType) null);
    }
}
