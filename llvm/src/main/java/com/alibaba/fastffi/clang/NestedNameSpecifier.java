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
import com.alibaba.fastffi.llvm.LLVMPointer;

@FFIGen
@CXXHead("clang/AST/NestedNameSpecifier.h")
@FFITypeAlias("clang::NestedNameSpecifier")
public interface NestedNameSpecifier extends LLVMPointer {

    @FFITypeAlias("clang::NestedNameSpecifier::SpecifierKind")
    @FFITypeRefiner("SpecifierKind.get")
    enum SpecifierKind implements CXXEnum {

        Identifier(Library.INSTANCE.Identifier()),
        Namespace(Library.INSTANCE.Namespace()),
        NamespaceAlias(Library.INSTANCE.NamespaceAlias()),
        TypeSpec(Library.INSTANCE.TypeSpec()),
        TypeSpecWithTemplate(Library.INSTANCE.TypeSpecWithTemplate()),
        Global(Library.INSTANCE.Global()),
        Super(Library.INSTANCE.Super());

        @FFIGen
        @CXXHead("clang/AST/NestedNameSpecifier.h")
        @FFILibrary(value = "clang::NestedNameSpecifier::SpecifierKind", namespace = "clang::NestedNameSpecifier::SpecifierKind")
        interface Library {
            Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
            @FFIGetter int Identifier();
            @FFIGetter int Namespace();
            @FFIGetter int NamespaceAlias();
            @FFIGetter int TypeSpec();
            @FFIGetter int TypeSpecWithTemplate();
            @FFIGetter int Global();
            @FFIGetter int Super();
        }
        int value;
        SpecifierKind(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }

        static CXXEnumMap<SpecifierKind> map = new CXXEnumMap<>(values());
        public static SpecifierKind get(int value) {
            return map.get(value);
        }
    };

    SpecifierKind getKind();
    NestedNameSpecifier getPrefix();
    IdentifierInfo getAsIdentifier();
    NamespaceDecl getAsNamespace();
    NamespaceAliasDecl getAsNamespaceAlias();
    CXXRecordDecl getAsRecordDecl();
    Type getAsType();
}
