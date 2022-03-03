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
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;

@FFIGen
@CXXHead("clang/Sema/Template.h")
@FFITypeAlias("clang::MultiLevelTemplateArgumentList")
public interface MultiLevelTemplateArgumentList
        extends CXXPointer {

    Factory FACTORY = FFITypeFactory.getFactory(MultiLevelTemplateArgumentList.class);

    static MultiLevelTemplateArgumentList create(TemplateArgumentList templateArgumentList) {
        return FACTORY.create(templateArgumentList);
    }

    TemplateSubstitutionKind getKind();

    boolean isRewrite();
    int getNumLevels();
    int getNumSubstitutedLevels();
    int getNumRetainedOuterLevels();
    int getNewDepth(int OldDepth);
    boolean hasTemplateArgument(int Depth, int Index);
    void setArgument(int Depth, int Index, @CXXValue TemplateArgument Arg);
    void addOuterTemplateArguments(TemplateArgumentList templateArgs);
    void addOuterRetainedLevel();
    void addOuterRetainedLevels(int Num);

    @FFIFactory
    @CXXHead("clang/Sema/Template.h")
    interface Factory {
        MultiLevelTemplateArgumentList create(@CXXReference TemplateArgumentList TemplateArguments);
    }
}
