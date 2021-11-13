/*
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
