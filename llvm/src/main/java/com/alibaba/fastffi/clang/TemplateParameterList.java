package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.LLVMPointer;

@FFIGen
@FFITypeAlias("clang::TemplateParameterList")
@CXXHead("clang/AST/DeclTemplate.h")
public interface TemplateParameterList extends LLVMPointer {
    int size();
    NamedDecl getParam(int idx);
    int getMinRequiredArguments();
}
