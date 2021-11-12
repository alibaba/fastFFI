package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::RecordDecl")
@CXXHead("clang/AST/Decl.h")
public interface RecordDecl extends TagDecl {
    static RecordDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (RecordDecl) null);
    }

    @FFIGen
    @FFITypeAlias("clang::RecordDecl::field_iterator")
    @CXXHead("clang/AST/Decl.h")
    interface field_iterator extends FFIPointer {
        @CXXOperator("*")
        FieldDecl get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference field_iterator other);
    }

    @CXXValue field_iterator field_begin();
    @CXXValue field_iterator field_end();
    boolean field_empty();

    boolean isAnonymousStructOrUnion();
    boolean hasObjectMember();
    boolean isInjectedClassName();
    RecordDecl getDefinition();
}
