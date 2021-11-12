package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::DependentNameType")
public interface DependentNameType extends TypeWithKeyword {
    static DependentNameType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (DependentNameType) null);
    }
    NestedNameSpecifier getQualifier();
    IdentifierInfo getIdentifier();
}
