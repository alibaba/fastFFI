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
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.LLVMPointer;
import com.alibaba.fastffi.stdcxx.StdString;

import javax.annotation.Nonnull;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::QualType")
public interface QualType extends LLVMPointer {
    @Nonnull Type getTypePtr();
    Type getTypePtrOrNull();

    boolean isCanonical();
    boolean isCanonicalAsParam();
    boolean isNull();
    boolean isLocalConstQualified();
    boolean isConstQualified();
    boolean isLocalRestrictQualified();
    boolean isRestrictQualified();
    boolean isLocalVolatileQualified();
    boolean isVolatileQualified();
    boolean hasLocalQualifiers();
    boolean hasQualifiers();
    boolean hasLocalNonFastQualifiers();

    @CXXValue Qualifiers getLocalQualifiers();
    @CXXValue Qualifiers getQualifiers();
    int getLocalCVRQualifiers();
    int getCVRQualifiers();

    @CXXValue QualType getCanonicalType();
    @CXXValue QualType getLocalUnqualifiedType();
    @CXXValue QualType getUnqualifiedType();

    @CXXValue StdString getAsString();
    IdentifierInfo getBaseTypeIdentifier();
}
