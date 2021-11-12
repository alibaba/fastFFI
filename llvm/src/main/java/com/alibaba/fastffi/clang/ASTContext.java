package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/AST/ASTContext.h")
@FFITypeAlias("clang::ASTContext")
public interface ASTContext extends CXXPointer {
    TranslationUnitDecl getTranslationUnitDecl();
    @CXXReference SourceManager getSourceManager();
    @CXXReference PrintingPolicy getPrintingPolicy();
}
