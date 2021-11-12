package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::EnumType")
public interface EnumType extends TagType {
    static EnumType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (EnumType) null);
    }
}
