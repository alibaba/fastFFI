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
package com.alibaba.fastffi.llvm;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.CXXValueRange;
import com.alibaba.fastffi.CXXValueRangeElement;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@CXXHead("llvm/Object/ObjectFile.h")
@FFITypeAlias("llvm::object::ObjectFile")
public interface ObjectFile extends Binary {

    @FFIGen
    @CXXHead("llvm/Object/ObjectFile.h")
    @FFITypeAlias("llvm::object::SectionRef")
    interface SectionRef extends LLVMPointer {
        @CXXValue
        Expected<StringRef> getName();
        @CXXValue
        Expected<StringRef> getContents();
    }

    @FFIGen
    @CXXHead("llvm/Object/ObjectFile.h")
    @FFITypeAlias("llvm::object::section_iterator")
    interface SectionIterator extends CXXValueRangeElement<SectionIterator> {
        @CXXOperator("*")
        @CXXReference
        SectionRef get();
    }

    @CXXValue
    SectionIterator section_begin();
    @CXXValue
    SectionIterator section_end();

    default CXXValueRange<SectionIterator> sections() {
        return new CXXValueRange<SectionIterator>() {
            @Override
            public SectionIterator begin() {
                return section_begin();
            }

            @Override
            public SectionIterator end() {
                return section_end();
            }
        };
    }
}
