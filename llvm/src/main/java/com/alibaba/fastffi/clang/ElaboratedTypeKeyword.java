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

@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::ElaboratedTypeKeyword")
@FFITypeRefiner("ElaboratedTypeKeyword.get")
public enum ElaboratedTypeKeyword implements CXXEnum {
    ETK_Struct(Library.INSTANCE.ETK_Struct()),
    ETK_Interface(Library.INSTANCE.ETK_Interface()),
    ETK_Union(Library.INSTANCE.ETK_Union()),
    ETK_Class(Library.INSTANCE.ETK_Class()),
    ETK_Enum(Library.INSTANCE.ETK_Enum()),
    ETK_Typename(Library.INSTANCE.ETK_Typename()),
    ETK_None(Library.INSTANCE.ETK_None())
    ;

    @FFIGen
    @FFILibrary(value = "clang::ElaboratedTypeKeyword", namespace = "clang::ElaboratedTypeKeyword")
    @CXXHead("clang/AST/Type.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter int ETK_Struct();
        @FFIGetter int ETK_Interface();
        @FFIGetter int ETK_Union();
        @FFIGetter int ETK_Class();
        @FFIGetter int ETK_Enum();
        @FFIGetter int ETK_Typename();
        @FFIGetter int ETK_None();
    }

    int value;
    ElaboratedTypeKeyword(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    static final CXXEnumMap<ElaboratedTypeKeyword> map = new CXXEnumMap<>(values());
    public static ElaboratedTypeKeyword get(int value) {
        return map.get(value);
    }
}
