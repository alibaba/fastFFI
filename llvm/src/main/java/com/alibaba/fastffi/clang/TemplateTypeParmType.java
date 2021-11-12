package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::TemplateTypeParmType")
public interface TemplateTypeParmType extends Type {
    static TemplateTypeParmType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (TemplateTypeParmType) null);
    }
    TemplateTypeParmDecl getDecl();
    int getDepth();
    int getIndex();
    IdentifierInfo getIdentifier();
}
