package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.LLVMContext;
import com.alibaba.fastffi.llvm.LLVMPointer;

@FFIGen
@FFITypeAlias("clang::DeclContext")
@CXXHead("clang/AST/DeclBase.h")
public interface DeclContext extends FFIPointer {

    static Decl cast(DeclContext context) {
        return DeclCasting.INSTANCE.cast(context, (Decl) null);
    }

    @FFIGen
    @FFITypeAlias("clang::DeclContext::decl_iterator")
    @CXXHead("clang/AST/DeclBase.h")
    interface decl_iterator extends LLVMPointer {
        @CXXOperator("*")
        Decl get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference decl_iterator other);
    }

    Decl.Kind getDeclKind();

    @CXXValue decl_iterator decls_begin();
    @CXXValue decl_iterator decls_end();
    boolean decls_empty();

    DeclContext getParent();
    DeclContext getLexicalParent();
    DeclContext getLookupParent();

    @CXXReference ASTContext getParentASTContext();
}
