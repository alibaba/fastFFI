package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@FFITypeAlias("clang::TranslationUnitDecl")
@CXXHead("clang/AST/Decl.h")
public interface TranslationUnitDecl extends Decl, DeclContext {
    static TranslationUnitDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (TranslationUnitDecl) null);
    }
    NamespaceDecl getAnonymousNamespace();
}
