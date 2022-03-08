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
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.SmallVectorImpl;

@FFIGen
@CXXHead("clang/AST/ASTContext.h")
@FFITypeAlias("clang::ASTContext")
public interface ASTContext extends CXXPointer {
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

    @CXXValue QualType getWCharType();
    @CXXValue QualType getWideCharType();
    @CXXValue QualType getSignedWCharType();
    @CXXValue QualType getUnsignedWCharType();
    @CXXValue QualType getWIntType();
    @CXXValue QualType getIntPtrType();
    @CXXValue QualType getUIntPtrType();

    @CXXReference SmallVectorImpl<Type> getTypes();
}
