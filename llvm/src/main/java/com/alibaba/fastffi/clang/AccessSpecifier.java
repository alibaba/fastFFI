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

@FFITypeAlias("clang::AccessSpecifier")
@FFITypeRefiner("AccessSpecifier.get")
public enum AccessSpecifier implements CXXEnum {
    AS_public(Library.INSTANCE.AS_public()),
    AS_protected(Library.INSTANCE.AS_protected()),
    AS_private(Library.INSTANCE.AS_private()),
    AS_none(Library.INSTANCE.AS_none())
    ;

    int value;

    @FFIGen
    @FFILibrary(namespace = "clang::AccessSpecifier")
    @CXXHead("clang/Basic/Specifiers.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter int AS_public();
        @FFIGetter int AS_protected();
        @FFIGetter int AS_private();
        @FFIGetter int AS_none();
    }

    public final boolean isPublicOrNone() {
        return this == AS_public || this == AS_none;
    }

    AccessSpecifier(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    static CXXEnumMap<AccessSpecifier> map = new CXXEnumMap<>(values());
    public static AccessSpecifier get(int value) {
        return map.get(value);
    }
}
