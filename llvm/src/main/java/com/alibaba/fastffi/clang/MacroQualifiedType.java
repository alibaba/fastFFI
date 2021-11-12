package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::MacroQualifiedType")
public interface MacroQualifiedType extends Type {
    static MacroQualifiedType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (MacroQualifiedType) null);
    }
}
