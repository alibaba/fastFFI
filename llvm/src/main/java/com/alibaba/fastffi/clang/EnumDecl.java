package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::EnumDecl")
@CXXHead("clang/AST/Decl.h")
public interface EnumDecl extends TagDecl {
    static EnumDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (EnumDecl) null);
    }

    EnumDecl getDefinition();

    @FFIGen
    @FFITypeAlias("clang::EnumDecl::enumerator_iterator")
    @CXXHead("clang/AST/Decl.h")
    interface enumerator_iterator extends FFIPointer {
        @CXXOperator("*")
        EnumConstantDecl get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference enumerator_iterator other);
    }

    @CXXValue enumerator_iterator enumerator_begin();
    @CXXValue enumerator_iterator enumerator_end();
}
