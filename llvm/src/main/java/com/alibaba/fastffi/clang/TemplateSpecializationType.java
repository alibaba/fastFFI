package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/Type.h")
@CXXHead("clang/AST/TemplateBase.h")
@FFITypeAlias("clang::TemplateSpecializationType")
public interface TemplateSpecializationType extends Type {
    static TemplateSpecializationType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (TemplateSpecializationType) null);
    }

    int getNumArgs();
    @CXXReference TemplateArgument getArg(int idx);
    @CXXValue TemplateName getTemplateName();
}
