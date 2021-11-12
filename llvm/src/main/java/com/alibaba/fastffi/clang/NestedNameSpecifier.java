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
import com.alibaba.fastffi.llvm.StringRef;

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
