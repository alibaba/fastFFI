package com.alibaba.fastffi.llvm.cl;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@CXXHead("llvm/Support/CommandLine.h")
@FFITypeAlias("llvm::cl::OptionCategory")
public interface OptionCategory extends CXXPointer {
    @FFIFactory
    interface Factory {
        OptionCategory create(@CXXValue StringRef name, @CXXValue StringRef description);
    }

    @CXXValue StringRef getName();
    @CXXValue StringRef getDescription();
}

