package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::CXXMethodDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface CXXMethodDecl extends FunctionDecl {
    static CXXMethodDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (CXXMethodDecl) null);
    }
    boolean isStatic();
    boolean isInstance();
    boolean isVirtual();
    boolean isConst();
    boolean isVolatile();
    boolean isLambdaStaticInvoker();

    @CXXValue Qualifiers getMethodQualifiers();
}
