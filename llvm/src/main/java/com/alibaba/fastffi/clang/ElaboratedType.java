package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::ElaboratedType")
public interface ElaboratedType extends TypeWithKeyword {
    static ElaboratedType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (ElaboratedType) null);
    }

    NestedNameSpecifier getQualifier();
    @CXXValue QualType getNamedType();
    @CXXValue QualType desugar();
    TagDecl getOwnedTagDecl();
}
