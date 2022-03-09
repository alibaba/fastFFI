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

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.LLVMPointer;
import com.alibaba.fastffi.llvm.SmallVectorImpl;

@FFIGen
@CXXHead({"clang/AST/ASTContext.h", "clang/AST/TemplateBase.h"})
@FFITypeAlias("clang::ASTContext")
public interface ASTContext extends CXXPointer, LLVMPointer {
    TranslationUnitDecl getTranslationUnitDecl();
    @CXXReference SourceManager getSourceManager();
    @CXXReference PrintingPolicy getPrintingPolicy();

    long getASTAllocatedMemory();
    long getSideTableAllocatedMemory();
    Decl getPrimaryMergedDecl(Decl D);
    TypedefDecl getInt128Decl();
    TypedefDecl getUInt128Decl();
    @CXXValue QualType getRestrictType(@CXXValue QualType T);
    @CXXValue QualType getVolatileType(@CXXValue QualType T);
    @CXXValue QualType getConstType(@CXXValue QualType T);

    @CXXValue QualType getPointerType(@CXXValue QualType T);
    @CXXValue QualType getAdjustedType(@CXXValue QualType Orig, @CXXValue QualType New);
    @CXXValue QualType getLValueReferenceType(@CXXValue QualType T, boolean SpelledAsLValue);
    @CXXValue QualType getRValueReferenceType(@CXXValue QualType T);
    @CXXValue QualType getTypeDeclType(TypeDecl Decl, TypeDecl PrevDecl);
    @CXXValue QualType getTypedefType(TypedefNameDecl Decl, @CXXValue QualType Underlying);
    @CXXValue QualType getRecordType(RecordDecl Decl);
    @CXXValue QualType getEnumType(EnumDecl Decl);
    @CXXValue QualType getTypeOfType(@CXXValue QualType t);
    @CXXValue QualType getInjectedClassNameType(CXXRecordDecl Decl, @CXXValue QualType TST);
    @CXXValue QualType getAttributedType(@CXXValue AttrKind attrKind, @CXXValue QualType modifiedType, @CXXValue QualType equivalentType);

    @CXXValue QualType getSubstTemplateTypeParmType(TemplateTypeParmType Replaced, @CXXValue QualType Replacement);
    @CXXValue QualType getSubstTemplateTypeParmPackType(TemplateTypeParmType Replaced, @CXXReference TemplateArgument ArgPack);
    @CXXValue QualType getTemplateTypeParmType(int Depth, int Index, boolean ParameterPack, TemplateTypeParmDecl ParmDecl);
    @CXXValue QualType getTemplateSpecializationType(@CXXValue TemplateName T, @CXXValue TemplateArgumentArray Args, @CXXValue QualType Canon);
    @CXXValue QualType getCanonicalTemplateSpecializationType(@CXXValue TemplateName T, @CXXValue TemplateArgumentArray Args);
    @CXXValue QualType getTemplateSpecializationType(@CXXValue TemplateName T, @CXXReference TemplateArgumentListInfo Args, @CXXValue QualType Canon);
    TypeSourceInfo getTemplateSpecializationTypeInfo(@CXXValue TemplateName T, @CXXValue SourceLocation TLoc, @CXXReference TemplateArgumentListInfo Args, @CXXValue QualType Canon);
    @CXXValue QualType getParenType(@CXXValue QualType NamedType);
    @CXXValue QualType getMacroQualifiedType(@CXXValue QualType UnderlyingTy, IdentifierInfo MacroII);
    @CXXValue QualType getElaboratedType(@CXXValue ElaboratedTypeKeyword Keyword, NestedNameSpecifier NNS, @CXXValue QualType NamedType, TagDecl OwnedTagDecl);
    @CXXValue QualType getDependentNameType(@CXXValue ElaboratedTypeKeyword Keyword, NestedNameSpecifier NNS, IdentifierInfo Name, @CXXValue QualType Canon);
    @CXXValue QualType getDependentTemplateSpecializationType(@CXXValue ElaboratedTypeKeyword Keyword, NestedNameSpecifier NNS, IdentifierInfo Name, @CXXReference TemplateArgumentListInfo Args);
    @CXXValue QualType getDependentTemplateSpecializationType(@CXXValue ElaboratedTypeKeyword Keyword, NestedNameSpecifier NNS, IdentifierInfo Name, @CXXValue TemplateArgumentArray Args);
    @CXXValue TemplateArgument getInjectedTemplateArg(NamedDecl ParamDecl);
    void getInjectedTemplateArgs(TemplateParameterList Params, @CXXReference SmallVectorImpl<TemplateArgument> Args);

    @CXXValue QualType getWCharType();
    @CXXValue QualType getWideCharType();
    @CXXValue QualType getSignedWCharType();
    @CXXValue QualType getUnsignedWCharType();
    @CXXValue QualType getWIntType();
    @CXXValue QualType getIntPtrType();
    @CXXValue QualType getUIntPtrType();

    int getTypeAlign(Type T);
    int getTypeUnadjustedAlign(@CXXValue QualType T);
    int getTypeUnadjustedAlign(Type T);

    Type getCanonicalType(Type T);
    boolean hasSameType(@CXXValue QualType T1, @CXXValue QualType T2);
    boolean hasSameType(Type T1, Type T2);

    @CXXValue TemplateArgument getCanonicalTemplateArgument(@CXXReference TemplateArgument Arg);

    @CXXReference SmallVectorImpl<Type> getTypes();
}
