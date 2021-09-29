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
import com.alibaba.fastffi.llvm.Constant;
import com.alibaba.fastffi.llvm.ConstantDataArray;
import com.alibaba.fastffi.llvm.ConstantFP;
import com.alibaba.fastffi.llvm.ConstantInt;
import com.alibaba.fastffi.llvm.DataLayout;
import com.alibaba.fastffi.llvm.GlobalVariable;
import com.alibaba.fastffi.llvm.IntegerType;
import com.alibaba.fastffi.llvm.StructLayout;
import com.alibaba.fastffi.llvm.StructType;
import com.alibaba.fastffi.llvm.Type;
import com.alibaba.fastffi.llvm.ValueTy;
import com.alibaba.fastffi.llvm4jni.body.LibraryMethod;
import com.alibaba.fastffi.llvm4jni.body.MethodBody;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import static com.alibaba.fastffi.llvm4jni.LLVMToBytecode.unsupportedValue;

public class LLVMConstantInitializerGenerator extends AbstractBytecodeGenerator {

    MethodBody methodBody;
    Constant constant;

    public LLVMConstantInitializerGenerator(Universe universe, MethodBody methodBody, Constant constant) {
        super(universe);
        this.methodBody = methodBody;
        this.constant = constant;
    }

    public static void generate(Universe universe, LibraryMethod methodBody, GlobalVariable globalVariable) {
        LLVMConstantInitializerGenerator generator = new LLVMConstantInitializerGenerator(universe, methodBody, globalVariable.getInitializer());
        generator.generate();
    }

    @Override
    protected int getStackBasePointerLocalIndex() {
        throw new UnsupportedIRException(constant, "No alloca for constant initializer");
    }

    protected void emitLoadCursor() {
        addInstruction(new VarInsnNode(Opcodes.LLOAD, 0));
    }

