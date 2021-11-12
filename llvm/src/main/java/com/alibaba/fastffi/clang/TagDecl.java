package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::TagDecl")
@CXXHead("clang/AST/Decl.h")
public interface TagDecl extends TypeDecl, DeclContext {
    static TagDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (TagDecl) null);
    }

    TagDecl getDefinition();
    Decl getPreviousDecl();
    Decl getMostRecentDecl();
    boolean isFirstDecl();
    boolean isThisDeclarationADefinition();
    boolean isCompleteDefinition();
    boolean isBeingDefined();
    boolean isDependentType();

    boolean isClass();
    boolean isStruct();
    boolean isInterface();
    boolean isUnion();
    boolean isEnum();

    boolean isCXXClassMember();
    boolean isCXXInstanceMember();

    NestedNameSpecifier getQualifier();
}
