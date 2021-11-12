package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::HeaderSearch")
@CXXHead("clang/Lex/HeaderSearch.h")
public interface HeaderSearch extends FFIPointer {
    @FFIGen
    @FFITypeAlias("clang::HeaderSearch::search_dir_iterator")
    @CXXHead("clang/Lex/HeaderSearch.h")
    interface search_dir_iterator extends FFIPointer {
        @CXXOperator("*")
        @CXXReference DirectoryLookup get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference search_dir_iterator other);
    }

    @CXXValue search_dir_iterator search_dir_begin();
    @CXXValue search_dir_iterator search_dir_end();
    int search_dir_size();
    @CXXValue search_dir_iterator quoted_dir_begin();
    @CXXValue search_dir_iterator quoted_dir_end();
    @CXXValue search_dir_iterator angled_dir_begin();
    @CXXValue search_dir_iterator angled_dir_end();
    @CXXValue search_dir_iterator system_dir_begin();
    @CXXValue search_dir_iterator system_dir_end();
}