    /**
     * Caller is responsible to update cursor
     * @param constant
     */
    protected void emitInitConstant(Constant constant) {
        ValueTy ty = constant.getValueID();
        switch (ty) {
            case BlockAddressVal: {
                throw LLVMToBytecode.unsupportedValue(constant, "TODO: no support of " + ty + " in Java");
            }
            case ConstantExprVal: {
                Type type = constant.getType();
                JavaKind kind = javaKindOrFail(constant);
                // TODO: no support of aggregate type now
                emitLoadUnsafe();
                emitLoadCursor();
                emitReadValue(constant);
                emitStoreKind(type, kind);
                return;
            }
            case ConstantArrayVal: {
                ArrayType arrayType = (ArrayType) constant.getType();
                Type elementType = arrayType.getElementType();
                long elementSize = getDataLayout().getTypeAllocSize(elementType);
                int numOp = constant.getNumOperands();
                if (numOp != arrayType.getNumElements()) {
                    throw new IllegalStateException("Oops");
                }
                for (int i = 0; i < numOp; i++) {
                    emitLoadCursor();
                    Constant op = (Constant) constant.getOperand(i);
                    emitInitConstant(op);
                    emitIncrementCursor(elementSize, false);
                }
                return;
            }
            case ConstantStructVal: {
                StructType structType = (StructType) constant.getType();
                // need to handle padding
                int numOp = constant.getNumOperands();
                if (numOp != structType.getNumContainedTypes()) {
                    throw new IllegalStateException("Oops");
                }
                long[] offsetDelta = computeStructOffsetDelta(structType);
                for (int i = 0; i < numOp; i++) {
                    emitLoadCursor();
                    Constant op = (Constant) constant.getOperand(i);
                    emitInitConstant(op);
                    emitIncrementCursor(offsetDelta[i + 1], false);
                }
                return;
            }
            case ConstantVectorVal:
            case UndefValueVal: {
                throw LLVMToBytecode.unsupportedValue(constant, "Do not support " + ty);
            }
            case ConstantAggregateZeroVal: {

                return;
            }
            case ConstantDataArrayVal: {
                ConstantDataArray dataArray = (ConstantDataArray) constant;
                int numElements = dataArray.getNumElements();
                Type elementType = dataArray.getElementType();
                switch (elementType.getTypeID()) {
                    case IntegerTyID: {
                        IntegerType integerType = (IntegerType) elementType;
                        int bitWidth = integerType.getBitWidth();
                        switch (bitWidth) {
                            case 1:
                            case 8:
                            case 16:
                            case 32: {
                                int bytes = Math.max(bitWidth / 8, 1);
                                for (int i = 0; i < numElements; i++) {
                                    emitLoadUnsafe();
                                    emitLoadCursor();
                                    int value = Math.toIntExact(dataArray.getElementAsInteger(i));
                                    emitLdc(value);
                                    emitStoreKind(elementType, javaKindOrFail(elementType));
                                    emitIncrementCursor(bytes);
                                }
                                return;
                            }
                            case 64: {
                                for (int i = 0; i < numElements; i++) {
                                    emitLoadUnsafe();
                                    emitLoadCursor();
                                    long value = dataArray.getElementAsInteger(i);
                                    emitLdc(value);
                                    emitStoreKind(elementType, javaKindOrFail(elementType));
                                    emitIncrementCursor(8);
                                }
                                return;
                            }
                            default:
                                throw LLVMToBytecode.unsupportedValue(dataArray, "unsupported BitWidth " + bitWidth);
                        }
                    }
                    case FloatTyID: {
                        for (int i = 0; i < numElements; i++) {
                            emitLoadUnsafe();
                            emitLoadCursor();
                            float value = dataArray.getElementAsFloat(i);
                            emitLdc(value);
                            emitStoreKind(elementType, javaKindOrFail(elementType));
                            emitIncrementCursor(4);
                        }
                        return;
                    }
                    case DoubleTyID: {
                        for (int i = 0; i < numElements; i++) {
                            emitLoadUnsafe();
                            emitLoadCursor();
                            double value = dataArray.getElementAsDouble(i);
                            emitLdc(value);
                            emitStoreKind(elementType, javaKindOrFail(elementType));
                            emitIncrementCursor(8);
                        }
                        return;
                    }
                }
            }
            case ConstantDataVectorVal: {
                throw LLVMToBytecode.unsupportedValue(constant, "Do not support " + ty);
            }
            case ConstantIntVal: {
                Type type = constant.getType();
                ConstantInt constantInt = (ConstantInt) constant;
                int bytes = Math.max(constantInt.getBitWidth() / 8, 1);
                switch (constantInt.getBitWidth()) {
                    case 1:
                    case 8:
                    case 16:
                    case 32:
                    case 64: {
                        emitLoadUnsafe();
                        emitLoadCursor();
                        emitReadValue(constant);
                        emitStoreKind(type, javaKindOrFail(constant));
                        return;
                    }
                    default:
                        throw LLVMToBytecode.unsupportedValue(constantInt, "unsupported bit width: " + constantInt.getBitWidth());
                }
            }
            case ConstantFPVal: {
                Type type = constant.getType();
                ConstantFP constantFloat = (ConstantFP) constant;
                switch (type.getTypeID()) {
                    case FloatTyID:
                    case DoubleTyID: {
                        emitLoadUnsafe();
                        emitLoadCursor();
                        emitReadValue(constant);
                        emitStoreKind(type, javaKindOrFail(constant));
                        return;
                    }
                    default:
                        throw LLVMToBytecode.unsupportedValue(constantFloat, "unsuppported float type: " + type.getTypeID());
                }
            }
            case ConstantPointerNullVal: {
                emitLoadUnsafe();
                emitLoadCursor();
                addInstruction(new InsnNode(Opcodes.LCONST_0));
                emitStoreKind(constant.getType(), javaKindOrFail(constant));
                return;
            }
            case ConstantTokenNoneVal: {
                throw LLVMToBytecode.unsupportedValue(constant);
            }
            default:
                throw LLVMToBytecode.unsupportedValue(constant, "Unknown constant variable: " + ty);
        }
    }

    private long[] computeStructOffsetDelta(StructType structType) {
        DataLayout dataLayout = getDataLayout();
        StructLayout structLayout = dataLayout.getStructLayout(structType);
        int numOfElements = structType.getNumContainedTypes();
        long[] offsetDelta = new long[numOfElements + 1];
        long lastOffset = 0;
        for (int i = 0; i < numOfElements; i++) {
            long curOffset = structLayout.getElementOffset(i);
            if (curOffset < lastOffset) {
                throw new IllegalStateException("Oops");
            }
            offsetDelta[i] = curOffset - lastOffset; // greater or equal zero
            lastOffset = curOffset;
        }
        return offsetDelta;
    }

    private void emitIncrementCursor(long i) {
        emitIncrementCursor(i, true);
    }

    private void emitIncrementCursor(long i, boolean load) {
        if (load) emitLoadCursor();
        emitLdc(i);
        addInstruction(new InsnNode(Opcodes.LADD));
        addInstruction(new VarInsnNode(Opcodes.LSTORE, 0));
    }

    public void generate() {
        unallocatedLocalIndex = 2; // reserve for the cursor
        emitInitConstant(constant);
        addInstruction(new InsnNode(Opcodes.RETURN));
        methodBody.setInsnList(toInsnList(instructions));
    }
}
