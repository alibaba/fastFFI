package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::DecayedType")
public interface DecayedType extends AdjustedType {
    static DecayedType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (DecayedType) null);
    }
}
