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

@FFITypeAlias("clang::RefQualifierKind")
@FFITypeRefiner("RefQualifierKind.get")
public enum RefQualifierKind implements CXXEnum {
    None(Library.INSTANCE.RQ_None()),
    LValue(Library.INSTANCE.RQ_LValue()),
    RValue(Library.INSTANCE.RQ_RValue())
    ;

    @FFIGen
    @FFILibrary(value = "clang::RefQualifierKind", namespace = "clang::RefQualifierKind")
    @CXXHead("clang/AST/Type.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter int RQ_None();
        @FFIGetter int RQ_LValue();
        @FFIGetter int RQ_RValue();
    }

    int value;
    RefQualifierKind(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    static final CXXEnumMap<RefQualifierKind> map = new CXXEnumMap<>(values());
    public static RefQualifierKind get(int value) {
        return map.get(value);
    }
}
