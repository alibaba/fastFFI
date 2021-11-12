package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::Preprocessor")
@CXXHead("clang/Lex/Preprocessor.h")
public interface Preprocessor extends FFIPointer {
    @CXXReference HeaderSearch getHeaderSearchInfo();
}
