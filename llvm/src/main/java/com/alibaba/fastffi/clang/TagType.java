package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::TagType")
public interface TagType extends Type {
    static TagType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (TagType) null);
    }
}
