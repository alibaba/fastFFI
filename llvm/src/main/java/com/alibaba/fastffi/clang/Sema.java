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
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
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
@CXXHead("clang/Sema/Sema.h")
@FFITypeAlias("clang::Sema")
public interface Sema extends CXXPointer, LLVMPointer {
    @CXXValue QualType SubstAutoType(@CXXValue QualType TypeWithAuto, @CXXValue QualType Replacement);
    @CXXValue QualType ReplaceAutoType(@CXXValue QualType TypeWithAuto, @CXXValue QualType Replacement);

    boolean isConstantEvaluated();
    void addImplicitTypedef(@CXXValue StringRef Name, @CXXValue QualType T);
    @CXXReference ASTContext getASTContext();

    boolean inTemplateInstantiation();
    void PerformPendingInstantiations(boolean LocalOnly);

    @CXXValue QualType SubstType(
            @CXXValue QualType T,
            @CXXReference  MultiLevelTemplateArgumentList TemplateArgs,
            @CXXValue SourceLocation Loc,
            @CXXValue DeclarationName Entity);
    Decl SubstDecl(
            Decl D,
            DeclContext Owner,
            @CXXReference MultiLevelTemplateArgumentList TemplateArgs);
    FunctionDecl SubstSpaceshipAsEqualEqual(
            CXXRecordDecl RD,
            FunctionDecl Spaceship);
    boolean SubstBaseSpecifiers(
            CXXRecordDecl Instantiation,
            CXXRecordDecl Pattern,
            @CXXReference MultiLevelTemplateArgumentList TemplateArgs);

    boolean InstantiateClass(
            @CXXValue SourceLocation PointOfInstantiation,
            CXXRecordDecl Instantiation,
            CXXRecordDecl Pattern,
            @CXXReference MultiLevelTemplateArgumentList TemplateArgs,
            @CXXValue TemplateSpecializationKind TSK,
            boolean Complain);
    boolean InstantiateEnum(
            @CXXValue SourceLocation PointOfInstantiation,
            EnumDecl Instantiation,
            EnumDecl Pattern,
            @CXXReference MultiLevelTemplateArgumentList TemplateArgs,
            @CXXValue TemplateSpecializationKind TSK);
    boolean InstantiateInClassInitializer(
            @CXXValue SourceLocation PointOfInstantiation,
            FieldDecl Instantiation,
            FieldDecl Pattern,
            @CXXReference MultiLevelTemplateArgumentList TemplateArgs);
    void InstantiateAttrs(
            @CXXReference MultiLevelTemplateArgumentList TemplateArgs,
            Decl Pattern,
            Decl Inst,
            LateInstantiatedAttrVec LateAttrs,
            LocalInstantiationScope OuterMostScope);
    void InstantiateAttrsForDecl(
            @CXXReference MultiLevelTemplateArgumentList TemplateArgs,
            Decl Pattern,
            Decl Inst,
            LateInstantiatedAttrVec LateAttrs,
            LocalInstantiationScope OuterMostScope);

    boolean InstantiateClassTemplateSpecialization(
            @CXXReference SourceLocation PointOfInstantiation,
            ClassTemplateSpecializationDecl ClassTemplateSpec,
            @CXXValue TemplateSpecializationKind TSK,
            boolean Complain);
    void InstantiateClassMembers(
            @CXXValue SourceLocation PointOfInstantiation,
            CXXRecordDecl Instantiation,
            @CXXReference MultiLevelTemplateArgumentList TemplateArgs,
            @CXXValue TemplateSpecializationKind TSK);
    void InstantiateClassTemplateSpecializationMembers(
            @CXXValue SourceLocation PointOfInstantiation,
            ClassTemplateSpecializationDecl ClassTemplateSpec,
            @CXXValue TemplateSpecializationKind TSK);
    boolean InstantiateDefaultArgument(
            @CXXValue SourceLocation CallLoc,
            FunctionDecl FD,
            ParmVarDecl Param);
    void InstantiateExceptionSpec(
            @CXXValue SourceLocation PointOfInstantiation,
            FunctionDecl Function);

    boolean isCompleteType(@CXXValue SourceLocation Loc, @CXXValue QualType T, @CXXValue CompleteTypeKind Kind);
    boolean RequireCompleteType(@CXXValue SourceLocation Loc, @CXXValue QualType T, @CXXValue CompleteTypeKind Kind, @CXXReference TypeDiagnoser Diagnoser);
    boolean RequireCompleteType(@CXXValue SourceLocation Loc, @CXXValue QualType T, @CXXValue CompleteTypeKind Kind, int DiagID);
    boolean RequireCompleteType(@CXXValue SourceLocation Loc, @CXXValue QualType T, @CXXReference TypeDiagnoser Diagnoser);
    boolean RequireCompleteType(@CXXValue SourceLocation Loc, @CXXValue QualType T, @CXXValue int DiagID);

    @FFITypeAlias("clang::Sema::CompleteTypeKind")
    @FFITypeRefiner("com.alibaba.fastffi.clang.Sema.CompleteTypeKind.get")
    enum CompleteTypeKind implements CXXEnum
    {
        Normal(Sema.CompleteTypeKind.Library.INSTANCE.Normal()),
        AcceptSizeless(Sema.CompleteTypeKind.Library.INSTANCE.AcceptSizeless()),
        Default(Sema.CompleteTypeKind.Library.INSTANCE.Default())
        ;

        @FFIGen
        @CXXHead("clang/Sema/Sema.h")
        @FFILibrary(value = "clang::Sema::CompleteTypeKind", namespace = "clang::Sema::CompleteTypeKind")
        interface Library {
            Sema.CompleteTypeKind.Library INSTANCE = FFITypeFactory.getLibrary(Sema.CompleteTypeKind.Library.class);
            @FFIGetter int Normal();
            @FFIGetter int AcceptSizeless();
            @FFIGetter int Default();
        }

        int value;
        CompleteTypeKind(int value) {
            this.value = value;
        }

        private static CXXEnumMap<Sema.CompleteTypeKind> map = new CXXEnumMap<>(Sema.CompleteTypeKind.values());
        public static Sema.CompleteTypeKind get(int value) {
            return map.get(value);
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
