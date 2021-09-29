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

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;

@FFITypeAlias("llvm::Type::TypeID")
@FFITypeRefiner("com.alibaba.fastffi.llvm.TypeID.getTypeID")
public enum TypeID implements CXXEnum {

    // PrimitiveTypes
    HalfTyID(Library.INSTANCE.HalfTyID()),      ///< 16-bit floating point type
    BFloatTyID(Library.INSTANCE.BFloatTyID()),    ///< 16-bit floating point type (7-bit significand)
    FloatTyID(Library.INSTANCE.FloatTyID()),     ///< 32-bit floating point type
    DoubleTyID(Library.INSTANCE.DoubleTyID()),    ///< 64-bit floating point type
    X86_FP80TyID(Library.INSTANCE.X86_FP80TyID()),  ///< 80-bit floating point type (X87)
    FP128TyID(Library.INSTANCE.FP128TyID()),     ///< 128-bit floating point type (112-bit significand)
    PPC_FP128TyID(Library.INSTANCE.PPC_FP128TyID()), ///< 128-bit floating point type (two 64-bits(Library.INSTANCE.64-bits()), PowerPC)
    VoidTyID(Library.INSTANCE.VoidTyID()),      ///< type with no size
    LabelTyID(Library.INSTANCE.LabelTyID()),     ///< Labels
    MetadataTyID(Library.INSTANCE.MetadataTyID()),  ///< Metadata
    X86_MMXTyID(Library.INSTANCE.X86_MMXTyID()),   ///< MMX vectors (64 bits(Library.INSTANCE.bits()), X86 specific)
    TokenTyID(Library.INSTANCE.TokenTyID()),     ///< Tokens

    // Derived types... see DerivedTypes.h file.
    IntegerTyID(Library.INSTANCE.IntegerTyID()),       ///< Arbitrary bit width integers
    FunctionTyID(Library.INSTANCE.FunctionTyID()),      ///< Functions
    PointerTyID(Library.INSTANCE.PointerTyID()),       ///< Pointers
    StructTyID(Library.INSTANCE.StructTyID()),        ///< Structures
    ArrayTyID(Library.INSTANCE.ArrayTyID()),         ///< Arrays
    FixedVectorTyID(Library.INSTANCE.FixedVectorTyID()),   ///< Fixed width SIMD vector type
    ScalableVectorTyID(Library.INSTANCE.ScalableVectorTyID()) ///< Scalable SIMD vector type
    ;

    @FFIGen
    @CXXHead("llvm/IR/Type.h")
    @FFILibrary(value = "llvm::Type::TypeID", namespace = "llvm::Type::TypeID")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        // PrimitiveTypes
        @FFIGetter
        int HalfTyID();      ///< 16-bit floating point type
        @FFIGetter
        int BFloatTyID();    ///< 16-bit floating point type (7-bit significand)
        @FFIGetter
        int FloatTyID();     ///< 32-bit floating point type
        @FFIGetter
        int DoubleTyID();    ///< 64-bit floating point type
        @FFIGetter
        int X86_FP80TyID();  ///< 80-bit floating point type (X87)
        @FFIGetter
        int FP128TyID();     ///< 128-bit floating point type (112-bit significand)
        @FFIGetter
        int PPC_FP128TyID(); ///< 128-bit floating point type (two 64-bits(); PowerPC)
        @FFIGetter
        int VoidTyID();      ///< type with no size
        @FFIGetter
        int LabelTyID();     ///< Labels
        @FFIGetter
        int MetadataTyID();  ///< Metadata
        @FFIGetter
        int X86_MMXTyID();   ///< MMX vectors (64 bits(); X86 specific)
        @FFIGetter
        int TokenTyID();     ///< Tokens

        // Derived types... see DerivedTypes.h file.
        @FFIGetter
        int IntegerTyID();        ///< Arbitrary bit width integers
        @FFIGetter
        int FunctionTyID();       ///< Functions
        @FFIGetter
        int PointerTyID();        ///< Pointers
        @FFIGetter
        int StructTyID();         ///< Structures
        @FFIGetter
        int ArrayTyID();          ///< Arrays
        @FFIGetter
        int FixedVectorTyID();    ///< Fixed width SIMD vector type
        @FFIGetter
        int ScalableVectorTyID(); ///< Scalable SIMD vector type
    }

    int value;
    TypeID(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    public static final CXXEnumMap<TypeID> map = new CXXEnumMap<>(values());

    public static TypeID getTypeID(int value) {
        return map.get(value);
    }
}
