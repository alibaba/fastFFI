package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::FunctionDecl")
@CXXHead("clang/AST/Decl.h")
public interface FunctionDecl extends DeclaratorDecl, DeclContext {
    static FunctionDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (FunctionDecl) null);
    }

    Decl getPreviousDecl();
    Decl getMostRecentDecl();
    boolean isFirstDecl();
    FunctionDecl getDefinition();

    FunctionTemplateDecl getDescribedFunctionTemplate();

    boolean hasBody();
    boolean isDefined();
    boolean isVariadic();
    boolean isTrivial();
    boolean isDefaulted();
    boolean isExplicitlyDefaulted();
    boolean isUserProvided();
    boolean isDeleted();
    boolean isExternC();
    boolean isThisDeclarationADefinition();

    boolean isOverloadedOperator();
    @CXXValue OverloadedOperatorKind getOverloadedOperator();

    @FFIGen
    @FFITypeAlias("clang::FunctionDecl::param_iterator")
    @CXXHead("clang/AST/Decl.h")
    interface param_iterator extends FFIPointer {
        @CXXOperator("*")
        ParmVarDecl get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference param_iterator other);
    }

    @CXXValue param_iterator param_begin();
    @CXXValue param_iterator param_end();
    long param_size();

    @CXXValue QualType getReturnType();
    int getNumParams();
    ParmVarDecl getParamDecl(int idx);

    int getMinRequiredArguments();
}
