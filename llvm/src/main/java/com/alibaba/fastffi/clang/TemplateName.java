package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::TemplateName")
@CXXHead("clang/AST/TemplateName.h")
public interface TemplateName extends FFIPointer {
    TemplateDecl getAsTemplateDecl();
}
