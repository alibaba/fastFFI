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
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeRefiner;
import com.alibaba.fastffi.llvm.LLVMPointer;
import com.alibaba.fastffi.llvm.StringOStream;
import com.alibaba.fastffi.stdcxx.StdString;

@FFIGen
@CXXHead({"clang/AST/Type.h", "clang/AST/Decl.h"})
@FFITypeAlias("clang::Type")
@FFITypeRefiner("com.alibaba.fastffi.clang.TypeRefiner.refine")
public interface Type extends LLVMPointer {
    TypeClass getTypeClass();

    boolean isFromAST();
    boolean containsUnexpandedParameterPack();

    boolean isCanonicalUnqualified();
    @CXXValue QualType getLocallyUnqualifiedSingleStepDesugaredType();
    boolean isSizelessType();
    boolean isSizelessBuiltinType();
    boolean isIncompleteOrObjectType();
    boolean isObjectType();
    boolean isLiteralType(@CXXReference ASTContext ctx);
    boolean isStandardLayoutType();
    boolean isBuiltinType();
    boolean isSpecificBuiltinType(int k);
    boolean isPlaceholderType();
    BuiltinType getAsPlaceholderType();
    boolean isSpecificPlaceholderType(int k);
    boolean isNonOverloadPlaceholderType();
    boolean isEnumeralType();
    boolean isScopedEnumeralType();
    boolean isCharType();
    boolean isWideCharType();
    boolean isChar8Type();
    boolean isChar16Type();
    boolean isChar32Type();
    boolean isAnyCharacterType();
    boolean isIntegralType (@CXXReference ASTContext ctx);
    boolean isIntegralOrUnscopedEnumerationType();
    boolean isUnscopedEnumerationType();
    boolean isRealFloatingType();
    boolean isComplexType();
    boolean isAnyComplexType();
    boolean isFloatingType();
    boolean isHalfType();
    boolean isFloat16Type();
    boolean isBFloat16Type();
    boolean isFloat128Type();
    boolean isRealType();
    boolean isArithmeticType();
    boolean isVoidType();
    boolean isAggregateType();
    boolean isFundamentalType();
    boolean isCompoundType();
    boolean isFunctionType();
    boolean isFunctionNoProtoType();
    boolean isFunctionProtoType();
    boolean isPointerType();
    boolean isAnyPointerType();
    boolean isBlockPointerType();
    boolean isVoidPointerType();
    boolean isReferenceType();
    boolean isLValueReferenceType();
    boolean isRValueReferenceType();
    boolean isObjectPointerType();
    boolean isFunctionPointerType();
    boolean isFunctionReferenceType();
    boolean isMemberPointerType();
    boolean isMemberFunctionPointerType();
    boolean isMemberDataPointerType();
    boolean isArrayType();
    boolean isConstantArrayType();
    boolean isIncompleteArrayType();
    boolean isVariableArrayType();
    boolean isDependentSizedArrayType();
    boolean isRecordType();
    boolean isClassType();
    boolean isStructureType();
    boolean isObjCBoxableRecordType();
    boolean isInterfaceType();
    boolean isStructureOrClassType();
    boolean isUnionType();
    boolean isComplexIntegerType();
    boolean isVectorType();
    boolean isExtVectorType();
    boolean isMatrixType();
    boolean isConstantMatrixType();
    boolean isDependentAddressSpaceType();
    boolean isTemplateTypeParmType();
    boolean isNullPtrType();
    boolean isNothrowT();
    boolean isAlignValT();
    boolean isStdByteType();
    boolean isAtomicType();
    boolean isUndeducedAutoType();
    TypeDependence getDependence();
    boolean containsErrors();
    boolean isDependentType();
    boolean isInstantiationDependentType();
    boolean isUndeducedType();
    boolean isVariablyModifiedType();
    boolean hasSizedVLAType();
    boolean hasUnnamedOrLocalType();
    boolean isOverloadableType();
    boolean isElaboratedTypeSpecifier();
    boolean canDecayToPointerType();
    boolean hasPointerRepresentation();
    boolean hasIntegerRepresentation();
    boolean hasSignedIntegerRepresentation();
    boolean hasUnsignedIntegerRepresentation();
    boolean hasFloatingRepresentation();
    RecordType getAsStructureType();
    RecordType getAsUnionType();
    ComplexType getAsComplexIntegerType();
    CXXRecordDecl getAsCXXRecordDecl();
    RecordDecl getAsRecordDecl();
    TagDecl getAsTagDecl();
    CXXRecordDecl getPointeeCXXRecordDecl();
    DeducedType getContainedDeducedType();
    AutoType getContainedAutoType();
    boolean hasAutoForTrailingReturnType();
    @CXXValue QualType getPointeeType();
    Type getUnqualifiedDesugaredType();
    boolean isPromotableIntegerType();
    boolean isSignedIntegerType();
    boolean isUnsignedIntegerType();
    boolean isSignedIntegerOrEnumerationType();
    boolean isUnsignedIntegerOrEnumerationType();
    boolean isFixedPointType();
    boolean isFixedPointOrIntegerType();
    boolean isSaturatedFixedPointType();
    boolean isUnsaturatedFixedPointType();
    boolean isSignedFixedPointType();
    boolean isUnsignedFixedPointType();
    boolean isConstantSizeType();
    boolean isSpecifierType();
    boolean isVisibilityExplicit();
    boolean isLinkageValid();
    boolean canHaveNullability (boolean resultIfUnknown);

    @CXXValue QualType getCanonicalTypeInternal();

    void dump(@CXXReference StringOStream Out, @CXXReference ASTContext Context);

    default String dump(@CXXReference ASTContext Context) {
        StdString out = StdString.create();
        StringOStream os = StringOStream.create(out);
        dump(os, Context);
        return out.toJavaString();
    }

    default StdString getAsString() {
        return QualType.create(this, 0).getAsString();
    }
}
