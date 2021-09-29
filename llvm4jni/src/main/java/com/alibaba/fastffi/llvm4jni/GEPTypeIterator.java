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
package com.alibaba.fastffi.llvm4jni;

import com.alibaba.fastffi.llvm.ArrayType;
import com.alibaba.fastffi.llvm.ConstantInt;
import com.alibaba.fastffi.llvm.GetElementPtrInst;
import com.alibaba.fastffi.llvm.StructType;
import com.alibaba.fastffi.llvm.Type;
import com.alibaba.fastffi.llvm.Value;
import com.alibaba.fastffi.llvm.VectorType;

/**
 * Why we need to deal with opIndex = 0;
 * The GEP starts with a source type, however, the source type is
 * the pointee type of the first operand, which is either a vector type,
 * an array type or a struct type. Vector type is not supported now.
 * The pointer type of an array type or a struct type can be indexed
 * as the array type of the source array type or struct type.
 * Sicne we do not convert the pointer type to an array type,
 * we must do special treatment of the curType when opIndex = 0;
 */
public class GEPTypeIterator {

        // CurType points to the container type;
        Type curType;
        int opIndex;
        GetElementPtrInst inst;

        public GEPTypeIterator(GetElementPtrInst inst) {
            this.inst = inst;
            curType = inst.getSourceElementType();
            if (curType instanceof VectorType) {
                throw LLVMToBytecode.unsupportedType(curType, "GetElementPtr on VectorType is not supported.");
            }
            opIndex = 0;
        }

        public boolean hasNext() {
            return opIndex < inst.getNumIndices();
        }

        public void next() {
            if (opIndex == 0) {
                opIndex++;
                return;
            }
            Type type = getIndexedType();
            opIndex++;
            if (!hasNext()) {
                return;
            }
            if (type.isArrayTy()) {
                curType = type;
            } else if (type.isVectorTy()) {
                throw LLVMToBytecode.unsupportedType(type, "GetElementPtr on VectorType is not supported.");
            } else if (type.isStructTy()) {
                curType = type;
            } else {
                throw new IllegalStateException("Should not reach here." + type);
            }

        }

        /**
         * See llvm/IR/GetElementPtrIterator.h
         * The source element type is stored as a Type* pointer.
         * So it cannot be treated as a struct type event it is indeed a struct type.
         * @return
         */
        public StructType getStructTypeOrNull() {
            if (opIndex == 0) {
                // should be an array type that simulates the pointer type.
                // but we do not create the array trye and just use the pointee type
                // instead.
                return null;
            }
            if (curType.isStructTy()) {
                return (StructType) curType;
            }
            return null;
        }

        public Type getIndexedType() {
            if (opIndex == 0) {
                return curType;
            }
            if (curType.isStructTy()) {
                ConstantInt index = (ConstantInt) getOperand();
                StructType structType = (StructType) curType;
                return structType.getTypeAtIndex((int) index.getZExtValue());
            }
            if (curType instanceof ArrayType) {
                return ((ArrayType) curType).getElementType();
            }
            throw LLVMToBytecode.unsupportedType(curType, "GetElementPtr on " + curType + " is not supported.");
        }

        public Value getOperand() {
            return inst.getOperand(opIndex + 1);
        }

    }
