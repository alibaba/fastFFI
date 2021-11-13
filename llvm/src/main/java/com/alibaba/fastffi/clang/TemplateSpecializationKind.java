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

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;

@FFITypeAlias("clang::TemplateSpecializationKind")
@FFITypeRefiner("TemplateSpecializationKind.get")
public enum TemplateSpecializationKind implements CXXEnum {
    TSK_Undeclared(Library.INSTANCE.TSK_Undeclared()),
    TSK_ImplicitInstantiation(Library.INSTANCE.TSK_ImplicitInstantiation()),
    TSK_ExplicitSpecialization(Library.INSTANCE.TSK_ExplicitSpecialization()),
    TSK_ExplicitInstantiationDeclaration(Library.INSTANCE.TSK_ExplicitInstantiationDeclaration()),
    TSK_ExplicitInstantiationDefinition(Library.INSTANCE.TSK_ExplicitInstantiationDefinition()),
    ;

    int value;

    @FFIGen
    @FFILibrary(namespace = "clang::TemplateSpecializationKind")
    @CXXHead("clang/Basic/Specifiers.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter int TSK_Undeclared();
        @FFIGetter int TSK_ImplicitInstantiation();
        @FFIGetter int TSK_ExplicitSpecialization();
        @FFIGetter int TSK_ExplicitInstantiationDeclaration();
        @FFIGetter int TSK_ExplicitInstantiationDefinition();
        @FFIExpr("clang::isTemplateInstantiation({1})")
        boolean isTemplateInstantiation(@CXXValue TemplateSpecializationKind kind);
        @FFIExpr("clang::isTemplateExplicitInstantiationOrSpecialization({1})")
        boolean isTemplateExplicitInstantiationOrSpecialization(@CXXValue TemplateSpecializationKind kind);
    }

    TemplateSpecializationKind(int value) {
        this.value = value;
    }

    public boolean isTemplateInstantiation() {
        return Library.INSTANCE.isTemplateInstantiation(this);
    }

    public boolean isTemplateExplicitInstantiationOrSpecialization() {
        return Library.INSTANCE.isTemplateExplicitInstantiationOrSpecialization(this);
    }

    @Override
    public int getValue() {
        return value;
    }

    static CXXEnumMap<TemplateSpecializationKind> map = new CXXEnumMap<>(values());
    public static TemplateSpecializationKind get(int value) {
        return map.get(value);
    }
}
