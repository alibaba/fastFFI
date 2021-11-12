package com.alibaba.fastffi.clang.tooling;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.llvm.cl.OptionCategory;
import com.alibaba.fastffi.stdcxx.StdString;
import com.alibaba.fastffi.stdcxx.StdVector;

@FFIGen
@CXXHead("clang/Tooling/CommonOptionsParser.h")
@CXXHead("clang/Tooling/Tooling.h")
@FFITypeAlias("clang::tooling::CommonOptionsParser")
public interface CommonOptionsParser extends CXXPointer {
    @FFIFactory
    interface Factory {
        CommonOptionsParser create(int argc, @FFITypeAlias("const char**") long argv,
                                   @CXXReference OptionCategory category);
    }

    @CXXReference CompilationDatabase getCompilations();
    @CXXReference StdVector<StdString> getSourcePathList();

    @FFIExpr("new clang::tooling::ClangTool({0}->getCompilations(), {0}->getSourcePathList())")
    ClangTool createClangTool();
    @FFIExpr("new clang::tooling::ClangTool({0}->getCompilations(), {1})")
    ClangTool createClangTool(@CXXReference StdVector<StdString> sourcePath);
}
