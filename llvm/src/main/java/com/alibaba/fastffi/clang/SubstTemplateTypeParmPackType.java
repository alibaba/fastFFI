package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::SubstTemplateTypeParmPackType")
public interface SubstTemplateTypeParmPackType extends Type {
    static SubstTemplateTypeParmPackType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (SubstTemplateTypeParmPackType) null);
    }
}
