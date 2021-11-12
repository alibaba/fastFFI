package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("clang/Basic/Diagnostic.h")
@FFITypeAlias("clang::DiagnosticsEngine")
public interface DiagnosticsEngine extends FFIPointer {
    boolean hasErrorOccurred();
    boolean hasUncompilableErrorOccurred();
    boolean hasFatalErrorOccurred();
    boolean hasUnrecoverableErrorOccurred();
    int getNumWarnings();
}
