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

@CXXHead("clang/AST/ASTDumperUtils.h")
@FFITypeAlias("clang::ASTDumpOutputFormat")
@FFITypeRefiner("ASTDumpOutputFormat.get")
public enum ASTDumpOutputFormat implements CXXEnum {
    Default(Library.INSTANCE.ADOF_Default()),
    JSON(Library.INSTANCE.ADOF_JSON())
    ;

    int value;

    @FFIGen
    @FFILibrary(namespace = "clang::ASTDumpOutputFormat")
    @CXXHead("clang/AST/ASTDumperUtils.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter int ADOF_Default();
        @FFIGetter int ADOF_JSON();
    }

    ASTDumpOutputFormat(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    static CXXEnumMap<ASTDumpOutputFormat> map = new CXXEnumMap<>(values());
    public static ASTDumpOutputFormat get(int value) {
        return map.get(value);
    }
}
