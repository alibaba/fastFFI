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
package com.alibaba.fastffi.llvm;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFISkip;
import com.alibaba.fastffi.FFITypeFactory;

/**
 * Casting from Type to a proper subtype of Type
 */
@FFIGen
@CXXHead("llvm/IR/Type.h")
@CXXHead("llvm/IR/DerivedTypes.h")
@FFILibrary(value = "llvm::Type::Casting", namespace = "llvm")
public interface TypeCasting {

    TypeCastingGen INSTANCE = (TypeCastingGen) FFITypeFactory.getLibrary(TypeCasting.class);

    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::FunctionType"},
            java = {"Type", "FunctionType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::ArrayType"},
            java = {"Type", "ArrayType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::VectorType"},
            java = {"Type", "VectorType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::ScalableVectorType"},
            java = {"Type", "ScalableVectorType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::FixedVectorType"},
            java = {"Type", "FixedVectorType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::IntegerType"},
            java = {"Type", "IntegerType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::PointerType"},
            java = {"Type", "PointerType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::StructType"},
            java = {"Type", "StructType"}
    )
    <@FFISkip From, To> To cast(From from, @FFISkip To unused);

    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::FunctionType"},
            java = {"Type", "FunctionType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::ArrayType"},
            java = {"Type", "ArrayType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::VectorType"},
            java = {"Type", "VectorType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::ScalableVectorType"},
            java = {"Type", "ScalableVectorType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::FixedVectorType"},
            java = {"Type", "FixedVectorType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::IntegerType"},
            java = {"Type", "IntegerType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::PointerType"},
            java = {"Type", "PointerType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::StructType"},
            java = {"Type", "StructType"}
    )
    <@FFISkip From, To> To dyn_cast(From from, @FFISkip To unused);

    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::FunctionType"},
            java = {"Type", "FunctionType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::ArrayType"},
            java = {"Type", "ArrayType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::VectorType"},
            java = {"Type", "VectorType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::ScalableVectorType"},
            java = {"Type", "ScalableVectorType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::FixedVectorType"},
            java = {"Type", "FixedVectorType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::IntegerType"},
            java = {"Type", "IntegerType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::PointerType"},
            java = {"Type", "PointerType"}
    )
    @CXXTemplate(
            cxx = {"llvm::Type", "llvm::StructType"},
            java = {"Type", "StructType"}
    )
    <@FFISkip From, To> boolean isa(From from, @FFISkip To unused);
}
