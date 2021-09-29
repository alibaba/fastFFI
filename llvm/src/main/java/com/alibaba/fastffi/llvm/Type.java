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
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeRefiner;

@FFIGen
@CXXHead("llvm/IR/DerivedTypes.h")
@FFITypeAlias("llvm::Type")
@FFITypeRefiner("com.alibaba.fastffi.llvm.LLVMTypeRefiner.refine")
public interface Type extends LLVMPointer {

    TypeID getTypeID();

    /// Return true if this is 'void'.
    boolean isVoidTy();

    /// Return true if this is 'half', a 16-bit IEEE fp type.
    boolean isHalfTy();

    /// Return true if this is 'bfloat', a 16-bit bfloat type.
    boolean isBFloatTy();

    /// Return true if this is 'float', a 32-bit IEEE fp type.
    boolean isFloatTy();

    /// Return true if this is 'double', a 64-bit IEEE fp type.
    boolean isDoubleTy();

    /// Return true if this is x86 long double.
    boolean isX86_FP80Ty();

    /// Return true if this is 'fp128'.
    boolean isFP128Ty();

    /// Return true if this is powerpc long double.
    boolean isPPC_FP128Ty();

    /// Return true if this is one of the six floating-point types
    boolean isFloatingPointTy();

    /// Return true if this is X86 MMX.
    boolean isX86_MMXTy();

    /// Return true if this is a FP type or a vector of FP.
    boolean isFPOrFPVectorTy();

    /// Return true if this is 'label'.
    boolean isLabelTy();

    /// Return true if this is 'metadata'.
    boolean isMetadataTy();

    /// Return true if this is 'token'.
    boolean isTokenTy();

    /// True if this is an instance of IntegerType.
    boolean isIntegerTy();

    /// Return true if this is an IntegerType of the given width.
    boolean isIntegerTy(int Bitwidth);

    /// Return true if this is an integer type or a vector of integer types.
    boolean isIntOrIntVectorTy();

    /// Return true if this is an integer type or a vector of integer types of
    /// the given width.
    boolean isIntOrIntVectorTy(int BitWidth);

    /// Return true if this is an integer type or a pointer type.
    boolean isIntOrPtrTy();

    /// True if this is an instance of FunctionType.
    boolean isFunctionTy();

    /// True if this is an instance of StructType.
    boolean isStructTy();

    /// True if this is an instance of ArrayType.
    boolean isArrayTy();

    /// True if this is an instance of PointerType.
    boolean isPointerTy();

    /// Return true if this is a pointer type or a vector of pointer types.
    boolean isPtrOrPtrVectorTy();

    /// True if this is an instance of VectorType.
    boolean isVectorTy();

    /// Return true if this type could be converted with a lossless BitCast to
    /// type 'Ty'. For example, i8* to i32*. BitCasts are valid for types of the
    /// same size only where no re-interpretation of the bits is done.
    /// Determine if this type could be losslessly bitcast to Ty
    boolean canLosslesslyBitCastTo(Type Ty);

    /// Return true if this type is empty, that is, it has no elements or all of
    /// its elements are empty.
    boolean isEmptyTy();

    /// Return true if the type is "first class", meaning it is a valid type for a
    /// Value.
    boolean isFirstClassType();

    /// Return true if the type is a valid type for a register in codegen. This
    /// includes all first-class types except struct and array types.
    boolean isSingleValueType();

    /// Return true if the type is an aggregate type. This means it is valid as
    /// the first operand of an insertvalue or extractvalue instruction. This
    /// includes struct and array types, but does not include vector types.
    boolean isAggregateType();

    Type getContainedType(int index);

    int getNumContainedTypes();
    int getIntegerBitWidth();

    Type getFunctionParamType(int i);
    int getFunctionNumParams();
    boolean isFunctionVarArg();

    @CXXValue StringRef getStructName();
    int getStructNumElements();
    Type getStructElementType(int N);

    long getArrayNumElements();
    Type getArrayElementType();
    Type getPointerElementType();
    Type getScalarType();
}
