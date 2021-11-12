package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@CXXHead("clang/Basic/SourceManager.h")
@FFITypeAlias("clang::SourceManager")
public interface SourceManager extends FFIPointer {
    boolean isInMainFile(@CXXValue SourceLocation sourceLocation);
    @CXXValue StringRef getFilename(@CXXValue SourceLocation location);
    int getSpellingColumnNumber(@CXXValue SourceLocation Loc);
    int getExpansionColumnNumber(@CXXValue SourceLocation Loc);
    int getPresumedColumnNumber(@CXXValue SourceLocation Loc);
    int getSpellingLineNumber(@CXXValue SourceLocation Loc);
    int getExpansionLineNumber(@CXXValue SourceLocation Loc);
    int getPresumedLineNumber(@CXXValue SourceLocation Loc);

}
