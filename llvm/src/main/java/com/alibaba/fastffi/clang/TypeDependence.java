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
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;

@FFITypeAlias("clang::TypeDependenceScope::TypeDependence")
@FFITypeRefiner("TypeDependence.get")
@CXXHead("clang/AST/DependenceFlags.h")
public enum TypeDependence implements CXXEnum {
    UnexpandedPack(Library.INSTANCE.UnexpandedPack()),
    Instantiation(Library.INSTANCE.Instantiation()),
    Dependent(Library.INSTANCE.Dependent()),
    VariablyModified(Library.INSTANCE.VariablyModified()),
    Error(Library.INSTANCE.Error()),
    None(Library.INSTANCE.None()),
    All(Library.INSTANCE.All()),
    DependentInstantiation(Library.INSTANCE.DependentInstantiation())
    ;

    @FFIGen
    @FFILibrary(value = "clang::TypeDependenceScope::TypeDependence", namespace = "clang::TypeDependenceScope::TypeDependence")
    @CXXHead("clang/AST/DependenceFlags.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter int UnexpandedPack();
        @FFIGetter int Instantiation();
        @FFIGetter int Dependent();
        @FFIGetter int VariablyModified();
        @FFIGetter int Error();
        @FFIGetter int None();
        @FFIGetter int All();
        @FFIGetter int DependentInstantiation();
    }

    int value;
    TypeDependence(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    static final CXXEnumMap<TypeDependence> map = new CXXEnumMap<>(values());
    public static TypeDependence get(int value) {
        return map.get(value);
    }
}
