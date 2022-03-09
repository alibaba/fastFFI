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
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;
import com.alibaba.fastffi.llvm.LLVMPointer;

@FFIGen
@FFITypeAlias("clang::TemplateArgument")
@CXXHead("clang/AST/TemplateBase.h")
public interface TemplateArgument extends LLVMPointer {

    @FFITypeAlias("clang::TemplateArgument::ArgKind")
    @FFITypeRefiner("com.alibaba.fastffi.clang.TemplateArgument.ArgKind.get")
    enum ArgKind implements CXXEnum {
        Null(Library.INSTANCE.Null()),
        Type(Library.INSTANCE.Type()),
        Declaration(Library.INSTANCE.Declaration()),
        NullPtr(Library.INSTANCE.NullPtr()),
        Integral(Library.INSTANCE.Integral()),
        Template(Library.INSTANCE.Template()),
        TemplateExpansion(Library.INSTANCE.TemplateExpansion()),
        Expression(Library.INSTANCE.Expression()),
        Pack(Library.INSTANCE.Pack()),
        ;

        @FFIGen
        @CXXHead("clang/AST/TemplateBase.h")
        @FFILibrary(namespace = "clang::TemplateArgument::ArgKind")
        interface Library {
            Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
            @FFIGetter int Null();
            @FFIGetter int Type();
            @FFIGetter int Declaration();
            @FFIGetter int NullPtr();
            @FFIGetter int Integral();
            @FFIGetter int Template();
            @FFIGetter int TemplateExpansion();
            @FFIGetter int Expression();
            @FFIGetter int Pack();
        }

        int value;

        ArgKind(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }

        static CXXEnumMap<ArgKind> map = new CXXEnumMap<>(values());
        public static ArgKind get(int value) {
            return map.get(value);
        }
    }

    ArgKind getKind();
    @CXXValue QualType getAsType();
}
