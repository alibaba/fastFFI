package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("clang::CXXRecordDecl")
@CXXHead("clang/AST/DeclCXX.h")
public interface CXXRecordDecl extends RecordDecl {
    static CXXRecordDecl dyn_cast(Decl decl) {
        return DeclCasting.INSTANCE.dyn_cast(decl, (CXXRecordDecl) null);
    }

    @FFIGen
    @FFITypeAlias("clang::CXXRecordDecl::base_class_iterator")
    @CXXHead("clang/AST/DeclCXX.h")
    interface base_class_iterator extends FFIPointer {
        @CXXOperator("*")
        @CXXReference CXXBaseSpecifier get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference base_class_iterator other);
    }

    @CXXValue base_class_iterator bases_begin();
    @CXXValue base_class_iterator bases_end();
    int getNumBases();

    @FFIGen
    @FFITypeAlias("clang::CXXRecordDecl::method_iterator")
    @CXXHead("clang/AST/DeclCXX.h")
    interface method_iterator extends FFIPointer {
        @CXXOperator("*")
        CXXMethodDecl get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference method_iterator other);
    }

    @CXXValue method_iterator method_begin();
    @CXXValue method_iterator method_end();

    @FFIGen
    @FFITypeAlias("clang::CXXRecordDecl::ctor_iterator")
    @CXXHead("clang/AST/DeclCXX.h")
    interface ctor_iterator extends FFIPointer {
        @CXXOperator("*")
        CXXConstructorDecl get();

        @CXXOperator("++")
        void next();

        @CXXOperator("!=")
        boolean notEquals(@CXXReference ctor_iterator other);
    }

    @CXXValue ctor_iterator ctor_begin();
    @CXXValue ctor_iterator ctor_end();

    boolean defaultedCopyConstructorIsDeleted();
    boolean defaultedMoveConstructorIsDeleted();
    boolean defaultedDestructorIsDeleted();
    boolean hasSimpleCopyConstructor();
    boolean hasSimpleMoveConstructor();
    boolean hasSimpleCopyAssignment();
    boolean hasSimpleMoveAssignment();
    boolean hasSimpleDestructor();
    boolean hasDefaultConstructor();
    boolean needsImplicitDefaultConstructor();
    boolean hasUserDeclaredConstructor();
    boolean hasUserProvidedDefaultConstructor();
    boolean hasUserDeclaredCopyConstructor();
    boolean needsImplicitCopyConstructor();
    boolean needsOverloadResolutionForCopyConstructor();
    boolean implicitCopyConstructorHasConstParam();
    boolean hasCopyConstructorWithConstParam();
    boolean hasUserDeclaredMoveOperation();
    boolean hasUserDeclaredMoveConstructor();
    boolean hasMoveConstructor();
    boolean needsImplicitMoveConstructor();
    boolean needsOverloadResolutionForMoveConstructor();
    boolean hasUserDeclaredCopyAssignment();
    boolean needsImplicitCopyAssignment();
    boolean needsOverloadResolutionForCopyAssignment();
    boolean implicitCopyAssignmentHasConstParam();
    boolean hasCopyAssignmentWithConstParam();
    boolean hasUserDeclaredMoveAssignment();
    boolean hasMoveAssignment();
    boolean needsImplicitMoveAssignment();
    boolean needsOverloadResolutionForMoveAssignment();
    boolean hasUserDeclaredDestructor();
    boolean needsImplicitDestructor();
    boolean needsOverloadResolutionForDestructor();
    boolean isLambda();
    boolean isGenericLambda();

    CXXRecordDecl getDefinition();
    CXXRecordDecl getCanonicalDecl();
    ClassTemplateDecl getDescribedClassTemplate();
}
