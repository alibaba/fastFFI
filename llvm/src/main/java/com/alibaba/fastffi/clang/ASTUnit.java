package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@CXXHead("clang/Frontend/ASTUnit.h")
@FFITypeAlias("clang::ASTUnit")
public interface ASTUnit extends CXXPointer {

    @FFIGen
    @CXXHead(system = "vector")
    @CXXHead("clang/AST/DeclBase.h")
    @FFITypeAlias("std::vector<clang::Decl*>::iterator")
    interface top_level_iterator extends FFIPointer {
        @CXXOperator("*")
        Decl get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference top_level_iterator other);
    }

    @CXXReference ASTContext getASTContext();

    @CXXValue top_level_iterator top_level_begin();
    @CXXValue top_level_iterator top_level_end();
    long top_level_size();
    boolean top_level_empty();

    @CXXValue StringRef getOriginalSourceFileName();
    @CXXValue StringRef getMainFileName();
    @CXXValue StringRef getASTFileName();
    @CXXReference DiagnosticsEngine getDiagnostics();
    @CXXReference Preprocessor getPreprocessor();
}
