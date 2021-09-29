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

import com.alibaba.fastffi.CXXValueScope;
import com.alibaba.fastffi.llvm.AllocaInst;
import com.alibaba.fastffi.llvm.Argument;
import com.alibaba.fastffi.llvm.AtomicCmpXchgInst;
import com.alibaba.fastffi.llvm.AtomicRMWInst;
import com.alibaba.fastffi.llvm.BasicBlock;
import com.alibaba.fastffi.llvm.BinaryOperator;
import com.alibaba.fastffi.llvm.BitCastInst;
import com.alibaba.fastffi.llvm.BranchInst;
import com.alibaba.fastffi.llvm.CallBase;
import com.alibaba.fastffi.llvm.CallInst;
import com.alibaba.fastffi.llvm.CastInst;
import com.alibaba.fastffi.llvm.Constant;
import com.alibaba.fastffi.llvm.ConstantDataSequential;
import com.alibaba.fastffi.llvm.ConstantExpr;
import com.alibaba.fastffi.llvm.ConstantFP;
import com.alibaba.fastffi.llvm.ConstantInt;
import com.alibaba.fastffi.llvm.ConstantPointerNull;
import com.alibaba.fastffi.llvm.DataLayout;
import com.alibaba.fastffi.llvm.FCmpInst;
import com.alibaba.fastffi.llvm.FPExtInst;
import com.alibaba.fastffi.llvm.FPToSIInst;
import com.alibaba.fastffi.llvm.FPToUIInst;
import com.alibaba.fastffi.llvm.FPTruncInst;
import com.alibaba.fastffi.llvm.Function;
import com.alibaba.fastffi.llvm.FunctionType;
import com.alibaba.fastffi.llvm.GetElementPtrInst;
import com.alibaba.fastffi.llvm.GlobalVariable;
import com.alibaba.fastffi.llvm.ICmpInst;
import com.alibaba.fastffi.llvm.IndirectBrInst;
import com.alibaba.fastffi.llvm.Instruction;
import com.alibaba.fastffi.llvm.IntToPtrInst;
import com.alibaba.fastffi.llvm.IntegerType;
import com.alibaba.fastffi.llvm.IntrinsicID;
import com.alibaba.fastffi.llvm.IntrinsicInst;
import com.alibaba.fastffi.llvm.InvokeInst;
import com.alibaba.fastffi.llvm.LoadInst;
import com.alibaba.fastffi.llvm.Opcode;
import com.alibaba.fastffi.llvm.Optional;
import com.alibaba.fastffi.llvm.PHINode;
import com.alibaba.fastffi.llvm.PointerType;
import com.alibaba.fastffi.llvm.Predicate;
import com.alibaba.fastffi.llvm.PtrToIntInst;
import com.alibaba.fastffi.llvm.ResumeInst;
import com.alibaba.fastffi.llvm.ReturnInst;
import com.alibaba.fastffi.llvm.SExtInst;
import com.alibaba.fastffi.llvm.SIToFPInst;
import com.alibaba.fastffi.llvm.SelectInst;
import com.alibaba.fastffi.llvm.StoreInst;
import com.alibaba.fastffi.llvm.SwitchInst;
import com.alibaba.fastffi.llvm.TruncInst;
import com.alibaba.fastffi.llvm.Type;
import com.alibaba.fastffi.llvm.UIToFPInst;
import com.alibaba.fastffi.llvm.UnaryOperator;
import com.alibaba.fastffi.llvm.UnreachableInst;
import com.alibaba.fastffi.llvm.Value;
import com.alibaba.fastffi.llvm.ZExtInst;
import com.alibaba.fastffi.llvm4jni.body.LibraryClass;
import com.alibaba.fastffi.llvm4jni.body.LibraryMethod;
import com.alibaba.fastffi.llvm4jni.type.FieldTypeDef;
import com.alibaba.fastffi.llvm4jni.type.MethodTypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDef;
import com.alibaba.fastffi.llvm4jni.type.TypeDefException;
import com.alibaba.fastffi.llvm4jni.runtime.JavaRuntime;
import com.alibaba.fastffi.llvm4jni.runtime.RuntimeGlobal;
import com.alibaba.fastffi.llvm4jni.runtime.UnreachableException;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastffi.llvm4jni.LLVMToBytecode.unsupportedValue;


public abstract class AbstractBytecodeGenerator {

    Options options;

    // every LLVMBasicBlock has a blockId
    Map<BasicBlock, LabelNode> basicBlockLabels = new HashMap<>();

    // temporary list containers for instructions generation
    protected List<AbstractInsnNode> instructions = new ArrayList<>();

    protected int unallocatedLocalIndex;
    protected Map<Value, Integer> valueToLocalIndex = new HashMap<>();
    // start from zero and generate negative offsets
    protected long unallocatedAllocaOffset = 0;
    Map<AllocaInst, Long> allocaToOffset = new HashMap<>();

    boolean needStackPointer;

    IndirectCallPolicy indirectCallPolicy;

    // Ignore GEP on JNIEnv and JNINativeInterface together their LoadInst
    Map<Value, JavaTag> valueToJavaTag = new HashMap<>();
    Map<Value, TypeDef> jclassToTypeDef = new HashMap<>();
    // TODO: now we only support mono devirtualization
    Map<Value, VTableEntry> valueToVTableEntry = new HashMap<>();


    Universe universe;

    public AbstractBytecodeGenerator(Universe universe) {
        this.universe = universe;
        this.options = universe.getOptions();
        this.indirectCallPolicy = IndirectCallPolicy.createIndirectCallPolicy(options);
    }

    public LabelNode getBlockLabel(BasicBlock bb) {
        LabelNode label = basicBlockLabels.get(bb);
        if (label == null) {
            label = new LabelNode();
            basicBlockLabels.put(bb, label);
        }
        return label;
    }

    protected InsnList toInsnList(List<AbstractInsnNode> instructions, boolean print) {
        InsnList insnList = InstructionOptimizer.toInsnList(instructions);
        if (print) {
            InstructionOptimizer.printInsnList(insnList);
        }
        return insnList;
    }

    protected InsnList toInsnList(List<AbstractInsnNode> instructions) {
        return toInsnList(instructions, options.printInsn());
    }

    void emitLoadUnsafe() {
        FieldInsnNode load = new FieldInsnNode(Opcodes.GETSTATIC, JavaRuntime.DESCRIPTOR, "UNSAFE", "Lsun/misc/Unsafe;");
        addInstruction(load);
    }

    void addInstruction(AbstractInsnNode node) {
        instructions.add(node);
    }

    long getOrAllocateAllocaOffset(AllocaInst inst) {
        Long offset = allocaToOffset.get(inst);
        if (offset == null) {
            if (!inst.isStaticAlloca()) {
                throw LLVMToBytecode.unsupportedValue(inst, "Only static alloca is supported");
            }
            Optional<Long> sizeOrNone = inst.getAllocationSizeInBits(getDataLayout());
            if (!sizeOrNone.hasValue()) {
                throw LLVMToBytecode.unsupportedValue(inst, "Cannot determine size of type");
            }
            long size = sizeOrNone.getValue();
            int alignment = inst.getAlignment();
            if (!Utils.powerOfTwo(alignment)) {
                throw LLVMToBytecode.unsupportedValue(inst, "Alignment is not power of 2: align=" + alignment);
            }
            if (alignment < 8) {
                alignment = 8;
            }
            unallocatedAllocaOffset = Utils.allocateStack(unallocatedAllocaOffset, size, alignment);
            allocaToOffset.put(inst, unallocatedAllocaOffset);
            return unallocatedAllocaOffset;
        }
        return offset;
    }

    public JavaKind sameJavaKindOrFail(Value first, Value ... others) {
        JavaKind kind = javaKindOrFail(first);
        for (int i = 0; i < others.length; i++) {
            Value other = others[i];
            JavaKind otherKind = javaKindOrFail(other);
            if (otherKind != kind) {
                // TODO: too hacky
                if (isConstantPointerNull(first) && kind == JavaKind.Long && otherKind == JavaKind.Object) {
                    continue;
                } else if (isConstantPointerNull(other) && otherKind == JavaKind.Long && kind == JavaKind.Object) {
                    continue;
                }
                throw LLVMToBytecode.unsupportedValue(others[i]);
            }
        }
        return kind;
    }

    public JavaKind javaKindOrFail(Value value) {
        JavaTag tag = getJavaTag(value);
        if (tag != null) {
            return tag.isArray() ? JavaKind.Array : JavaKind.Object;
        }
        return LLVMToBytecode.javaKindOrFail(value);
    }

    public JavaKind javaKindOrFail(Type type) {
        return LLVMToBytecode.javaKindOrFail(type);
    }

    protected void collectInfo(Instruction inst) {
        Logger.debug("Collect info for " + inst);
        if (inst instanceof AllocaInst) {
            if (!options.supportAlloca()) {
                throw LLVMToBytecode.unsupportedValue(inst, "Alloca is disabled.");
            }
            AllocaInst allocaInst = (AllocaInst) inst;
            if (allocaInst.isStaticAlloca()) {
                needStackPointer = true;
            } else {
                throw LLVMToBytecode.unsupportedValue(inst, "TODO: no support of dynamic alloca now.");
            }
            return;
        } else if (inst instanceof GetElementPtrInst) {
            GetElementPtrInst gep = (GetElementPtrInst) inst;
            Value pointer = gep.getPointerOperand();
            if (isJNINativeInterfacePointerType(pointer)) {
                if (gep.getNumIndices() != 2) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Do not know how to deal with read JNINativeInterface");
                }
                Value idx0 = gep.getOperand(1);
                if (!isConstantIntZero(idx0)) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Do not know how to deal with read JNINativeInterface");
                }
                Value idx1 = gep.getOperand(2);
                if (!(idx1 instanceof ConstantInt)) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Do not know how to deal with read JNINativeInterface");
                }
                JNIEnvFun jniEnvFun = getJNIFunction(gep);
                mapJavaTag(inst, JavaTag.jniEnvFun(inst, jniEnvFun));
                return;
            } else if (isJNIEnvPointerType(pointer)) {
                if (gep.getNumIndices() != 2) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Do not know how to deal with read JNIEnv");
                }
                Value idx0 = gep.getOperand(1);
                Value idx1 = gep.getOperand(2);
                if (!isConstantIntZero(idx0) || !isConstantIntZero(idx1)) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Do not know how to deal with read JNIEnv");
                }
                mapJavaTag(inst, JavaTag.jniNativeInterface(inst));
                return;
            }
            {
                Logger.debug("Skip add JavaTag for " + gep);
                // TODO:
                return;
            }
        } else if (inst instanceof LoadInst) {
            LoadInst loadInst = (LoadInst) inst;
            Value pointer = loadInst.getPointerOperand();
            if (isJNINativeInterfacePointerType(pointer)) {
                mapJavaTag(loadInst, JavaTag.jniNativeInterface(loadInst));
            } else if (isJNIEnFunType(pointer)) {
                JavaTag javaTag = getJavaTag(pointer);
                if (javaTag.getType() != JavaTag.TagType.JNIEnvFun) {
                    throw new IllegalStateException();
                }
                mapJavaTag(loadInst, JavaTag.jniEnvFun(loadInst, (JNIEnvFun) javaTag.getData()));
            } else {
                Logger.debug("Skip add JavaTag for " + loadInst);
            }
            return;
        } else if (inst instanceof BitCastInst) {
            Value from = inst.getOperand(0);
            JavaTag tag = getJavaTag(from);
            if (tag != null) {
                mapJavaTag(inst, tag);
            }
            return;
        } else if (inst instanceof CallBase) {
            if (collectJNIInfo((CallBase) inst)) {
                return;
            }
            // A JNI call must not be a virtual dispatching call
            if (collectVTableInfo((CallBase) inst)) {
                return;
            }
            return;
        } else if (inst instanceof PHINode) {
            // We do not do iterative analysis until a fixed point.
            // We simply require that all incoming values and the PHINode must have the same JavaTag;
            PHINode phi = (PHINode) inst;
            int numOfValue = phi.getNumIncomingValues();
            if (numOfValue == 0) {
                // TODO: impossible
                return;
            }
            JavaTag javaTag = null;
            // propagate all tag and data
            for (int i = 0; i < numOfValue; i++) {
                Value value = phi.getIncomingValue(i);
                JavaTag curTag = getJavaTag(value);
                javaTag = mergeJavaTag(value, javaTag, curTag);
            }
            if (javaTag != null) {
                mapJavaTag(inst, JavaTag.tag(inst, javaTag.getType(), javaTag.getData()));
            }
        }
    }

    protected boolean collectVTableInfo(CallBase inst) {
        Value called = inst.getCalledOperand();
        FunctionType functionType = inst.getFunctionType();
        int vtableIndex = -1;
        {
            // (1) Check called: must be a LoadInst
            if (!(called instanceof LoadInst)) {
                return false;
            }
            Type type = called.getType();
            if (!(type instanceof PointerType)) {
                return false;
            }
            PointerType pointerType = (PointerType) type;
            if (!pointerType.getElementType().equals(functionType)) {
                // must be a pointer type pointing to the function
                return false;
            }
        }

        LoadInst loadInst = (LoadInst) called;
        {
            // (2) Check loaded
            Value value = loadInst.getPointerOperand();
            if (!(value instanceof GetElementPtrInst)) {
                return false;
            }
            GetElementPtrInst gep = (GetElementPtrInst) value;
            // Must be constant offset even though the type of pointer is not a StructType
            if (!gep.isInBounds()) {
                return false;
            }
            if (gep.getNumIndices() != 1) {
                return false;
            }
            if (!isConstantInt(gep.getOperand(1))) {
                return false;
            }
            ConstantInt constantInt = (ConstantInt) gep.getOperand(1);
            vtableIndex = Math.toIntExact(constantInt.getZExtValue());
            Type type = gep.getType();
            // must be a pointer type pointing to
            // a pointer type pointing to
            // a function type
            if (!(type instanceof PointerType)) {
                return false;
            }
            PointerType pointerType = (PointerType) type;
            type = pointerType.getElementType();
            if (!(type instanceof PointerType)) {
                return false;
            }
            pointerType = (PointerType) type;
            if (!pointerType.getElementType().equals(functionType)) {
                return false;
            }
            value = gep.getPointerOperand();
            if (!(value instanceof LoadInst)) {
                return false;
            }
            loadInst = (LoadInst) value;
        }

        {
            Value value = loadInst.getPointerOperand();
            if (!(value instanceof CastInst)) {
                return false;
            }
            Type type = value.getType();
            // must be a pointer type pointing to
            // a pointer type pointing to
            // a pointer type pointing to
            // a function type
            if (!(type instanceof PointerType)) {
                return false;
            }
            PointerType pointerType = (PointerType) type;
            type = pointerType.getElementType();
            if (!(type instanceof PointerType)) {
                return false;
            }
            pointerType = (PointerType) type;
            type = pointerType.getElementType();
            if (!(type instanceof PointerType)) {
                return false;
            }
            pointerType = (PointerType) type;
            if (!pointerType.getElementType().equals(functionType)) {
                return false;
            }
        }

        List<VTableEntry> matchedEntries = universe.getVTableEntryList(functionType);
        if (matchedEntries.isEmpty()) {
            return false;
        }
        VTableEntry selected = null;
        for (VTableEntry entry : matchedEntries) {
            // TODO: select the first one with index vtableIndex
            if (entry.getIndex() == vtableIndex) {
                selected = entry;
                break;
            }
        }
        if (selected == null) {
            return false;
        }
        if (valueToVTableEntry.put(inst, selected) != null) {
            throw new IllegalStateException("Visited twice??");
        }
        return true;
    }

    protected VTableEntry getVTableEntry(Value value) {
        return this.valueToVTableEntry.get(value);
    }

    private JavaTag mergeJavaTag(Value value, JavaTag tag1, JavaTag tag2) {
        if (tag1 != null) {
            if (tag2 == null) {
                return tag1;
            } else {
                JavaTag.TagType tagType1 = tag1.getType();
                JavaTag.TagType tagType2 = tag2.getType();
                if (tagType1 != tagType2) {
                    throw LLVMToBytecode.unsupportedValue(value, "TODO: Do not know how to merge JavaTag " + tagType1 + " and " + tagType2);
                }

                switch (tagType1) {
                    case JNIEnv:
                        return JavaTag.jniEnv(value);
                    case JNINativeInterface:
                        return JavaTag.jniNativeInterface(value);
                    case JNIEnvFun:
                        JNIEnvFun fun1 = (JNIEnvFun) tag1.getData();
                        JNIEnvFun fun2 = (JNIEnvFun) tag2.getData();
                        if (!fun1.equals(fun2)) {
                            throw LLVMToBytecode.unsupportedValue(value, "TODO: Do not know how to merge JavaTag " + fun1 + " and " + fun2);
                        }
                        return JavaTag.jniEnvFun(value, fun1);
                    default:
                        throw LLVMToBytecode.unsupportedValue(value, "TODO: Do not know how to merge JavaTag " + tagType1);
                }

            }
        } else {
            return tag2;
        }
    }

    /**
     * Try fetch a string from a ConstantData, a GlobalVariable or ..
     * @param value
     * @return
     */
    public String getStringFromCStringConstant(Value value) {
        if (value instanceof GlobalVariable) {
            GlobalVariable globalVariable = (GlobalVariable) value;
            if (!globalVariable.isConstant()) {
                return null;
            }
            if (!globalVariable.hasInitializer()) {
                return null;
            }
            Constant c = globalVariable.getInitializer();
            return getStringFromCStringConstant(c);
        } else if (value instanceof ConstantDataSequential) {
            ConstantDataSequential cds = (ConstantDataSequential) value;
            if (cds.isCString()) {
                return cds.getAsCString();
            }
            return null;
        } else if (value instanceof ConstantExpr) {
            ConstantExpr ce = (ConstantExpr) value;
            if (ce.isGEPWithNoNotionalOverIndexing()) {
                GetElementPtrInst gep = (GetElementPtrInst) ce.getAsInstruction();
                Value pointer = gep.getPointerOperand();
                String baseString = getStringFromCStringConstant(pointer);
                if (baseString == null) {
                    return null;
                }
                int index = Math.toIntExact(getConstantOffset(gep));
                if (index >= baseString.length()) {
                    throw new IllegalStateException();
                }
                if (index != 0) {
                    throw LLVMToBytecode.unsupportedValue(value, "TODO: need to deal with encoding");
                }
                return baseString.substring(index);
            }
            return null;
        }

        return null;
    }

    private String getStringOrFail(Value value, String argName, JNIEnvFun jniFun) {
        String string = getStringFromCStringConstant(value);
        if (string == null) {
            throw LLVMToBytecode.unsupportedValue(value, "Cannot get string for " + argName + " in " + jniFun);
        }
        return string;
    }

    private UnsupportedIRException unsupportedJNIFunction(Value value, JNIEnvFun fun) {
        throw LLVMToBytecode.unsupportedValue(value, "Unsupported JNI function: " + fun);
    }

    private UnsupportedIRException unsupportedJNIFunction(Value value, JNIEnvFun fun, String reason) {
        throw LLVMToBytecode.unsupportedValue(value, "Unsupported JNI function: " + fun + ", reason = " + reason);
    }

    protected boolean collectJNIInfo(CallBase inst) {
        try {
            return collectJNIInfoInner(inst);
        } catch (TypeDefException e) {
            throw LLVMToBytecode.unsupportedValue(inst, "" + e.getMessage() + " during JNI info analysis.");
        }
    }

    protected JavaKind getResultKind(JNIEnvFun jniFun) {
        switch (jniFun) {
            case CallVoidMethod:
            case CallNonvirtualVoidMethod:
            case CallStaticVoidMethod:
            case SetObjectField:
            case SetBooleanField:
            case SetByteField:
            case SetCharField:
            case SetShortField:
            case SetIntField:
            case SetLongField:
            case SetFloatField:
            case SetDoubleField:
            case SetStaticObjectField:
            case SetStaticBooleanField:
            case SetStaticByteField:
            case SetStaticCharField:
            case SetStaticShortField:
            case SetStaticIntField:
            case SetStaticLongField:
            case SetStaticFloatField:
            case SetStaticDoubleField:
                return JavaKind.Void;
            case CallObjectMethod:
            case CallNonvirtualObjectMethod:
            case CallStaticObjectMethod:
            case GetObjectField:
            case GetStaticObjectField:
                return JavaKind.Object;
            case CallBooleanMethod:
            case CallNonvirtualBooleanMethod:
            case CallStaticBooleanMethod:
            case GetBooleanField:
            case GetStaticBooleanField:
                return JavaKind.Boolean;
            case CallByteMethod:
            case CallNonvirtualByteMethod:
            case CallStaticByteMethod:
            case GetByteField:
            case GetStaticByteField:
                return JavaKind.Byte;
            case CallCharMethod:
            case CallNonvirtualCharMethod:
            case CallStaticCharMethod:
            case GetCharField:
            case GetStaticCharField:
                return JavaKind.Character;
            case CallShortMethod:
            case CallNonvirtualShortMethod:
            case CallStaticShortMethod:
            case GetShortField:
            case GetStaticShortField:
                return JavaKind.Short;
            case CallIntMethod:
            case CallNonvirtualIntMethod:
            case CallStaticIntMethod:
            case GetIntField:
            case GetStaticIntField:
                return JavaKind.Integer;
            case CallLongMethod:
            case CallNonvirtualLongMethod:
            case CallStaticLongMethod:
            case GetLongField:
            case GetStaticLongField:
                return JavaKind.Long;
            case CallFloatMethod:
            case CallNonvirtualFloatMethod:
            case CallStaticFloatMethod:
            case GetFloatField:
            case GetStaticFloatField:
                return JavaKind.Float;
            case CallDoubleMethod:
            case CallNonvirtualDoubleMethod:
            case CallStaticDoubleMethod:
            case GetDoubleField:
            case GetStaticDoubleField:
                return JavaKind.Double;
            default:
                throw new IllegalStateException("Oops. unknown JNIFunction " + jniFun);
        }
    }

    protected void checkJNIFunctionResultType(CallBase inst, JNIEnvFun jniFun, TypeDef typeDef) {
        JavaKind got = javaKind(typeDef);
        checkJNIFunctionResultType(inst, jniFun, got);
    }

    protected void checkJNIFunctionResultType(CallBase inst, JNIEnvFun jniFun, JavaKind got) {
        JavaKind expected = getResultKind(jniFun);
        if (expected != got) {
            throw unsupportedJNIFunction(inst, jniFun, "Expected result kind: " + expected + ", got: " + got);
        }
    }

    /**
     * Do a simple linear scan of each instruction to make sure that every
     * Java non-primitive argument of each JNI function call has a proper Java value (e.g., Java tag).
     * TODO: build call graph and do inter-procedural type inference/annotation
     * Note that all jclass, jmethodID and jfieldID must be compile-time constants.
     * @param inst
     */
    protected boolean collectJNIInfoInner(CallBase inst) {
        JNIEnvFun jniFun = getJNIFunction(inst);
        if (jniFun == null) {
            return false;
        }
        switch (jniFun) {
            case GetVersion:
                // See JavaRuntime.jniGetVersion
                break;
            case DefineClass:
                throw unsupportedJNIFunction(inst, jniFun);
            case FindClass: {
                // 0: JNIEnv*; 1: class name
                // TODO: we could support non-constant className via Reflection API.
                // However, this would not be cost-effective.
                TypeDef typeDef = getTypeDef(getStringOrFail(inst.getArgOperand(1), "class name", jniFun));
                typeDef = typeDef.getExact();
                mapJavaTagForJClass(inst, typeDef);
                break;
            }
            case FromReflectedMethod: {
                // 0: JNIEnv*; 1: jobject java.lang.reflect.Method
                // JavaTag methodObject = getAccessibleJavaTagOrFail(inst.getArgOperand(1));
                //mapJavaTag(inst, JavaTag.jmethodID(inst, methodObject));
                //break;
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case FromReflectedField: {
                // 0: JNIEnv*; 1: jobject java.lang.reflect.Field
                // JavaTag fieldObject = getAccessibleJavaTagOrFail(inst.getArgOperand(1));
                // mapJavaTag(inst, JavaTag.jfieldID(inst, fieldObject));
                // break;
                // TODO: the problem is that we cannot infer its exact type.
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case ToReflectedMethod:
                // 0: JNIEnv*; 1: jmethodId
                // JavaTag jmethodId = getAccessibleJavaTagOrFail(inst.getArgOperand(1));
                // mapJavaTag(inst, JavaTag.jmethodID(inst, jmethodId));
                throw unsupportedJNIFunction(inst, jniFun);
            case GetSuperclass: {
                // 0: JNIEnv*; 1: jclass
                // TODO: assume java.lang.Object is a valid super class for all classes.
                // For primitive and interface, the getSuperclass returns null.
                TypeDef input = getJClassTypeDef(inst.getArgOperand(1));
                if (input.isExact()) {
                    TypeDef superType = input.getSuperType();
                    if (superType != null) {
                        mapJavaTagForJClass(inst, input.getSuperType());
                    } else {
                        // TODO: we are sure it is null
                        mapJavaTagForJClass(inst, universe.getJavaLangObject());
                    }
                } else {
                    mapJavaTagForJClass(inst, universe.getJavaLangObject());
                }
                break;
            }
            case IsAssignableFrom: {
                // 0: JNIEnv*; 1: jclass; 2: jclass
                // NOTE: No need to know which jclass is.
                getTypeDef(inst.getArgOperand(1)); // must be tagged
                getTypeDef(inst.getArgOperand(2));
                break;
            }
            case ToReflectedField: {
                // 0: JNIEnv*; 1: jfieldId
                // JavaTag jfieldId = getAccessibleJavaTagOrFail(inst.getArgOperand(1));
                // mapJavaTag(inst, JavaTag.jfieldID(inst, jfieldId));
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case Throw: {
                // 0: JNIEnv*; 1: jthrowable
                // getTypeDef(inst.getArgOperand(1));
                // We must know whether the arg is a throwable
                // break;
                // TODO: We need to remove the trailing return.
                // Enable it when we know how to remove trailing return after throw.
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case ThrowNew: {
                // 0: JNIEnv*; 1: jclass; 2: char * message;
                // getTypeDef(inst.getArgOperand(1));
                // break;
                // TODO: We need to remove the trailing return.
                // Enable it when we know how to remove trailing return after throw.
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case ExceptionOccurred:
            case ExceptionDescribe:
            case ExceptionClear:
            case FatalError:
            case PushLocalFrame:
            case PopLocalFrame: {
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case NewGlobalRef: {
                // 0: JNIEnv*; 1: jobject
                // JavaTag from = getAccessibleJavaTagOrFail(inst.getArgOperand(1));
                // mapJavaTag(inst, JavaTag.tag(inst, from.getType(), from.getData()));
                // TODO: since we cannot support delete gloabl ref, we should not support NewGlobalRef
                // break;
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case DeleteGlobalRef: {
                // TODO: the only thing we can do in Java world is to ignore the delete operation.
                // However, if we ignore the delete operation, the GlobalRef may leak.
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case DeleteLocalRef: {
                // 0: JNIEnv*; 1: jobject
                // getAccessibleJavaTagOrFail(inst.getArgOperand(1));
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case IsSameObject: {
                // 0: JNIEnv*; 1: jobject; 2: jobject
                TypeDef typeDef1 = getTypeDef(inst.getArgOperand(1));
                TypeDef typeDef2 = getTypeDef(inst.getArgOperand(2));
                if (!(typeDef1.isAssignableFrom(typeDef2) || typeDef2.isAssignableFrom(typeDef1))) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot compare two unassignable references");
                }
                break;
            }
            case NewLocalRef: {
                // 0: JNIEnv*; 1: jobject
                // JavaTag from = getAccessibleJavaTagOrFail(inst.getArgOperand(1));
                // mapJavaTag(inst, JavaTag.tag(inst, from.getType(), from.getData()));
                // break;
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case EnsureLocalCapacity: {
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case AllocObject: {
                // 0: JNIEnv*; 1: jclass
                // NOTE: this is something like reflection. However, we hope any use of the allocated object
                // is annotated with proper type.
                TypeDef typeDef = getJClassTypeDef(inst.getArgOperand(1));
                if (!typeDef.isExact()) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot allocate objects for an inexact type: " + typeDef);
                }
                if (typeDef.isArray() || typeDef.isPrimitive() || typeDef.isAbstract()) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot allocate objects for " + typeDef);
                }
                mapJavaTag(inst, JavaTag.jobject(inst, typeDef));
                break;
            }
            case NewObject: {
                // 0: JNIEnv*; 1: jclass; 2: jmethodID; 3: args
                TypeDef typeDef = getJClassTypeDef(inst.getArgOperand(1));
                if (!typeDef.isExact()) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot allocate objects for an inexact type: " + typeDef);
                }
                if (typeDef.isArray() || typeDef.isPrimitive() || typeDef.isAbstract()) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot allocate objects " + typeDef);
                }
                MethodTypeDef methodTypeDef = getMethodTypeDef(inst.getArgOperand(2));
                checkParameterAccessibleJavaTagOrFail(inst, methodTypeDef, 3);
                mapJavaTag(inst, JavaTag.jobject(inst, typeDef));
                break;
            }
            case NewObjectV:
            case NewObjectA: {
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case GetObjectClass: {
                // 0: JNIEnv*; 1: jobject;
                TypeDef typeDef = getTypeDef(inst.getArgOperand(1));
                mapJavaTagForJClass(inst, typeDef);
                break;
            }
            case IsInstanceOf: {
                // 0: JNIEnv*; 1: jobject; 2: jclass
                TypeDef objectType = getTypeDef(inst.getArgOperand(1));
                TypeDef classType = getJClassTypeDef(inst.getArgOperand(2));
                ensureObjectOrArray(inst, objectType);
                ensureObjectOrArray(inst, classType);
                // TODO: we need to check whether the static type of jobject is a subtype of the interface
                ensureAssignable(inst, objectType, classType);
                break;
            }
            case GetMethodID:
            case GetStaticMethodID: {
                // 0: JNIEnv*; 1: jclass; 2: method name; 3: method desc;
                TypeDef ownerDef = getJClassTypeDef(inst.getArgOperand(1));
                if (!ownerDef.isExact()) {
                    throw unsupportedJNIFunction(inst, jniFun, "not an exact type");
                }
                String name = getStringOrFail(inst.getArgOperand(2), "name", jniFun);
                String desc = getStringOrFail(inst.getArgOperand(3), "desc", jniFun);
                mapJavaTag(inst, JavaTag.jmethodID(inst, getMethodTypeDef(inst, ownerDef, name, desc)));
                break;
            }
            case CallObjectMethod:
            case CallBooleanMethod:
            case CallByteMethod:
            case CallCharMethod:
            case CallShortMethod:
            case CallIntMethod:
            case CallLongMethod:
            case CallFloatMethod:
            case CallDoubleMethod:
            case CallVoidMethod: {
                // 0: JNIEnv*; 1: jobject; 2: jmethodId; 3...
                TypeDef typeDef = getTypeDef(inst.getArgOperand(1));
                MethodTypeDef methodTypeDef = getMethodTypeDef(inst.getArgOperand(2));
                ensureMemberOf(inst, methodTypeDef, typeDef);
                checkParameterAccessibleJavaTagOrFail(inst, methodTypeDef, 3);
                checkJNIFunctionResultType(inst, jniFun, methodTypeDef.returnType());
                if (jniFun == JNIEnvFun.CallObjectMethod) {
                    mapJavaTag(inst, methodTypeDef.returnType());
                }
                break;
            }
            case CallNonvirtualObjectMethod:
            case CallNonvirtualBooleanMethod:
            case CallNonvirtualByteMethod:
            case CallNonvirtualCharMethod:
            case CallNonvirtualShortMethod:
            case CallNonvirtualIntMethod:
            case CallNonvirtualLongMethod:
            case CallNonvirtualFloatMethod:
            case CallNonvirtualDoubleMethod:
            case CallNonvirtualVoidMethod: {
                // 0: JNIEnv*; 1: jobject; 2: jclass, 3: jmethodId; 4...
                TypeDef typeDef1 = getTypeDef(inst.getArgOperand(1));
                TypeDef typeDef2 = getJClassTypeDef(inst.getArgOperand(2));
                MethodTypeDef methodTypeDef = getMethodTypeDef(inst.getArgOperand(3));
                ensureAssignable(inst, typeDef2, typeDef1);
                ensureMemberOf(inst, methodTypeDef, typeDef2);
                checkParameterAccessibleJavaTagOrFail(inst, methodTypeDef, 4);
                checkJNIFunctionResultType(inst, jniFun, methodTypeDef.returnType());
                if (jniFun == JNIEnvFun.CallNonvirtualObjectMethod) {
                    mapJavaTag(inst, methodTypeDef.returnType());
                }
                break;
            }
            case GetFieldID:
            case GetStaticFieldID: {
                // 0: JNIEnv*; 1: jclass; 2: method name; 3: method desc;
                TypeDef ownerDef = getJClassTypeDef(inst.getArgOperand(1));
                if (!ownerDef.isExact()) {
                    throw unsupportedJNIFunction(inst, jniFun, "not an exact type");
                }
                String name = getStringOrFail(inst.getArgOperand(2), "name", jniFun);
                String desc = getStringOrFail(inst.getArgOperand(3), "desc", jniFun);
                mapJavaTag(inst, JavaTag.jfieldID(inst, getFieldTypeDef(inst, ownerDef, name, desc)));
                break;
            }
            case GetObjectField:
            case GetBooleanField:
            case GetByteField:
            case GetCharField:
            case GetShortField:
            case GetIntField:
            case GetLongField:
            case GetFloatField:
            case GetDoubleField: {
                // 0: JNIEnv*; 1: jobject; 2: jfieldID
                TypeDef typeDef = getTypeDef(inst.getArgOperand(1));
                FieldTypeDef fieldTypeDef = getFieldTypeDef(inst.getArgOperand(2));
                if (fieldTypeDef.isStatic()) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot access a static field via " + jniFun);
                }
                ensureMemberOf(inst, fieldTypeDef, typeDef);
                checkJNIFunctionResultType(inst, jniFun, fieldTypeDef.type());
                if (jniFun == JNIEnvFun.GetObjectField) {
                    mapJavaTag(inst, fieldTypeDef.type());
                }
                break;
            }
            case SetObjectField:
            case SetBooleanField:
            case SetByteField:
            case SetCharField:
            case SetShortField:
            case SetIntField:
            case SetLongField:
            case SetFloatField:
            case SetDoubleField: {
                // 0: JNIEnv*; 1: jobject; 2: jfieldID; 3: jobject if SetObjectField
                TypeDef typeDef = getTypeDef(inst.getArgOperand(1)); // make sure the jclass has been tagged
                FieldTypeDef fieldTypeDef = getFieldTypeDef(inst.getArgOperand(2));
                if (fieldTypeDef.isStatic()) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot access a static field via " + jniFun);
                }
                ensureMemberOf(inst, fieldTypeDef, typeDef);
                checkJNIFunctionResultType(inst, jniFun, JavaKind.Void);
                if (jniFun == JNIEnvFun.SetObjectField) {
                    ensureAssignable(inst, fieldTypeDef.type(), inst.getArgOperand(3));
                }
                break;
            }
            case CallStaticObjectMethod:
            case CallStaticBooleanMethod:
            case CallStaticByteMethod:
            case CallStaticCharMethod:
            case CallStaticShortMethod:
            case CallStaticIntMethod:
            case CallStaticLongMethod:
            case CallStaticFloatMethod:
            case CallStaticDoubleMethod:
            case CallStaticVoidMethod: {
                // 0: JNIEnv*; 1: jclass; 2: jmethodID; 3: jobject if SetObjectField
                TypeDef typeDef = getJClassTypeDef(inst.getArgOperand(1)); // make sure the jclass has been tagged
                MethodTypeDef methodTypeDef = getMethodTypeDef(inst.getArgOperand(2));
                ensureMemberOf(inst, methodTypeDef, typeDef);
                checkParameterAccessibleJavaTagOrFail(inst, methodTypeDef, 3);
                checkJNIFunctionResultType(inst, jniFun, methodTypeDef.returnType());
                if (jniFun == JNIEnvFun.CallStaticObjectMethod) {
                    mapJavaTag(inst, methodTypeDef.returnType());
                }
                break;
            }
            case GetStaticObjectField:
            case GetStaticBooleanField:
            case GetStaticByteField:
            case GetStaticCharField:
            case GetStaticShortField:
            case GetStaticIntField:
            case GetStaticLongField:
            case GetStaticFloatField:
            case GetStaticDoubleField: {
                // 0: JNIEnv*; 1: jclass; 2: jfieldID
                TypeDef typeDef = getJClassTypeDef(inst.getArgOperand(1)); // make sure the jclass has been tagged
                FieldTypeDef fieldTypeDef = getFieldTypeDef(inst.getArgOperand(2));
                ensureMemberOf(inst, fieldTypeDef, typeDef);
                checkJNIFunctionResultType(inst, jniFun, fieldTypeDef.type());
                if (!fieldTypeDef.isStatic()) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot access an instance field via " + jniFun);
                }
                if (jniFun == JNIEnvFun.GetStaticObjectField) {
                    mapJavaTag(inst, fieldTypeDef.type());
                }
                break;
            }
            case SetStaticObjectField:
            case SetStaticBooleanField:
            case SetStaticByteField:
            case SetStaticCharField:
            case SetStaticShortField:
            case SetStaticIntField:
            case SetStaticLongField:
            case SetStaticFloatField:
            case SetStaticDoubleField: {
                // 0: JNIEnv*; 1: jclass; 2: jfieldID; 3: jobject if SetObjectField
                TypeDef typeDef = getJClassTypeDef(inst.getArgOperand(1)); // make sure the jclass has been tagged
                FieldTypeDef fieldTypeDef = getFieldTypeDef(inst.getArgOperand(2));
                ensureMemberOf(inst, fieldTypeDef, typeDef);
                checkJNIFunctionResultType(inst, jniFun, JavaKind.Void);
                if (!fieldTypeDef.isStatic()) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot access an instance field via " + jniFun);
                }
                if (jniFun == JNIEnvFun.SetStaticObjectField) {
                    ensureAssignable(inst, fieldTypeDef.type(), inst.getArgOperand(3));
                }
                break;
            }
            case NewString:
            case GetStringLength:
            case GetStringChars:
            case ReleaseStringChars:
            case NewStringUTF:
            case GetStringUTFLength:
            case GetStringUTFChars:
            case ReleaseStringUTFChars: {
                throw unsupportedJNIFunction(inst, jniFun, "TODO: string is not supported now");
            }
            case GetArrayLength: {
                // 0: JNIEnv*; 1: array;
                ensureArray(inst, getTypeDef(inst.getArgOperand(1)));
                break;
            }
            case NewObjectArray: {
                // 0: JNIEnv*; 1: size; 2: jclass; 3: initial element
                TypeDef componentType = getJClassTypeDef(inst.getArgOperand(2));
                if (!componentType.isExact()) {
                    throw unsupportedJNIFunction(inst, jniFun, "cannot allocate array on inexact types");
                }
                ensureObjectOrArray(inst, componentType);
                ensureAssignable(inst, componentType, inst.getArgOperand(3));
                mapJavaTag(inst, JavaTag.objectArray(inst, componentType.getArrayType()));
                break;
            }
            case GetObjectArrayElement: {
                // 0: JNIEnv*; 1: array; 2: index;
                TypeDef arrayType = getTypeDef(inst.getArgOperand(1));
                ensureObjectArray(inst, arrayType);
                mapJavaTag(inst, JavaTag.jobject(inst, arrayType.getComponentTypeDef()));
                break;
            }
            case SetObjectArrayElement: {
                // 0: JNIEnv*; 1: array; 2: index; 3: value
                TypeDef arrayType = getTypeDef(inst.getArgOperand(1));
                ensureObjectArray(inst, arrayType);
                ensureAssignable(inst, arrayType.getComponentTypeDef(), inst.getArgOperand(3));
                break;
            }
            case NewBooleanArray: {
                mapJavaTag(inst, JavaTag.jbooleanArray(inst));
                break;
            }
            case NewByteArray: {
                mapJavaTag(inst, JavaTag.jbyteArray(inst));
                break;
            }
            case NewCharArray: {
                mapJavaTag(inst, JavaTag.jcharArray(inst));
                break;
            }
            case NewShortArray: {
                mapJavaTag(inst, JavaTag.jshortArray(inst));
                break;
            }
            case NewIntArray: {
                mapJavaTag(inst, JavaTag.jintArray(inst));
                break;
            }
            case NewLongArray: {
                mapJavaTag(inst, JavaTag.jlongArray(inst));
                break;
            }
            case NewFloatArray: {
                mapJavaTag(inst, JavaTag.jfloatArray(inst));
                break;
            }
            case NewDoubleArray: {
                mapJavaTag(inst, JavaTag.jdoubleArray(inst));
                break;
            }
            case GetBooleanArrayElements:
            case GetByteArrayElements:
            case GetCharArrayElements:
            case GetShortArrayElements:
            case GetIntArrayElements:
            case GetLongArrayElements:
            case GetFloatArrayElements:
            case GetDoubleArrayElements:
            case ReleaseBooleanArrayElements:
            case ReleaseByteArrayElements:
            case ReleaseCharArrayElements:
            case ReleaseShortArrayElements:
            case ReleaseIntArrayElements:
            case ReleaseLongArrayElements:
            case ReleaseFloatArrayElements:
            case ReleaseDoubleArrayElements:
                throw unsupportedJNIFunction(inst, jniFun, "TODO: support array elements");
            case RegisterNatives:
            case UnregisterNatives:
                throw unsupportedJNIFunction(inst, jniFun);
            case MonitorEnter:
            case MonitorExit: {
                // 0: JNIEnv*; 1: jobject
                getTypeDef(inst.getArgOperand(1));
                break;
            }
            case GetJavaVM: {
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case GetStringRegion:
            case GetStringUTFRegion: {
                throw unsupportedJNIFunction(inst, jniFun, "TODO: string is not supported now");
            }
            case GetPrimitiveArrayCritical:
            case ReleasePrimitiveArrayCritical: {
                throw unsupportedJNIFunction(inst, jniFun, "TODO: support array elements");
            }
            case GetStringCritical:
            case ReleaseStringCritical: {
                // 0: JNIEnv*; 1: jstring
                throw unsupportedJNIFunction(inst, jniFun, "TODO: string is not supported now");
            }
            case NewWeakGlobalRef:
            case DeleteWeakGlobalRef: {
                // 0: JNIEnv*; 1: jobject
                // getAccessibleJavaTagOrFail(inst.getArgOperand(1));
                // break;
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case ExceptionCheck: {
                // TODO: exception control flow is hard to rebuild
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case NewDirectByteBuffer: {
                // mapJavaTag(inst, JavaTag.jobject(inst, null));
                // break;
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case GetDirectBufferAddress:
            case GetDirectBufferCapacity: {
                // 0: JNIEnv*; 1: jobject
                // getAccessibleJavaTagOrFail(inst.getArgOperand(1));
                // break;
                throw unsupportedJNIFunction(inst, jniFun);
            }
            case GetObjectRefType: {
                // 0: JNIEnv*; 1: jobject
                //getAccessibleJavaTagOrFail(inst.getArgOperand(1));
                //break;
                throw unsupportedJNIFunction(inst, jniFun);
            }
            default:
                throw LLVMToBytecode.unsupportedValue(inst, "TODO: do not know how to support JNI function " + jniFun);
        }
        return true;
    }

    protected JavaKind javaKind(TypeDef typeDef) {
        if (typeDef.isArray()) {
            return JavaKind.Array;
        }
        if (typeDef.isPrimitive()) {
            String name = typeDef.getName();
            switch (name) {
                case "void":
                    return JavaKind.Void;
                case "boolean":
                    return JavaKind.Boolean;
                case "byte":
                    return JavaKind.Byte;
                case "short":
                    return JavaKind.Short;
                case "char":
                    return JavaKind.Character;
                case "int":
                    return JavaKind.Integer;
                case "float":
                    return JavaKind.Float;
                case "long":
                    return JavaKind.Long;
                case "double":
                    return JavaKind.Double;
                default:
                    throw new IllegalArgumentException("Not a valid primitive type: " + typeDef);
            }
        }
        return JavaKind.Object;
    }

    protected void ensureObjectOrArray(Value value, TypeDef typeDef) {
        if (typeDef == null || typeDef.isPrimitive()) {
            throw LLVMToBytecode.unsupportedValue(value, "Expected object or array, got " + typeDef);
        }
    }

    protected void ensureObjectArray(Value value, TypeDef typeDef) {
        if (typeDef == null || !typeDef.isArray() || typeDef.getComponentTypeDef().isPrimitive()) {
            throw LLVMToBytecode.unsupportedValue(value, "Expected object array, got " + typeDef);
        }
    }

    protected void ensureArray(Value value, TypeDef typeDef) {
        if (typeDef == null || !typeDef.isArray()) {
            throw LLVMToBytecode.unsupportedValue(value, "Expected array, got " + typeDef);
        }
    }

    private boolean isSubclassOf(TypeDef theClass, TypeDef sup) {
        if (!theClass.isExact()) {
            theClass = theClass.getExact();
        }
        if (!sup.isExact()) {
            sup = sup.getExact();
        }
        while (theClass != null) {
            if (theClass == sup) {
                return true;
            }
            theClass = theClass.getSuperType();
        }
        return false;
    }

    /**
     *
     * @param theClass: e.g., java.util.List
     * @param fromValue: e.g., java.lang.String (false) and java.util.ArrayList (true)
     */
    private void ensureAssignable(Value value, TypeDef theClass, Value fromValue) {
        if (isConstantPointerNull(fromValue)) {
            if (theClass.isPrimitive()) {
                throw LLVMToBytecode.unsupportedValue(value, "Type " + theClass + " must be an assignable from " + value);
            }
            return;
        }
        TypeDef from = getTypeDef(fromValue);
        if (!theClass.isAssignableFrom(from)) {
            throw LLVMToBytecode.unsupportedValue(value, "Type " + theClass + " must be an assignable from " + from);
        }
    }

    private void ensureAssignable(Value value, TypeDef theClass, TypeDef from) {
        if (!theClass.isAssignableFrom(from)) {
            throw LLVMToBytecode.unsupportedValue(value, "Type " + theClass + " must be an assignable from " + from);
        }
    }

    /**
     *
     * @param value
     * @param methodTypeDef
     * @param typeDef: must not be an inteface since it has a jobject
     */
    private void ensureMemberOf(Value value, MethodTypeDef methodTypeDef, TypeDef typeDef) {
        if (typeDef.isInterface()) {
            throw LLVMToBytecode.unsupportedValue(value, "Must not be an interface " + typeDef);
        }
        TypeDef owner = methodTypeDef.owner();
        if (owner.isInterface()) {
            if (!isSubclassOf(typeDef, owner)) {
                throw LLVMToBytecode.unsupportedValue(value, "Method " + methodTypeDef + " must be a member of " + typeDef);
            }
        } else {
            if (!isSubclassOf(typeDef, owner)) {
                throw LLVMToBytecode.unsupportedValue(value, "Method " + methodTypeDef + " must be a member of " + typeDef);
            }
        }

    }

    private void ensureMemberOf(Value value, FieldTypeDef fieldTypeDef, TypeDef typeDef) {
        if (typeDef.isInterface()) {
            throw LLVMToBytecode.unsupportedValue(value, "Must not be an interface " + typeDef);
        }
        TypeDef owner = fieldTypeDef.owner();
        if (!isSubclassOf(typeDef, owner)) {
            throw LLVMToBytecode.unsupportedValue(value, "Field " + fieldTypeDef + " must be a member of " + typeDef);
        }
    }

    private void checkParameterAccessibleJavaTagOrFail(CallBase inst, MethodTypeDef methodTypeDef, int beginIdx) {
        int totalArgs = inst.getNumArgOperands();
        TypeDef[] argTypes = methodTypeDef.parameterTypes();
        if (totalArgs - beginIdx != argTypes.length) {
            throw LLVMToBytecode.unsupportedValue(inst, "Unmatched args number: expected " + argTypes.length + ", got " + (totalArgs - beginIdx));
        }
        for (int i = 0; i < argTypes.length; i++) {
            TypeDef argType = argTypes[i];
            if (!argType.isPrimitive()) {
                ensureAssignable(inst, argType, inst.getArgOperand(beginIdx + i));
            }
        }
    }

    private void emitFieldAccess(FieldTypeDef fieldTypeDef, int opcode) {
        addInstruction(new FieldInsnNode(opcode, fieldTypeDef.owner().getInternalName(),
                fieldTypeDef.name(), fieldTypeDef.descriptor()));
    }


    private void emitInvokeJava(CallBase inst, MethodTypeDef methodTypeDef, int beginIdx, int opcode) {
        if (methodTypeDef.isVariadic()) {
            throw LLVMToBytecode.unsupportedValue(inst, "Cannot call variadic Java methods");
        }
        // caller must load the receiver
        int totalArgs = inst.getNumArgOperands();
        TypeDef[] argTypes = methodTypeDef.parameterTypes();
        if (totalArgs - beginIdx != argTypes.length) {
            throw LLVMToBytecode.unsupportedValue(inst, "Unmatched args number: expected " + argTypes.length + ", got " + (totalArgs - beginIdx));
        }
        for (int i = 0; i < argTypes.length; i++) {
            TypeDef argType = argTypes[i];
            Value arg = inst.getArgOperand(beginIdx + i);
            JavaKind argKind = javaKind(argType).onStackKind();
            JavaKind valueKind = javaKindOrFail(arg).onStackKind();
            if (!argType.isPrimitive()) {
                // TODO: Do we need a type checking cast?
                // We have manually do assignable check during checkJNIInfo.
                // Here we should not do casting if the check in checkJNIInfo is sound and complete.
                emitReadJavaReference(arg);
            } else {
                emitReadValue(arg);
                if (argKind != valueKind) {
                    // vararg: the value kind may not be the same as the declared kind from Java signature.
                    if (argKind == JavaKind.Float && valueKind == JavaKind.Double) {
                        addInstruction(new InsnNode(Opcodes.D2F));
                    } else {
                        throw new IllegalStateException("Oops..");
                    }
                }
            }
        }
        addInstruction(new MethodInsnNode(opcode, methodTypeDef.owner().getInternalName(), methodTypeDef.name(), methodTypeDef.descriptor()));
    }

    /**
     * Get the TypeDef for a given class descriptor or class name
     * @param classNameOrDescriptor
     * @return
     */
    protected TypeDef getTypeDef(String classNameOrDescriptor) {
        String descriptor;
        if (classNameOrDescriptor.startsWith("[")) {
            descriptor = classNameOrDescriptor;
        } else {
            if (classNameOrDescriptor.endsWith(";")) {
                descriptor = classNameOrDescriptor;
            } else {
                descriptor = "L" + classNameOrDescriptor + ";";
            }
        }
        return universe.getTypeDef(descriptor);
    }

    protected TypeDef getJClassTypeDef(Value value) {
        JavaTag tag = getAccessibleJavaTagOrFail(value);
        if (tag.getType() != JavaTag.TagType.jclass) {
            throw LLVMToBytecode.unsupportedValue(value, "Expected jclass for " + value + ", got " + tag.getType());
        }
        Object data = tag.getData();
        if (!(data instanceof TypeDef)) {
            throw LLVMToBytecode.unsupportedValue(value, "Expected TypeDef for " + tag + ", got " + data);
        }
        TypeDef typeDef = this.jclassToTypeDef.get(value);
        if (typeDef == null) {
            throw LLVMToBytecode.unsupportedValue(value, "Expected TypeDef for jclass " + value);
        }
        return typeDef;
    }

    protected TypeDef getTypeDef(Value value) {
        if (isConstantPointerNull(value)) {
            // TODO: we need a phantom null type.
            return universe.getJavaLangObject();
        }
        JavaTag tag = getAccessibleJavaTagOrFail(value);
        Object data = tag.getData();
        if (!(data instanceof TypeDef)) {
            throw LLVMToBytecode.unsupportedValue(value, "Expected TypeDef for " + tag + ", got " + data);
        }
        return (TypeDef) data;
    }

    protected FieldTypeDef getFieldTypeDef(Value value, TypeDef owner, String name, String desc) {
        FieldTypeDef fieldTypeDef = owner.getFieldTypeDef(name, desc);
        if (fieldTypeDef == null) {
            throw LLVMToBytecode.unsupportedValue(value, "Cannot find field: " + owner.getName() + " " + name + desc);
        }
        return fieldTypeDef;
    }

    protected MethodTypeDef getMethodTypeDef(Value value, TypeDef owner, String name, String desc) {
        MethodTypeDef methodTypeDef = owner.getMethodTypeDef(name, desc);
        if (methodTypeDef == null) {
            throw LLVMToBytecode.unsupportedValue(value, "Cannot get MethodTypeDef for " + owner + "::" + name + desc);
        }
        return methodTypeDef;
    }

    protected FieldTypeDef getFieldTypeDef(Value value) {
        JavaTag tag = getJavaTagOrFail(value);
        if (tag.getType() != JavaTag.TagType.jfieldID) {
            throw LLVMToBytecode.unsupportedValue(value, "Expected jfeildID, got " + tag.getType());
        }
        Object data = tag.getData();
        if (!(data instanceof FieldTypeDef)) {
            throw LLVMToBytecode.unsupportedValue(value, "Expected FieldTypeDef, got " + data);
        }
        return (FieldTypeDef) data;
    }

    protected MethodTypeDef getMethodTypeDef(Value value) {
        JavaTag tag = getJavaTagOrFail(value);
        if (tag.getType() != JavaTag.TagType.jmethodID) {
            throw LLVMToBytecode.unsupportedValue(value, "Expected jmethodID, got " + tag.getType());
        }
        Object data = tag.getData();
        if (!(data instanceof MethodTypeDef)) {
            throw LLVMToBytecode.unsupportedValue(value, "Expected MethodTypeDef, got " + data);
        }
        return (MethodTypeDef) data;
    }

    protected JavaTag getJavaTag(Value value) {
        return this.valueToJavaTag.get(value);
    }

    protected JavaTag getJavaTagOrFail(Value value) {
        JavaTag tag = this.valueToJavaTag.get(value);
        if (tag == null) {
            throw LLVMToBytecode.unsupportedValue(value, "No JavaTag for " + value);
        }
        return tag;
    }

    protected JavaTag getAccessibleJavaTagOrFail(Value value) {
        JavaTag tag = this.valueToJavaTag.get(value);
        if (tag == null) {
            throw LLVMToBytecode.unsupportedValue(value, "No JavaTag for " + value);
        }
        if (!tag.isAccessible()) {
            throw LLVMToBytecode.unsupportedValue(value, "Not an accessible JavaTag for " + value);
        }
        return tag;
    }

    protected JavaTag mapJavaTag(Value value, TypeDef typeDef) {
        if (typeDef.isPrimitive()) {
            throw new IllegalStateException("Oops.");
        }
        if (universe.isJavaLangClass(typeDef)) {
            // the value itself is a jclass but we have no idea of what the class is
            // and have to assume it is the java.lang.Object;
            return mapJavaTagForJClass(value, universe.getJavaLangObject());
        } else {
            if (typeDef.isArray()) {
                TypeDef componentType = typeDef.getComponentTypeDef();
                if (componentType.isPrimitive()) {
                    String desc = componentType.getDescriptor();
                    switch (desc) {
                        case "Z":
                            return mapJavaTag(value, JavaTag.jbooleanArray(value));
                        case "B":
                            return mapJavaTag(value, JavaTag.jbyteArray(value));
                        case "C":
                            mapJavaTag(value, JavaTag.jcharArray(value));
                        case "S":
                            return mapJavaTag(value, JavaTag.jshortArray(value));
                        case "I":
                            return mapJavaTag(value, JavaTag.jintArray(value));
                        case "L":
                            return mapJavaTag(value, JavaTag.jlongArray(value));
                        case "F":
                            return mapJavaTag(value, JavaTag.jfloatArray(value));
                        case "D":
                            return mapJavaTag(value, JavaTag.jdoubleArray(value));
                        default:
                            throw new IllegalStateException("Oops: not a valid return type: " + typeDef);
                    }
                } else {
                    return mapJavaTag(value, JavaTag.objectArray(value, componentType));
                }
            } else if (universe.isJavaLangString(typeDef)) {
                return mapJavaTag(value, JavaTag.jstring(value, typeDef));
            } else {
                return mapJavaTag(value, JavaTag.jobject(value, typeDef));
            }
        }
    }

    protected JavaTag mapJavaTag(Value value, JavaTag tag) {
        Logger.debug("Mapping JavaTag " + tag.getType() + " to " + value);
        if (value instanceof Constant) {
            throw new IllegalStateException("Oops: cannot map a Java tag to a constant");
        }
        JavaTag check = this.valueToJavaTag.get(value);
        if (check != null) {
            tag = mergeJavaTag(value, check, tag);
        }
        this.valueToJavaTag.put(value, tag);
        return tag;
    }

    /**
     * The TypeDef for a jclass tag is always JavaLangClass
     * @param value
     * @param typeDef
     * @return
     */
    protected JavaTag mapJavaTagForJClass(Value value, TypeDef typeDef) {
        Logger.debug("Mapping JavaTag jclass to " + value + ", where jclass represents " + typeDef);
        JavaTag tag = JavaTag.jclass(value, universe.getJavaLangClass());
        tag = mapJavaTag(value, tag);
        if (typeDef != null) {
            TypeDef check = this.jclassToTypeDef.put(value, typeDef);
            if (check != null && !check.equals(typeDef)) {
                throw LLVMToBytecode.unsupportedValue(value, "TODO: we only support 1-1 mapping now: got " + check + ", expected " + typeDef);
            }
        }
        return tag;
    }



    static int MAX_MAX_LOCAL = (2 << 16 - 1); // a max local is a u2

    protected int allocateLocalIndex(Value value) {
        return allocateLocalIndex(value, javaKindOrFail(value));
    }

    protected int allocateLocalIndex(Value value, JavaKind kind) {
        int localIndex = unallocatedLocalIndex;
        if (localIndex >= MAX_MAX_LOCAL) {
            throw new UnsupportedIRException(value, "Maximum local index reached");
        }
        valueToLocalIndex.put(value, localIndex);
        switch (kind) {
            case Boolean: // I1
            case Byte: // I8
            case Short: // I16
            case Character: // I16 or argument from Java char
            case Integer: // I32
                unallocatedLocalIndex += 1;
                break;
            case Float:
                unallocatedLocalIndex += 1;
                break;
            case Double:
                unallocatedLocalIndex += 2;
                break;
            case Long:
                unallocatedLocalIndex += 2;
                break;
            case Object:
            case Array:
                unallocatedLocalIndex +=1;
                break;
            default:
                throw new UnsupportedIRException(value,  "unsupported type/Java kind: " + kind);
        }

        return localIndex;
    }

    protected abstract int getStackBasePointerLocalIndex();

    protected int getJavaLocalIndex(Value value) {
        Integer index = valueToLocalIndex.get(value);
        if (index == null) {
            return allocateLocalIndex(value);
        }
        return index;
    }

    protected DataLayout getDataLayout() {
        return universe.getModule().getDataLayout();
    }

    private void processPHINode(BasicBlock from, BasicBlock to) {
        {
        Iterator<BasicBlock.InstList.Iter> iter = to.getInstList().iterator();
            Instruction inst;
            while (iter.hasNext() && (inst = iter.next().get()) instanceof PHINode) {
                PHINode phiNode = (PHINode) inst;
                int numIncomingValues = phiNode.getNumIncomingValues();
                Value value = null;
                for (int i = 0; i < numIncomingValues; i++) {
                    if (from.equals(phiNode.getIncomingBlock(i))) {
                        value = phiNode.getIncomingValue(i);
                        break;
                    }
                }

                if (value == null) {
                    continue;
                }

                if (!isJavaAccessible(value)) {
                    for (int i = 0; i < numIncomingValues; i++) {
                        Value other = phiNode.getIncomingValue(i);
                        if (isJavaAccessible(other)) {
                            throw LLVMToBytecode.unsupportedValue(phiNode, "A PHINode depends on some Java inaccessible value but not all.");
                        }
                    }
                    continue;
                }

                emitReadValue(value);
                emitWriteValue(phiNode);
            }
        }
    }

    protected void doVisit(Instruction inst) {
        Opcode opcode = inst.getOpcode();
        switch (opcode) {
            case Ret:
                emitReturnInst((ReturnInst) inst);
                break;
            case Br:
                emitBranchInst((BranchInst) inst);
                break;
            case Switch:
                emitSwitchInst((SwitchInst) inst);
                break;
            case IndirectBr:
                emitIndirectBrInst((IndirectBrInst) inst);
                break;
            case Invoke:
                emitInvokeInst((InvokeInst) inst);
                break;
            case Resume:
                emitResumeInst((ResumeInst) inst);
                break;
            case Unreachable:
                emitUnreachableInst((UnreachableInst) inst);
                break;
            case CleanupRet:
            case CatchRet:
            case CatchSwitch:
            case CallBr:
                throw new UnsupportedIRException(inst);
            case FNeg:
                emitUnaryOperator((UnaryOperator) inst);
                break;
            case Add:
            case FAdd:
            case Sub:
            case FSub:
            case Mul:
            case FMul:
            case UDiv:
            case SDiv:
            case FDiv:
            case URem:
            case SRem:
            case FRem:
            case Shl:
            case LShr:
            case AShr:
            case And:
            case Or:
            case Xor:
                emitBinaryOperator((BinaryOperator) inst);
                break;
            case Alloca:
                emitAllocaInst((AllocaInst) inst);
                break;
            case Load:
                emitLoadInst((LoadInst) inst);
                break;
            case Store:
                emitStoreInst((StoreInst) inst);
                break;
            case GetElementPtr:
                emitGetElementPtrInst((GetElementPtrInst) inst);
                break;
            case Fence:
            case AtomicCmpXchg:
                emitAtomicCmpXchgInst((AtomicCmpXchgInst) inst);
            case AtomicRMW:
                emitAtomicRMWInst((AtomicRMWInst) inst);
                break;
            case Trunc:
                emitTruncInst((TruncInst) inst);
                break;
            case ZExt:
                emitZExtInst((ZExtInst) inst);
                break;
            case SExt:
                emitSExtInst((SExtInst) inst);
                break;
            case FPToUI:
                emitFPToUIInst((FPToUIInst) inst);
                break;
            case FPToSI:
                emitFPToSIInst((FPToSIInst) inst);
                break;
            case UIToFP:
                emitUIToFPInst((UIToFPInst) inst);
                break;
            case SIToFP:
                emitSIToFPInst((SIToFPInst) inst);
                break;
            case FPTrunc:
                emitFPTruncInst((FPTruncInst) inst);
                break;
            case FPExt:
                emitFPExtInst((FPExtInst) inst);
                break;
            case PtrToInt:
                emitPtrToIntInst((PtrToIntInst) inst);
                break;
            case IntToPtr:
                emitIntToPtrInst((IntToPtrInst) inst);
                break;
            case BitCast:
                emitBitCastInst((BitCastInst) inst);
                break;
            case AddrSpaceCast:
            case CleanupPad:
            case CatchPad:
                throw new UnsupportedOperationException();
            case ICmp:
                emitICmpInst((ICmpInst) inst);
                break;
            case FCmp:
                emitFCmpInst((FCmpInst) inst);
                break;
            case PHI:
                break;
            case Call:
                emitCallInst((CallInst) inst);
                break;
            case Select:
                emitSelectInst((SelectInst) inst);
                break;
            case UserOp1:
            case UserOp2:
            case VAArg:
            case ExtractElement:
            case InsertElement:
            case ShuffleVector:
            case ExtractValue:
            case InsertValue:
            case LandingPad:
            case Freeze:
            default:
                throw LLVMToBytecode.unsupportedValue(inst, "TODO: unknown Instruction " + inst.getOpcode());
        }
    }

    private void emitAtomicCmpXchgInst(AtomicCmpXchgInst inst) {
        // TODO: the return value of cmpxchg is a tuple
        throw LLVMToBytecode.unsupportedValue(inst, "TODO: cmpxchg is not supported");
    }

    private void emitAtomicRMWInst(AtomicRMWInst inst) {
        AtomicRMWInst.BinOp binOp = inst.getOperation();
        emitReadValue(inst.getPointerOperand());
        String desc;
        Value value = inst.getValOperand();
        JavaKind kind = javaKindOrFail(value);
        switch (kind) {
            case Integer:
                desc = "(II)I";
                break;
            case Long:
                desc = "(JJ)J";
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst, "Unsupported Java value: " + kind);
        }
        emitReadValue(value);
        String name;
        switch (binOp) {
            case Xchg:
                name = "atomic_xchg";
                break;
            case Add:
                name = "atomic_add";
                break;
            case Sub:
                name = "atomic_sub";
                break;
            case And:
                name = "atomic_and";
                break;
            case Or:
                name = "atomic_or";
                break;
            case Xor:
                name = "atomic_xor";
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst, "Unsupported AtomicRMWInst");
        }
        emitCallJavaRuntime(name, desc);
    }

    private void emitAllocaInst(AllocaInst inst) {
        if (!inst.isStaticAlloca()) {
            throw LLVMToBytecode.unsupportedValue(inst, "Cannot support dynamic alloca");
        }
        long offset = getOrAllocateAllocaOffset(inst);
        emitLdc(offset);
        emitLoadBasePointer();
        addInstruction(new InsnNode(Opcodes.LADD));
    }

    protected void emitLoadBasePointer() {
        int spIndex = getStackBasePointerLocalIndex();
        addInstruction(new VarInsnNode(Opcodes.LLOAD, spIndex));
    }

    /**
     * Return false when the value has an inaccessiable JavaTag.
     * @param value
     * @return
     */
    protected boolean isJavaAccessible(Value value) {
        JavaTag javaTag = getJavaTag(value);
        if (javaTag != null) {
            return javaTag.isAccessible();
        }
        return true;
    }

    protected boolean skipInstruction(Instruction inst) {
        if (!isJavaAccessible(inst)) {
            return true;
        }
        return false;
    }

    protected void visit(Instruction inst) {
        Logger.debug("Visit Instruction: " + inst.getOpcode());
        if (skipInstruction(inst)) {
            return;
        }
        if (inst instanceof PHINode) {
            return;
        }
        preVisit(inst);
        doVisit(inst);
        postVisit(inst);
    }

    protected void preVisit(Instruction inst) {

    }

    protected void postVisit(Instruction inst) {
        emitWriteValue(inst);
    }

    protected void emitCallLibraryMethod(LibraryClass libraryClass, LibraryMethod libraryMethod) {
        MethodNode methodNode = libraryMethod.methodNode;
        addInstruction(new MethodInsnNode(Opcodes.INVOKESTATIC, libraryClass.getName(), methodNode.name,
                methodNode.desc));
    }

    protected void emitReadValue(Value value) {
        JavaKind kind = javaKindOrFail(value);
        JavaTag tag = getJavaTag(value);

        {
            if (tag != null) {
                if (!tag.isAccessible()) {
                    throw new IllegalStateException("Oops, attempt to read a JavaTag " + tag + " for " + value);
                }
                Logger.debug("Read Tag " + tag);
                kind = tag.isArray() ? JavaKind.Array : JavaKind.Object;
            }
        }

        if (value instanceof Instruction || value instanceof Argument) {
            int varIndex = getJavaLocalIndex(value);
            switch (kind) {
                case Boolean:
                case Byte:
                case Short:
                case Integer:
                    addInstruction(new VarInsnNode(Opcodes.ILOAD, varIndex));
                    break;
                case Float:
                    addInstruction(new VarInsnNode(Opcodes.FLOAD, varIndex));
                    break;
                case Long:
                    addInstruction(new VarInsnNode(Opcodes.LLOAD, varIndex));
                    break;
                case Double:
                    addInstruction(new VarInsnNode(Opcodes.DLOAD, varIndex));
                    break;
                case Object:
                case Array:
                    addInstruction(new VarInsnNode(Opcodes.ALOAD, varIndex));
                    break;
                default:
                    throw LLVMToBytecode.unsupportedValue(value);
            }
            return;
        } else if (value instanceof ConstantInt) {
            switch (kind) {
                case Boolean: {
                    byte byteValue = (byte) ((ConstantInt) value).getZExtValue();
                    if (byteValue == 0) {
                        emitFalse();
                    } else {
                        emitTrue();
                    }
                    break;
                }
                case Byte:
                case Short:
                case Integer: {
                    int intValue = (int) ((ConstantInt) value).getZExtValue();
                    emitLdc(intValue);
                    break;
                }
                case Long: {
                    long longValue = ((ConstantInt) value).getZExtValue();
                    emitLdc(longValue);
                    break;
                }
                default:
                    throw LLVMToBytecode.unsupportedValue(value);
            }
            return;
        } else if (value instanceof ConstantFP) {
            switch (kind) {
                case Float: {
                    emitLdc(((ConstantFP) value).getFloat());
                    break;
                }
                case Double: {
                    emitLdc(((ConstantFP) value).getDouble());
                    break;
                }
                default:
                    throw LLVMToBytecode.unsupportedValue(value);
            }
            return;
        } else if (value instanceof ConstantPointerNull) {
            if (tag != null) {
                emitNull();
                return;
            }
            emitLdc(0L);
            return;
        } else if (value instanceof ConstantExpr) {
            ConstantExpr constantExpr = (ConstantExpr) value;
            Instruction inst = constantExpr.getAsInstruction();
            visit(inst);
            emitReadValue(inst);
        } else if (value instanceof GlobalVariable) {
            GlobalVariable globalVariable = (GlobalVariable) value;
            emitReadGlobalVariable(globalVariable);
        } else if (value instanceof Function) {
            Function function = (Function) value;
            LibraryClass libraryClass = universe.getLibraryClass();
            LibraryMethod libraryMethod = libraryClass.getOrCreateGetFunctionPointerMethod(function);
            emitCallLibraryMethod(libraryClass, libraryMethod);
            return;
        } else {
            throw LLVMToBytecode.unsupportedValue(value);
        }
    }

    private void emitReadGlobalVariable(GlobalVariable globalVariable) {
        if (universe.isAvailableInELF(globalVariable)) {
            LibraryClass libraryClass = universe.getLibraryClass();
            FieldNode fieldNode = libraryClass.getOrCreateGlobalField(globalVariable);
            addInstruction(new FieldInsnNode(Opcodes.GETSTATIC, libraryClass.getName(), fieldNode.name, fieldNode.desc));
            addInstruction(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Utils.getBinaryName(RuntimeGlobal.class), "getPointer", "()J"));
            return;
        }

        if (!options.supportLocalConstant()) {
            throw LLVMToBytecode.unsupportedValue(globalVariable);
        }

        if (globalVariable.isConstant() && globalVariable.hasAtLeastLocalUnnamedAddr()) {
            LibraryClass libraryClass = universe.getLibraryClass();
            FieldNode fieldNode = libraryClass.getOrCreateGlobalConstantField(globalVariable);
            addInstruction(new FieldInsnNode(Opcodes.GETSTATIC, libraryClass.getName(), fieldNode.name, fieldNode.desc));
            addInstruction(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Utils.getBinaryName(RuntimeGlobal.class), "getPointer", "()J"));
            return;
        } else {
            throw LLVMToBytecode.unsupportedValue(globalVariable);
        }
    }


    protected void emitLdc(Object constant) {
        addInstruction(new LdcInsnNode(constant));
    }

    private void emitWriteValue(Value value) {
        if (!(value instanceof Instruction)) {
            return;
        }

        JavaKind kind = javaKindOrFail(value);
        if (kind == JavaKind.Void) {
            return;
        }
        int varIndex = getJavaLocalIndex(value);
        switch (kind) {
            case Boolean:
            case Byte:
            case Short:
            case Integer:
                addInstruction(new VarInsnNode(Opcodes.ISTORE, varIndex));
                break;
            case Float:
                addInstruction(new VarInsnNode(Opcodes.FSTORE, varIndex));
                break;
            case Long:
                addInstruction(new VarInsnNode(Opcodes.LSTORE, varIndex));
                break;
            case Double:
                addInstruction(new VarInsnNode(Opcodes.DSTORE, varIndex));
                break;
            case Object:
            case Array:
                addInstruction(new VarInsnNode(Opcodes.ASTORE, varIndex));
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(value);
        }
    }

    private void emitStoreInst(StoreInst inst) {
        if (inst.isVolatile()) {
            throw LLVMToBytecode.unsupportedValue(inst);
        }
        Value value = inst.getValueOperand();
        Type type = value.getType();
        // A value may have a JavaTag
        JavaKind typeKind = javaKindOrFail(type);
        JavaKind valueKind = javaKindOrFail(value);
        if (typeKind != valueKind) {
            throw LLVMToBytecode.unsupportedValue(inst, "Cannot store a annotated Java type into native memory");
        }
        emitReadValue(inst.getPointerOperand());
        emitReadValue(value);
        emitStoreKind(inst, type, valueKind);
    }

    protected void emitStoreKind(StoreInst inst, Type type, JavaKind valueKind) {
        String name, desc;
        switch (valueKind) {
            case Boolean:
                name = "putBoolean";
                desc = "(JZ)V";
                break;
            case Byte:
                name = "putByte";
                desc = "(JB)V";
                break;
            case Short:
                name = "putShort";
                desc = "(JS)V";
                break;
            case Integer:
                name = "putInt";
                desc = "(JI)V";
                break;
            case Float:
                name = "putFloat";
                desc = "(JF)V";
                break;
            case Long:
                name = "putLong";
                desc = "(JJ)V";
                break;
            case Double:
                name = "putDouble";
                desc = "(JD)V";
                break;
            default:
                throw LLVMToBytecode.unsupportedType(type, "unsupported JavaKind: " + valueKind + " for store");
        }
        emitCallJavaRuntime(name, desc);
    }

    protected void emitStoreKind(Type type, JavaKind valueKind) {
        String name, desc;
        switch (valueKind) {
            case Boolean:
                name = "putBoolean";
                desc = "(JZ)V";
                break;
            case Byte:
                name = "putByte";
                desc = "(JB)V";
                break;
            case Short:
                name = "putShort";
                desc = "(JS)V";
                break;
            case Integer:
                name = "putInt";
                desc = "(JI)V";
                break;
            case Float:
                name = "putFloat";
                desc = "(JF)V";
                break;
            case Long:
                name = "putLong";
                desc = "(JJ)V";
                break;
            case Double:
                name = "putDouble";
                desc = "(JD)V";
                break;
            default:
                throw LLVMToBytecode.unsupportedType(type, "unsupported JavaKind: " + valueKind + " for store");
        }
        emitCallJavaRuntime(name, desc);
    }

    private void emitLoadInst(LoadInst inst) {
        if (inst.isVolatile()) {
            throw LLVMToBytecode.unsupportedValue(inst, "TODO: support load volatile load inst");
        }
        Value pointer = inst.getPointerOperand();
        emitReadValue(pointer);
        PointerType pointerType = (PointerType) pointer.getType();
        Type elementType = pointerType.getElementType();
        JavaKind valueKind = javaKindOrFail(elementType);
        String name, desc;
        switch (valueKind) {
            case Boolean:
                name = "getBoolean";
                desc = "(J)Z";
                break;
            case Byte:
                name = "getByte";
                desc = "(J)B";
                break;
            case Short:
                name = "getShort";
                desc = "(J)S";
                break;
            case Integer:
                name = "getInt";
                desc = "(J)I";
                break;
            case Float:
                name = "getFloat";
                desc = "(J)F";
                break;
            case Long:
                name = "getLong";
                desc = "(J)J";
                break;
            case Double:
                name = "getDouble";
                desc = "(J)D";
                break;
            default:
                throw LLVMToBytecode.unsupportedType(elementType, "unsupported JavaKind: " + valueKind + " for load");

        }
        emitCallJavaRuntime(name, desc);
    }

    private void emitBinaryOperator(BinaryOperator inst) {
        Value lhs = inst.getOperand(0);
        Value rhs = inst.getOperand(1);
        emitReadValue(lhs);
        emitReadValue(rhs);
        JavaKind kind = sameJavaKindOrFail(lhs, rhs);
        switch (kind) {
            case Boolean:
                // TODO
                emitBooleanBinaryOperator(inst, kind, lhs, rhs);
                return;
            case Byte:
                emitIntegerBinaryOperator(inst, kind, lhs, rhs);
                addInstruction(new InsnNode(Opcodes.I2B));
                return;
            case Short:
                emitIntegerBinaryOperator(inst, kind, lhs, rhs);
                addInstruction(new InsnNode(Opcodes.I2S));
                return;
            case Integer:
                emitIntegerBinaryOperator(inst, kind, lhs, rhs);
                return;
            case Float:
                emitFloatBinaryOperator(inst, lhs, rhs);
                return;
            case Long:
                emitLongBinaryOperator(inst, lhs, rhs);
                return;
            case Double:
                emitDoubleBinaryOperator(inst, kind, lhs, rhs);
                return;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    private void emitBooleanBinaryOperator(BinaryOperator inst, JavaKind kind, Value lhs, Value rhs) {
        Opcode opcode = inst.getOpcode();
        switch (opcode) {
            case And:
                addInstruction(new InsnNode(Opcodes.IAND));
                return;
            case Or:
                addInstruction(new InsnNode(Opcodes.IOR));
                return;
            case Xor:
                addInstruction(new InsnNode(Opcodes.IXOR));
                return;
            default:
                throw LLVMToBytecode.unsupportedValue(inst, "cannot apply the binary operator to boolean in Java");
        }
    }

    private void emitIntegerBinaryOperator(BinaryOperator inst, JavaKind kind, Value lhs, Value rhs) {
        Opcode opcode = inst.getOpcode();
        String desc;
        switch (kind) {
            case Byte:
                desc = "(BB)B";
                break;
            case Short:
                desc = "(SS)S";
                break;
            case Integer:
                desc = "(II)I";
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
        switch (opcode) {
            case Add:
                addInstruction(new InsnNode(Opcodes.IADD));
                return;
            case Sub:
                addInstruction(new InsnNode(Opcodes.ISUB));
                return;
            case Mul:
                addInstruction(new InsnNode(Opcodes.IMUL));
                return;
            case UDiv:
                emitCallJavaRuntime("udiv", desc);
                return;
            case SDiv:
                addInstruction(new InsnNode(Opcodes.IDIV));
                return;
            case URem:
                emitCallJavaRuntime("urem", desc);
                return;
            case SRem:
                addInstruction(new InsnNode(Opcodes.IREM));
                return;
            case Shl:
                addInstruction(new InsnNode(Opcodes.ISHL));
                return;
            case LShr:
                addInstruction(new InsnNode(Opcodes.IUSHR));
                return;
            case AShr:
                addInstruction(new InsnNode(Opcodes.ISHR));
                return;
            case And:
                addInstruction(new InsnNode(Opcodes.IAND));
                return;
            case Or:
                addInstruction(new InsnNode(Opcodes.IOR));
                return;
            case Xor:
                addInstruction(new InsnNode(Opcodes.IXOR));
                return;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    private void emitFloatBinaryOperator(BinaryOperator inst, Value lhs, Value rhs) {
        Opcode opcode = inst.getOpcode();
        switch (opcode) {
            case FAdd:
                addInstruction(new InsnNode(Opcodes.FADD));
                return;
            case FSub:
                addInstruction(new InsnNode(Opcodes.FSUB));
                return;
            case FMul:
                addInstruction(new InsnNode(Opcodes.FMUL));
                return;
            case FDiv:
                addInstruction(new InsnNode(Opcodes.FDIV));
                return;
            case FRem:
                addInstruction(new InsnNode(Opcodes.FREM));
                return;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    private void emitLongBinaryOperator(BinaryOperator inst, Value lhs, Value rhs) {
        Opcode opcode = inst.getOpcode();
        switch (opcode) {
            case Add:
                addInstruction(new InsnNode(Opcodes.LADD));
                return;
            case Sub:
                addInstruction(new InsnNode(Opcodes.LSUB));
                return;
            case Mul:
                addInstruction(new InsnNode(Opcodes.LMUL));
                return;
            case UDiv:
                emitCallJavaRuntime("udiv", "(JJ)J");
                return;
            case SDiv:
                addInstruction(new InsnNode(Opcodes.LDIV));
                return;
            case URem:
                emitCallJavaRuntime("urem", "(JJ)J");
                return;
            case SRem:
                addInstruction(new InsnNode(Opcodes.LREM));
                return;
            case Shl:
                addInstruction(new InsnNode(Opcodes.L2I));
                addInstruction(new InsnNode(Opcodes.LSHL));
                return;
            case LShr:
                addInstruction(new InsnNode(Opcodes.L2I));
                addInstruction(new InsnNode(Opcodes.LUSHR));
                return;
            case AShr:
                addInstruction(new InsnNode(Opcodes.L2I));
                addInstruction(new InsnNode(Opcodes.LSHR));
                return;
            case And:
                addInstruction(new InsnNode(Opcodes.LAND));
                return;
            case Or:
                addInstruction(new InsnNode(Opcodes.LOR));
                return;
            case Xor:
                addInstruction(new InsnNode(Opcodes.LXOR));
                return;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    private void emitDoubleBinaryOperator(BinaryOperator inst, JavaKind kind, Value lhs, Value rhs) {
        Opcode opcode = inst.getOpcode();
        switch (opcode) {
            case FAdd:
                addInstruction(new InsnNode(Opcodes.DADD));
                return;
            case FSub:
                addInstruction(new InsnNode(Opcodes.DSUB));
                return;
            case FMul:
                addInstruction(new InsnNode(Opcodes.DMUL));
                return;
            case FDiv:
                addInstruction(new InsnNode(Opcodes.DDIV));
                return;
            case FRem:
                addInstruction(new InsnNode(Opcodes.DREM));
                return;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    private void emitTruncInst(TruncInst inst) {
        JavaKind srcKind = javaKindOrFail(inst.getSrcTy());
        JavaKind dstKind = javaKindOrFail(inst.getDestTy());
        emitReadValue(inst.getOperand(0));
        switch (dstKind) {
            case Boolean: {
                switch (srcKind) {
                    case Byte:
                    case Short:
                    case Integer:
                        addInstruction(new InsnNode(Opcodes.ICONST_1));
                        addInstruction(new InsnNode(Opcodes.IAND));
                        return;
                    case Long:
                        addInstruction(new InsnNode(Opcodes.LCONST_1));
                        addInstruction(new InsnNode(Opcodes.LAND));
                        addInstruction(new InsnNode(Opcodes.L2I));
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Byte: {
                switch (srcKind) {
                    case Short:
                    case Integer:
                        addInstruction(new InsnNode(Opcodes.I2B));
                        return;
                    case Long:
                        addInstruction(new InsnNode(Opcodes.L2I));
                        addInstruction(new InsnNode(Opcodes.I2B));
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Short: {
                switch (srcKind) {
                    case Integer:
                        addInstruction(new InsnNode(Opcodes.I2S));
                        return;
                    case Long:
                        addInstruction(new InsnNode(Opcodes.L2I));
                        addInstruction(new InsnNode(Opcodes.I2S));
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Integer: {
                switch (srcKind) {
                    case Long:
                        addInstruction(new InsnNode(Opcodes.L2I));
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Long: {
                switch (srcKind) {
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    private void emitZExtInst(ZExtInst inst) {
        JavaKind srcKind = javaKindOrFail(inst.getSrcTy());
        JavaKind dstKind = javaKindOrFail(inst.getDestTy());
        emitReadValue(inst.getOperand(0));
        switch (srcKind) {
            case Boolean: {
                switch (dstKind) {
                    case Byte:
                    case Short:
                    case Integer:
                        emitI1ToByte(false, false);
                        return;
                    case Long:
                        emitI1ToByte(false, true);;
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Byte: {
                switch (dstKind) {
                    case Short:
                    case Integer:
                        emitLdc(JavaRuntime.I8_MASK);
                        addInstruction(new InsnNode(Opcodes.IAND));
                        return;
                    case Long:
                        emitLdc(JavaRuntime.I8_MASK);
                        addInstruction(new InsnNode(Opcodes.IAND));
                        addInstruction(new InsnNode(Opcodes.I2L));
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Short: {
                switch (dstKind) {
                    case Integer:
                        emitLdc(JavaRuntime.I16_MASK);
                        addInstruction(new InsnNode(Opcodes.IAND));
                        return;
                    case Long:
                        emitLdc(JavaRuntime.I16_MASK);
                        addInstruction(new InsnNode(Opcodes.IAND));
                        addInstruction(new InsnNode(Opcodes.I2L));
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Integer: {
                switch (dstKind) {
                    case Long:
                        addInstruction(new InsnNode(Opcodes.I2L));
                        emitLdc(JavaRuntime.I32_MASK);
                        addInstruction(new InsnNode(Opcodes.LAND));
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Long: {
                switch (dstKind) {
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    /**
     * The store size of i1 is 8 bits.
     * An i1 value would be 0x00 or 0x01.
     * @param signed
     * @param isLong
     */
    private void emitI1ToByte(boolean signed, boolean isLong) {
        LabelNode trueLabel = new LabelNode();
        JumpInsnNode ifop = new JumpInsnNode(Opcodes.IFNE, trueLabel);
        addInstruction(ifop);
        if (isLong) {
            addInstruction(new InsnNode(Opcodes.LCONST_0));
        } else {
            addInstruction(new InsnNode(Opcodes.ICONST_0));
        }
        LabelNode endLabel = new LabelNode();
        JumpInsnNode goto_ = new JumpInsnNode(Opcodes.GOTO, endLabel);
        addInstruction(goto_);
        addInstruction(trueLabel);
        if (signed) {
            if (isLong) {
                emitLdc(-1L);
            } else {
                addInstruction(new InsnNode(Opcodes.ICONST_M1));
            }
        } else {
            if (isLong) {
                addInstruction(new InsnNode(Opcodes.LCONST_1));
            } else {
                addInstruction(new InsnNode(Opcodes.ICONST_1));
            }
        }
        addInstruction(endLabel);
    }

    private void emitSExtInst(SExtInst inst) {
        JavaKind srcKind = javaKindOrFail(inst.getSrcTy());
        JavaKind dstKind = javaKindOrFail(inst.getDestTy());
        emitReadValue(inst.getOperand(0));
        switch (srcKind) {
            case Boolean: {
                switch (dstKind) {
                    case Byte:
                    case Short:
                    case Integer:
                        emitI1ToByte(true, false);
                        return;
                    case Long:
                        emitI1ToByte(true, true);;
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Byte: {
                switch (dstKind) {
                    case Short:
                    case Integer:
                        return;
                    case Long:
                        addInstruction(new InsnNode(Opcodes.I2L));
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Short: {
                switch (dstKind) {
                    case Integer:
                        return;
                    case Long:
                        addInstruction(new InsnNode(Opcodes.I2L));
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Integer: {
                switch (dstKind) {
                    case Long:
                        addInstruction(new InsnNode(Opcodes.I2L));
                        return;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            case Long: {
                switch (dstKind) {
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
            }
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    private void emitFPToSIInst(FPToSIInst inst) {
        JavaKind srcKind = javaKindOrFail(inst.getSrcTy());
        JavaKind dstKind = javaKindOrFail(inst.getDestTy());
        assertJavaKind(srcKind, inst.getOperand(0));
        emitReadValue(inst.getOperand(0));
        if (srcKind == JavaKind.Double) {
            switch (dstKind) {
                case Boolean:
                    addInstruction(new InsnNode(Opcodes.D2I));
                    emitCompare(Opcodes.IFNE); // true when the value is not zero
                    return;
                case Byte:
                    addInstruction(new InsnNode(Opcodes.D2I));
                    addInstruction(new InsnNode(Opcodes.I2B));
                    return;
                case Short:
                    addInstruction(new InsnNode(Opcodes.D2I));
                    addInstruction(new InsnNode(Opcodes.I2S));
                    return;
                case Integer:
                    addInstruction(new InsnNode(Opcodes.D2I));
                    return;
                case Long:
                    addInstruction(new InsnNode(Opcodes.D2L));
                    return;
                default:
                    throw LLVMToBytecode.unsupportedValue(inst);
            }
        }
        if (srcKind == JavaKind.Float) {
            switch (dstKind) {
                case Boolean:
                    addInstruction(new InsnNode(Opcodes.F2I));
                    emitCompare(Opcodes.IFNE); // true when the value is not zero
                    return;
                case Byte:
                    addInstruction(new InsnNode(Opcodes.F2I));
                    addInstruction(new InsnNode(Opcodes.I2B));
                    return;
                case Short:
                    addInstruction(new InsnNode(Opcodes.F2I));
                    addInstruction(new InsnNode(Opcodes.I2S));
                    return;
                case Integer:
                    addInstruction(new InsnNode(Opcodes.F2I));
                    return;
                case Long:
                    addInstruction(new InsnNode(Opcodes.F2L));
                    return;
                default:
                    throw LLVMToBytecode.unsupportedValue(inst);
            }
        }
        throw LLVMToBytecode.unsupportedValue(inst);
    }

    private void emitFPToUIInst(FPToUIInst inst) {
        JavaKind srcKind = javaKindOrFail(inst.getSrcTy());
        JavaKind dstKind = javaKindOrFail(inst.getDestTy());
        assertJavaKind(srcKind, inst.getOperand(0));
        emitReadValue(inst.getOperand(0));
        if (srcKind == JavaKind.Double) {
            switch (dstKind) {
                case Boolean:
                    emitCallJavaRuntime("fptoui_dtoi1", "(D)Z");
                    return;
                case Byte:
                    emitCallJavaRuntime("fptoui_dtoi8", "(D)B");
                    return;
                case Short:
                    emitCallJavaRuntime("fptoui_dtoi16", "(D)S");
                    return;
                case Integer:
                    emitCallJavaRuntime("fptoui_dtoi32", "(D)I");
                    return;
                case Long:
                    emitCallJavaRuntime("fptoui_dtoi64", "(D)J");
                    return;
                default:
                    throw LLVMToBytecode.unsupportedValue(inst);
            }
        }
        if (srcKind == JavaKind.Float) {
            switch (dstKind) {
                case Boolean:
                    emitCallJavaRuntime("fptoui_ftoi1", "(F)Z");
                    return;
                case Byte:
                    emitCallJavaRuntime("fptoui_ftoi8", "(F)B");
                    return;
                case Short:
                    emitCallJavaRuntime("fptoui_ftoi16", "(F)S");
                    return;
                case Integer:
                    emitCallJavaRuntime("fptoui_ftoi32", "(F)I");
                    return;
                case Long:
                    emitCallJavaRuntime("fptoui_ftoi64", "(F)J");
                    return;
                default:
                    throw LLVMToBytecode.unsupportedValue(inst);
            }
        }
        throw LLVMToBytecode.unsupportedValue(inst);
    }

    private void emitUIToFPInst(UIToFPInst inst) {
        JavaKind srcKind = javaKindOrFail(inst.getSrcTy());
        JavaKind dstKind = javaKindOrFail(inst.getDestTy());
        emitReadValue(inst.getOperand(0));
        if (dstKind == JavaKind.Double) {
            switch (srcKind) {
                case Boolean:
                    emitCallJavaRuntime("uitofp_i1tod", "(Z)D");
                    return;
                case Byte:
                    emitCallJavaRuntime("uitofp_i8tod", "(B)D");
                    return;
                case Short:
                    emitCallJavaRuntime("uitofp_i16tod", "(S)D");
                    return;
                case Integer:
                    emitCallJavaRuntime("uitofp_i32tod", "(I)D");
                    return;
                case Long:
                    emitCallJavaRuntime("uitofp_i64tod", "(J)D");
                    return;
                default:
                    throw LLVMToBytecode.unsupportedValue(inst);
            }
        }
        if (dstKind == JavaKind.Float) {
            switch (srcKind) {
                case Boolean:
                    emitCallJavaRuntime("uitofp_i1tof", "(Z)F");
                    return;
                case Byte:
                    emitCallJavaRuntime("uitofp_i8tof", "(B)F");
                    return;
                case Short:
                    emitCallJavaRuntime("uitofp_i16tof", "(S)F");
                    return;
                case Integer:
                    emitCallJavaRuntime("uitofp_i32tof", "(I)F");
                    return;
                case Long:
                    emitCallJavaRuntime("uitofp_i64tof", "(J)F");
                    return;
                default:
                    throw LLVMToBytecode.unsupportedValue(inst);
            }
        }
        throw LLVMToBytecode.unsupportedValue(inst);
    }

    private void emitSIToFPInst(SIToFPInst inst) {
        JavaKind srcKind = javaKindOrFail(inst.getSrcTy());
        JavaKind dstKind = javaKindOrFail(inst.getDestTy());
        emitReadValue(inst.getOperand(0));
        if (dstKind == JavaKind.Double) {
            switch (srcKind) {
                case Boolean:
                case Byte:
                case Short:
                case Integer:
                    addInstruction(new InsnNode(Opcodes.I2D));
                    return;
                case Long:
                    addInstruction(new InsnNode(Opcodes.L2D));
                    return;
                default:
                    throw LLVMToBytecode.unsupportedValue(inst);
            }
        }
        if (dstKind == JavaKind.Float) {
            switch (srcKind) {
                case Boolean:
                case Byte:
                case Short:
                case Integer:
                    addInstruction(new InsnNode(Opcodes.I2F));
                    return;
                case Long:
                    addInstruction(new InsnNode(Opcodes.L2F));
                    return;
                default:
                    throw LLVMToBytecode.unsupportedValue(inst);
            }
        }
        throw LLVMToBytecode.unsupportedValue(inst);
    }

    private void emitFPExtInst(FPExtInst inst) {
        assertJavaKind(JavaKind.Float, inst.getSrcTy());
        assertJavaKind(JavaKind.Double, inst.getDestTy());
        emitReadValue(inst.getOperand(0));
        addInstruction(new InsnNode(Opcodes.F2D));
    }

    private void emitFPTruncInst(FPTruncInst inst) {
        assertJavaKind(JavaKind.Double, inst.getSrcTy());
        assertJavaKind(JavaKind.Float, inst.getDestTy());
        emitReadValue(inst.getOperand(0));
        addInstruction(new InsnNode(Opcodes.D2F));
    }

    private void emitPtrToIntInst(PtrToIntInst inst) {
        assertJavaKind(JavaKind.Long, inst.getSrcTy());
        JavaKind dstKind = javaKindOrFail(inst.getDestTy());
        emitReadValue(inst.getOperand(0));
        switch (dstKind) {
            case Integer:
                addInstruction(new InsnNode(Opcodes.L2I));
                break;
            case Long:
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    private void emitIntToPtrInst(IntToPtrInst inst) {
        JavaKind srcKind = javaKindOrFail(inst.getSrcTy());
        assertJavaKind(JavaKind.Long, inst.getDestTy());
        emitReadValue(inst.getOperand(0));
        switch (srcKind) {
            case Integer:
                addInstruction(new InsnNode(Opcodes.I2L));
                break;
            case Long:
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    private void emitBitCastInst(BitCastInst inst) {
        JavaKind srcKind = javaKindOrFail(inst.getSrcTy());
        JavaKind dstKind = javaKindOrFail(inst.getDestTy());
        emitReadValue(inst.getOperand(0));
        switch (dstKind) {
            case Boolean: {
                switch (srcKind) {
                    case Byte:
                        emitCompare(Opcodes.IFNE);
                        break;
                    case Boolean:
                        break;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
                break;
            }
            case Byte: {
                switch (srcKind) {
                    case Byte:
                        break;
                    case Boolean:
                        break;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
                break;
            }
            case Short: {
                switch (srcKind) {
                    case Short:
                        break;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
                break;
            }
            case Integer: {
                switch (srcKind) {
                    case Integer:
                        break;
                    case Float:
                        emitCallJavaRuntime("floatToIntBits", "(F)I");
                        break;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
                break;
            }
            case Float: {
                switch (srcKind) {
                    case Integer:
                        emitCallJavaRuntime("intBitsToFloat", "(I)F");
                        break;
                    case Float:
                        break;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
                break;
            }
            case Long: {
                switch (srcKind) {
                    case Long:
                        break;
                    case Double:
                        emitCallJavaRuntime("doubleToLongBits", "(D)J");
                        break;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
                break;
            }
            case Double: {
                switch (srcKind) {
                    case Long:
                        emitCallJavaRuntime("longBitsToDouble", "(J)D");
                        break;
                    case Double:
                        break;
                    default:
                        throw LLVMToBytecode.unsupportedValue(inst);
                }
                break;
            }
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    boolean isJNIEnvPointerType(Value value) {
        JavaTag javaTag = getJavaTag(value);
        if (javaTag != null) {
            if (javaTag.getType() == JavaTag.TagType.JNIEnv) {
                return true;
            }
        }
        return false;
    }

    boolean isJNINativeInterfacePointerType(Value value) {
        JavaTag javaTag = getJavaTag(value);
        if (javaTag != null) {
            if (javaTag.getType() == JavaTag.TagType.JNINativeInterface) {
                return true;
            }
        }
        return false;
    }

    boolean isJNIEnFunType(Value value) {
        JavaTag javaTag = getJavaTag(value);
        if (javaTag != null) {
            if (javaTag.getType() == JavaTag.TagType.JNIEnvFun) {
                return true;
            }
        }
        return false;
    }

    boolean isConstantInt(Value value) {
        return value instanceof ConstantInt;
    }

    boolean isConstantIntZero(Value value) {
        if (value instanceof ConstantInt) {
            ConstantInt constantInt = (ConstantInt) value;
            return constantInt.getZExtValue() == 0L;
        }
        return false;
    }

    private long getConstantOffset(GetElementPtrInst inst) {
        DataLayout layout = getDataLayout();
        long offset = 0;
        for (GEPTypeIterator iterator = new GEPTypeIterator(inst);
             iterator.hasNext(); iterator.next()) {
            Value operand = iterator.getOperand();
            if (iterator.getStructTypeOrNull() != null) {
                if (!(operand instanceof ConstantInt)) {
                    throw new IllegalStateException("Struct must have constant index.");
                }
                int index = (int) ((ConstantInt) operand).getZExtValue();
                offset += layout.getStructLayout(iterator.getStructTypeOrNull()).getElementOffset(index);
            } else {
                Type indexedType = iterator.getIndexedType();
                long elementSize = layout.getTypeAllocSize(indexedType);
                if (operand instanceof ConstantInt) {
                    offset += elementSize * ((ConstantInt) operand).getZExtValue();
                    continue;
                }
                throw LLVMToBytecode.unsupportedValue(inst, "GEP has non-constant index");
            }
        }
        return offset;
    }

    /**
     * see CodeGen/GlobalISel/IRTranslator.cpp
     * @param inst
     */
    private void emitGetElementPtrInst(GetElementPtrInst inst) {
        Value pointer = inst.getPointerOperand();
        emitReadValue(pointer);
        DataLayout layout = getDataLayout();
        long offset = 0;
        for (GEPTypeIterator iterator = new GEPTypeIterator(inst);
             iterator.hasNext(); iterator.next()) {
            Value operand = iterator.getOperand();
            if (iterator.getStructTypeOrNull() != null) {
                if (!(operand instanceof ConstantInt)) {
                    throw new IllegalStateException("Struct must have constant index.");
                }
                int index = (int) ((ConstantInt) operand).getSExtValue();
                offset += layout.getStructLayout(iterator.getStructTypeOrNull()).getElementOffset(index);
            } else {
                Type indexedType = iterator.getIndexedType();
                long elementSize = layout.getTypeAllocSize(indexedType);
                if (operand instanceof ConstantInt) {
                    offset += elementSize * ((ConstantInt) operand).getZExtValue();
                    continue;
                }
                if (offset != 0) {
                    emitLdc(offset);
                    addInstruction(new InsnNode(Opcodes.LADD));
                    offset = 0;
                }

                emitReadValue(operand);
                if (elementSize != 1) {
                    emitLdc(elementSize);
                    addInstruction(new InsnNode(Opcodes.LMUL));
                }
                addInstruction(new InsnNode(Opcodes.LADD));
            }
        }
        if (offset != 0) {
            emitLdc(offset);
            addInstruction(new InsnNode(Opcodes.LADD));
        }
    }

    private void emitFCmpInst(FCmpInst inst) {
        Predicate predicate = inst.getPredicate();
        Value lhs = inst.getOperand(0);
        Value rhs = inst.getOperand(1);
        JavaKind kind = sameJavaKindOrFail(lhs, rhs);
        String desc;
        switch (kind) {
            case Float:
                desc = "(FF)Z";
                break;
            case Double:
                desc = "(DD)Z";
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
        String name;
        if (predicate == Predicate.FCMP_FALSE) {
            emitFalse();
            return;
        }
        if (predicate == Predicate.FCMP_TRUE) {
            emitTrue();
            return;
        }
        emitReadValue(lhs);
        emitReadValue(rhs);
        switch (predicate) {
            case FCMP_OEQ:
                name = "oeq";
                break;
            case FCMP_OGT:
                name = "ogt";
                break;
            case FCMP_OGE:
                name = "oge";
                break;
            case FCMP_OLT:
                name = "olt";
                break;
            case FCMP_OLE:
                name = "ole";
                break;
            case FCMP_ONE:
                name = "one";
                break;
            case FCMP_ORD:
                name = "ord";
                break;
            case FCMP_UNO:
                name = "uno";
                break;
            case FCMP_UEQ:
                name = "ueq";
                break;
            case FCMP_UGT:
                name = "ugt";
                break;
            case FCMP_UGE:
                name = "uge";
                break;
            case FCMP_ULT:
                name = "ult";
                break;
            case FCMP_ULE:
                name = "ule";
                break;
            case FCMP_UNE:
                name = "une";
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
        emitCallJavaRuntime(name, desc);
    }

    protected void emitCallJavaRuntime(String name, String desc) {
        MethodInsnNode methodInsnNode = new MethodInsnNode(Opcodes.INVOKESTATIC, JavaRuntime.DESCRIPTOR, name, desc, false);
        addInstruction(methodInsnNode);
    }

    protected void emitUnsignedIntegerCompare(int opcode) {
        emitIntegerCompare(opcode, false);
    }

    protected void emitSignedIntegerCompare(int opcode) {
        emitIntegerCompare(opcode, true);
    }

    protected void emitIntegerCompare(int opcode, boolean signed) {
        if (signed) {
            // opcode can do the compare
        } else {
            emitCallJavaRuntime("compareUnsigned", "(II)I");
            switch (opcode) {
                case Opcodes.IF_ICMPLT:
                    opcode = Opcodes.IFLT;
                    break;
                case Opcodes.IF_ICMPLE:
                    opcode = Opcodes.IFLE;
                    break;
                case Opcodes.IF_ICMPGT:
                    opcode = Opcodes.IFGT;
                    break;
                case Opcodes.IF_ICMPGE:
                    opcode = Opcodes.IFGE;
                    break;
                default:
                    throw new RuntimeException("Impossible");
            }
        }
        emitCompare(opcode);
    }

    protected void emitSignedLongCompare(int opcode) {
        emitLongCompare(opcode, true);
    }

    protected void emitUnsignedLongCompare(int opcode) {
        emitLongCompare(opcode, false);
    }

    protected void emitLongCompare(int opcode, boolean signed) {
        if (signed) {
            addInstruction(new InsnNode(Opcodes.LCMP));
        } else {
            emitCallJavaRuntime("compareUnsigned", "(JJ)I");
        }
        emitCompare(opcode);
    }

    protected void emitFalse() {
        InsnNode iconst_0 = new InsnNode(Opcodes.ICONST_0);
        addInstruction(iconst_0);
    }

    protected void emitTrue() {
        InsnNode iconst_1 = new InsnNode(Opcodes.ICONST_1);
        addInstruction(iconst_1);
    }

    protected void emitCompare(int opcode) {
        {
            LabelNode trueLabel = new LabelNode();
            JumpInsnNode ifop = new JumpInsnNode(opcode, trueLabel);
            addInstruction(ifop);
            emitFalse();
            LabelNode endLabel = new LabelNode();
            JumpInsnNode goto_ = new JumpInsnNode(Opcodes.GOTO, endLabel);
            addInstruction(goto_);
            addInstruction(trueLabel);
            emitTrue();
            addInstruction(endLabel);
        }
    }

    private int getOpposite(int opcode) {
        switch (opcode) {
            case Opcodes.IF_ACMPEQ:
                return Opcodes.IF_ACMPNE;
            case Opcodes.IF_ACMPNE:
                return Opcodes.IF_ACMPEQ;
            case Opcodes.IFEQ:
                return Opcodes.IFNE;
            case Opcodes.IFGT:
                return Opcodes.IFLE;
            case Opcodes.IFGE:
                return Opcodes.IFLT;
            case Opcodes.IFLT:
                return Opcodes.IFGE;
            case Opcodes.IFLE:
                return Opcodes.IFGT;
            case Opcodes.IFNE:
                return Opcodes.IFEQ;
            case Opcodes.IF_ICMPEQ:
                return Opcodes.IF_ICMPNE;
            case Opcodes.IF_ICMPGT:
                return Opcodes.IF_ICMPLE;
            case Opcodes.IF_ICMPGE:
                return Opcodes.IF_ICMPLT;
            case Opcodes.IF_ICMPLT:
                return Opcodes.IF_ICMPGE;
            case Opcodes.IF_ICMPLE:
                return Opcodes.IF_ICMPGT;
            case Opcodes.IF_ICMPNE:
                return Opcodes.IF_ICMPEQ;
            default:
                throw new IllegalArgumentException("Unsupported opcode: " + opcode);
        }
    }

    private void emitICmpInst(ICmpInst inst) {
        Predicate predicate = inst.getPredicate();
        Value lhs = inst.getOperand(0);
        Value rhs = inst.getOperand(1);
        JavaKind kind = sameJavaKindOrFail(lhs, rhs);
        switch (kind) {
            case Byte:
            case Short:
            case Integer:
                emitICmpInst(inst, predicate, lhs, rhs);
                break;
            case Long:
                emitLCmpInst(inst, predicate, lhs, rhs);
                break;
            case Object:
            case Array:
                emitACmpInst(inst, predicate, lhs, rhs);
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    private void emitACmpInst(ICmpInst inst, Predicate predicate, Value lhs, Value rhs) {
        if (isConstantPointerNull(lhs)) {
            emitNull();
            if (isConstantPointerNull(rhs)) {
                emitNull();
            } else {
                getAccessibleJavaTagOrFail(rhs);
                emitReadValue(rhs);
            }
        } else if (isConstantPointerNull(rhs)) {
            if (isConstantPointerNull(lhs)) {
                emitNull();
            } else {
                getAccessibleJavaTagOrFail(lhs);
                emitReadValue(lhs);
            }
            emitNull();
        } else {
            getAccessibleJavaTagOrFail(lhs);
            emitReadValue(lhs);
            getAccessibleJavaTagOrFail(rhs);
            emitReadValue(rhs);
        }


        switch (predicate) {
            case ICMP_EQ:
                emitCompare(Opcodes.IF_ACMPEQ);
                break;
            case ICMP_NE:
                emitCompare(Opcodes.IF_ACMPNE);
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst, "Unknown predicate: " + predicate);
        }
    }

    private void emitLCmpInst(ICmpInst inst, Predicate predicate, Value lhs, Value rhs) {
        emitReadValue(lhs);
        emitReadValue(rhs);
        switch (predicate) {
            case ICMP_EQ:
                emitSignedLongCompare(Opcodes.IFEQ);
                break;
            case ICMP_NE:
                emitSignedLongCompare(Opcodes.IFNE);
                break;
            case ICMP_UGT:
                emitUnsignedLongCompare(Opcodes.IFGT);
                break;
            case ICMP_UGE:
                emitUnsignedLongCompare(Opcodes.IFGE);
                break;
            case ICMP_ULT:
                emitUnsignedLongCompare(Opcodes.IFLT);
                break;
            case ICMP_ULE:
                emitUnsignedLongCompare(Opcodes.IFLE);
                break;
            case ICMP_SGT:
                emitSignedLongCompare(Opcodes.IFGT);
                break;
            case ICMP_SGE:
                emitSignedLongCompare(Opcodes.IFGE);
                break;
            case ICMP_SLT:
                emitSignedLongCompare(Opcodes.IFLT);
                break;
            case ICMP_SLE:
                emitSignedLongCompare(Opcodes.IFLE);
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst, "Unknown predicate: " + predicate);
        }
    }

    private void emitICmpInst(ICmpInst inst, Predicate predicate, Value lhs, Value rhs) {
        emitReadValue(lhs);
        emitReadValue(rhs);
        switch (predicate) {
            case ICMP_EQ:
                emitSignedIntegerCompare(Opcodes.IF_ICMPEQ);
                break;
            case ICMP_NE:
                emitSignedIntegerCompare(Opcodes.IF_ICMPNE);
                break;
            case ICMP_UGT:
                emitUnsignedIntegerCompare(Opcodes.IF_ICMPGT);
                break;
            case ICMP_UGE:
                emitUnsignedIntegerCompare(Opcodes.IF_ICMPGE);
                break;
            case ICMP_ULT:
                emitUnsignedIntegerCompare(Opcodes.IF_ICMPLT);
                break;
            case ICMP_ULE:
                emitUnsignedIntegerCompare(Opcodes.IF_ICMPLE);
                break;
            case ICMP_SGT:
                emitSignedIntegerCompare(Opcodes.IF_ICMPGT);
                break;
            case ICMP_SGE:
                emitSignedIntegerCompare(Opcodes.IF_ICMPGE);
                break;
            case ICMP_SLT:
                emitSignedIntegerCompare(Opcodes.IF_ICMPLT);
                break;
            case ICMP_SLE:
                emitSignedIntegerCompare(Opcodes.IF_ICMPLE);
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }

    private void emitSelectInst(SelectInst inst) {
        Value condition = inst.getCondition();
        assertJavaKind(JavaKind.Boolean, condition);
        emitReadValue(condition);
        Value operandTrue = inst.getTrueValue();
        Value operandFalse = inst.getFalseValue();
        JavaKind trueKind = javaKindOrFail(operandTrue);
        JavaKind falseKind = javaKindOrFail(operandFalse);
        if (trueKind != falseKind) {
            throw new IllegalStateException();
        }
        LabelNode falseLabel = new LabelNode();
        JumpInsnNode ifop = new JumpInsnNode(Opcodes.IFNE, falseLabel);
        addInstruction(ifop);
        // condition is 0
        emitReadValue(operandFalse);
        LabelNode endLabel = new LabelNode();
        JumpInsnNode goto_ = new JumpInsnNode(Opcodes.GOTO, endLabel);
        addInstruction(goto_);
        addInstruction(falseLabel);
        // condition is 1
        emitReadValue(operandTrue);
        addInstruction(endLabel);
    }

    protected void assertJavaKind(JavaKind kind, Type type) {
        if (javaKindOrFail(type) != kind) {
            throw LLVMToBytecode.unsupportedType(type, "expected JavaKind " + kind + ", got " + javaKindOrFail(type));
        }
    }

    protected void assertJavaKind(JavaKind kind, Value value) {
        if (javaKindOrFail(value) != kind) {
            throw LLVMToBytecode.unsupportedValue(value);
        }
    }

    /**
     * To support vararg
     * @param callBase
     * @return
     */
    private JavaFunctionType getJavaFunctionTypeFromArgumentList(CallBase callBase) {
        FunctionType functionType = callBase.getFunctionType();
        if (!functionType.isVarArg()) {
            throw new IllegalArgumentException("Must be called for vararg");
        }
        int argNum = callBase.getNumArgOperands();
        JavaKind[] argumentKinds = new JavaKind[argNum];
        for (int i = 0; i < argNum; i++) {
            argumentKinds[i] = javaKindOrFail(callBase.getArgOperand(i));
        }

        return new JavaFunctionType(javaKindOrFail(functionType.getReturnType()), argumentKinds, false);
    }

    private void emitCallInst(CallInst inst) {
        emitCallBase(inst);
    }

    private boolean emitDevirtualization(CallBase inst, Value called, FunctionType functionType) {
        // try de-virtualization;
        VTableEntry vTableEntry = getVTableEntry(inst);
        if (vTableEntry != null) {
            VTable vtable = vTableEntry.getVtable();
            Function function = vTableEntry.getFunction();
            if (supportDirectCall(function)) {
                GlobalVariable typeInfo = vtable.getTypeInfo();
                LoadInst functionPointer = (LoadInst) called;
                GetElementPtrInst functionAddr = (GetElementPtrInst) functionPointer.getPointerOperand();
                Value vtableAddr = functionAddr.getPointerOperand();
                // https://itanium-cxx-abi.github.io/cxx-abi/abi.html#rtti-layout
                // rtti is in -1 ventry

                emitReadValue(typeInfo);
                emitReadValue(vtableAddr);
                emitLdc(-8L); // TODO: 64 bit platform only
                addInstruction(new InsnNode(Opcodes.LADD));
                emitCallJavaRuntime("getLong", "(J)J");
                addInstruction(new InsnNode(Opcodes.LCMP));

                LabelNode trueLabel = new LabelNode();
                JumpInsnNode ifop = new JumpInsnNode(Opcodes.IFEQ, trueLabel);
                addInstruction(ifop);
                emitCallIndirect(inst, called, functionType);
                LabelNode endLabel = new LabelNode();
                JumpInsnNode goto_ = new JumpInsnNode(Opcodes.GOTO, endLabel);
                addInstruction(goto_);
                addInstruction(trueLabel);
                emitCallFunction(inst, function, functionType, false);
                addInstruction(endLabel);
                return true;
            }
        }
        return false;
    }

    protected boolean supportIndirectCall(CallBase inst) {
        return indirectCallPolicy.supportIndirectCall(universe, inst);
    }

    private void emitCallBase(CallBase inst) {
        if (inst instanceof IntrinsicInst) {
            emitIntrinsicInst((IntrinsicInst) inst);
            return;
        }
        if (inst.hasByValArgument()) {
            // TODO: pass-by-value means we need to do
            throw LLVMToBytecode.unsupportedValue(inst, "TODO: pass-by-value");
        }

        FunctionType functionType = inst.getFunctionType();
        if (!LLVMToBytecode.support(functionType)) {
            throw LLVMToBytecode.unsupportedType(functionType, "Unsupported FunctionType");
        }

        // Check if it is an JNI function
        JNIEnvFun jniFun = getJNIFunction(inst);
        if (jniFun != null) {
            handleJNIFun(inst, jniFun);
            return;
        }

        // simply make an indirect call
        if (functionType.isVarArg()) {
            if (!supportIndirectCall(inst)) {
                throw LLVMToBytecode.unsupportedValue(inst, "supportIndirectCall is disabled");
            }
            // TODO: need more testing
            JavaFunctionType javaFunctionType = getJavaFunctionTypeFromArgumentList(inst);

            Value called = inst.getCalledOperand();
            if (called instanceof Function) {
                Function callee = (Function) called;
                if (!universe.isAvailableInELF(callee)) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot make indirect call on " + callee.getName());
                }
                LibraryClass libraryClass = universe.getLibraryClass();
                LibraryMethod libraryMethod = libraryClass.getOrCreateGetFunctionPointerMethod(callee);
                emitCallLibraryMethod(libraryClass, libraryMethod);
            } else {
                emitReadValue(called);
            }

            int numArgs = inst.getNumArgOperands();
            for (int i = 0; i < numArgs; i++) {
                emitReadValue(inst.getArgOperand(i));
            }

            LibraryClass libraryClass = universe.getLibraryClass();
            LibraryMethod methodBody = libraryClass.getOrCreateIndirectFunctionMethod(javaFunctionType);
            LLVMToBytecode.generateIndirectFunctionMethod(universe, methodBody, javaFunctionType);
            emitCallLibraryMethod(libraryClass, methodBody);
            return;
        }

        Value called = inst.getCalledOperand();
        if (called instanceof Function) {
            Function callee = (Function) called;
            if (emitCallLibc(inst, callee, functionType)) {
                return;
            }
            emitCallFunction(inst, callee, functionType, true);
            return;
        } else {
            if (options.supportDevirtualization()) {
                if (emitDevirtualization(inst, called, functionType)) {
                    return;
                }
            }

            if (!supportIndirectCall(inst)) {
                if (!functionType.equals(inst.getFunctionType())) {
                    throw new IllegalStateException();
                }
                if (Logger.debug()) {
                    List<VTableEntry> vTableEntries = universe.getVTableEntryList(functionType);
                    for (int i = 0; i < vTableEntries.size(); i++) {
                        Logger.debug("%3d: %s", i, vTableEntries.get(i));
                    }
                }
                throw LLVMToBytecode.unsupportedValue(inst, "supportIndirectCall is disabled");
            }
            emitCallIndirect(inst, called, functionType);
            return;
        }
    }

    private boolean emitCallLibc(CallBase inst, Function callee, FunctionType functionType) {
        if (!options.useJavaLibC()) {
            return false;
        }
        String name = callee.getName();
        switch (name) {
            case "strlen": {
                String desc = "(J)J";
                JavaFunctionType javaFunctionType = LLVMToBytecode.getJavaFunctionType(functionType);
                if (!javaFunctionType.toDescriptor().equals(desc)) {
                    return false;
                }
                int numArgs = inst.getNumArgOperands();
                if (numArgs != 1) {
                    return false;
                }
                emitReadValue(inst.getArgOperand(0));
                emitCallJavaRuntime(name, desc);
                return true;
            }
            case "bcmp": {
                String desc = "(JJJ)I";
                JavaFunctionType javaFunctionType = LLVMToBytecode.getJavaFunctionType(functionType);
                if (!javaFunctionType.toDescriptor().equals("(JJJ)I")) {
                    return false;
                }
                int numArgs = inst.getNumArgOperands();
                if (numArgs != 3) {
                    return false;
                }
                for (int i = 0; i < numArgs; i++) {
                    emitReadValue(inst.getArgOperand(i));
                }
                emitCallJavaRuntime(name, desc);
                return true;
            }
        }
        return false;
    }

    protected void checkCallingConv(CallBase inst) {
        if (!inst.getCallingConv().isDefaultC()) {
            throw LLVMToBytecode.unsupportedValue(inst, "Unsupported calling convention: " + inst.getCallingConv() + " to call " + inst.getCalledOperand());
        }
    }

    private boolean supportDirectCall(Function callee) {
        return universe.supportDirectCall(callee);
    }

    private void emitCallFunction(CallBase inst, Function callee, FunctionType functionType, boolean fallbackToIndirect) {
        LibraryClass libraryClass = universe.getLibraryClass();
        LibraryMethod methodBody = libraryClass.getOrCreateDirectFunctionMethod(callee);

        LLVMToBytecode.generateLLVMFunctionMethod(universe, methodBody, callee);

        if (methodBody.getBytecodeType() != LibraryMethod.BytecodeType.Direct) {
            if (!supportIndirectCall(inst)) {
                throw LLVMToBytecode.unsupportedValue(callee, "cannot call indirect method " + callee.getName());
            }
            if (!fallbackToIndirect) {
                throw LLVMToBytecode.unsupportedValue(callee, "cannot fall back to indirect method " + callee.getName());
            }
            checkCallingConv(inst);
        }

        int numArgs = inst.getNumArgOperands();
        for (int i = 0; i < numArgs; i++) {
            emitReadValue(inst.getArgOperand(i));
        }
        emitCallLibraryMethod(libraryClass, methodBody);
    }

    private void emitCallIndirect(CallBase inst, Value called, FunctionType functionType) {
        checkCallingConv(inst);
        emitReadValue(called);
        int numArgs = inst.getNumArgOperands();
        for (int i = 0; i < numArgs; i++) {
            emitReadValue(inst.getArgOperand(i));
        }

        JavaFunctionType javaFunctionType = LLVMToBytecode.getJavaFunctionType(functionType);
        LibraryClass libraryClass = universe.getLibraryClass();
        LibraryMethod methodBody = libraryClass.getOrCreateIndirectFunctionMethod(javaFunctionType);
        LLVMToBytecode.generateIndirectFunctionMethod(universe, methodBody, functionType);
        emitCallLibraryMethod(libraryClass, methodBody);
    }

    private void emitReadJavaReference(Value value) {
        if (isConstantPointerNull(value)) {
            // ConstantPointerNull is shared by both Java and C++
            // We cannot map it to any value.
            addInstruction(new InsnNode(Opcodes.ACONST_NULL));
        } else {
            emitReadValue(value);
        }
    }

    private void handleJNIFun(CallBase inst, JNIEnvFun jniFun) {
        switch (jniFun) {
            case GetVersion: {
                // See JavaRuntime.jniGetVersion
                emitCallJavaRuntime("jniGetVersion", "()I");
                break;
            }
            case FindClass: {
                // 0: JNIEnv*; 1: class name
                String name = getJClassTypeDef(inst).getInternalName();
                emitLdc(org.objectweb.asm.Type.getObjectType(name));
                return;
            }
            case GetSuperclass: {
                TypeDef typeDef = getJClassTypeDef(inst.getArgOperand(1));
                if (typeDef.isExact()) {
                    if (universe.isJavaLangObject(typeDef)) {
                        addInstruction(new InsnNode(Opcodes.ACONST_NULL));
                    } else {
                        String name = typeDef.getSuperType().getInternalName();
                        emitLdc(org.objectweb.asm.Type.getObjectType(name));
                    }
                } else {
                    emitReadJavaReference(inst.getArgOperand(1));
                    addInstruction(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                            "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;"));
                }
                return;
            }
            case IsAssignableFrom: {
                // 0: JNIEnv*; 1: jclass; 2: jclass
                emitReadJavaReference(inst.getArgOperand(1));
                emitReadJavaReference(inst.getArgOperand(2));
                emitCallJavaRuntime("jniIsAssignableFrom", "(Ljava/lang/Class;Ljava/lang/Class;)Z");
                return;
            }
            case IsSameObject: {
                // 0: JNIEnv*; 1: jobject; 2: jobject
                emitReadJavaReference(inst.getArgOperand(1));
                emitReadJavaReference(inst.getArgOperand(2));
                emitCompare(Opcodes.IF_ACMPEQ);
                return;
            }
            case AllocObject: {
                // 0: JNIEnv*; 1: jclass;
                TypeDef typeDef = getJClassTypeDef(inst.getArgOperand(1));
                emitLdc(org.objectweb.asm.Type.getObjectType(typeDef.getInternalName()));
                emitCallJavaRuntime("jniAllocObject", "(Ljava/lang/Class;)Ljava/lang/Object;");
                addInstruction(new TypeInsnNode(Opcodes.CHECKCAST, typeDef.getInternalName()));
                return;
            }
            case NewObject: {
                // 0: JNIEnv*; 1: jclass; 2: jmethodID; 3: ...
                String name = getJClassTypeDef(inst.getArgOperand(1)).getInternalName();
                addInstruction(new TypeInsnNode(Opcodes.NEW, name));
                addInstruction(new InsnNode(Opcodes.DUP));
                MethodTypeDef methodTypeDef = getMethodTypeDef(inst.getArgOperand(2));
                emitInvokeJava(inst, methodTypeDef, 3, Opcodes.INVOKESPECIAL);
                return;
            }
            case GetObjectClass: {
                // 0: JNIEnv*; 1: jobject;
                emitReadJavaReference(inst.getArgOperand(1));
                addInstruction(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                        "java/lang/Object", "getClass", "()Ljava/lang/Class;"));
                return;
            }
            case IsInstanceOf: {
                // 0: JNIEnv*; 1: jobject; 2: jclass
                emitReadJavaReference(inst.getArgOperand(1));
                String name = getJClassTypeDef(inst.getArgOperand(2)).getInternalName();
                addInstruction(new TypeInsnNode(Opcodes.INSTANCEOF, name));
                return;
            }
            case GetMethodID:
            case GetStaticMethodID: {
                // no-op
                return;
            }
            case CallObjectMethod:
            case CallBooleanMethod:
            case CallByteMethod:
            case CallCharMethod:
            case CallShortMethod:
            case CallIntMethod:
            case CallLongMethod:
            case CallFloatMethod:
            case CallDoubleMethod:
            case CallVoidMethod: {
                // 0: JNIEnv*; 1: jobject; 2: jmethodID; 3: ...
                emitReadJavaReference(inst.getArgOperand(1));
                MethodTypeDef methodTypeDef = getMethodTypeDef(inst.getArgOperand(2));
                if (methodTypeDef.isPrivate()) {
                    emitInvokeJava(inst, methodTypeDef, 3, Opcodes.INVOKESPECIAL);
                } else {
                    emitInvokeJava(inst, methodTypeDef, 3, Opcodes.INVOKEVIRTUAL);
                }
                return;
            }
            case CallNonvirtualObjectMethod:
            case CallNonvirtualBooleanMethod:
            case CallNonvirtualByteMethod:
            case CallNonvirtualCharMethod:
            case CallNonvirtualShortMethod:
            case CallNonvirtualIntMethod:
            case CallNonvirtualLongMethod:
            case CallNonvirtualFloatMethod:
            case CallNonvirtualDoubleMethod:
            case CallNonvirtualVoidMethod: {
                // 0: JNIEnv*; 1: jobject; 2: jclass; 3: jmethodID; 3: ...
                emitReadJavaReference(inst.getArgOperand(1));
                MethodTypeDef methodTypeDef = getMethodTypeDef(inst.getArgOperand(3));
                emitInvokeJava(inst, methodTypeDef, 4, Opcodes.INVOKESPECIAL);
                return;
            }
            case GetFieldID:
            case GetStaticFieldID: {
                // no-op
                return;
            }
            case GetObjectField:
            case GetBooleanField:
            case GetByteField:
            case GetCharField:
            case GetShortField:
            case GetIntField:
            case GetLongField:
            case GetFloatField:
            case GetDoubleField: {
                // 0: JNIEnv*; 1: jobject; 2: jfieldID
                emitReadJavaReference(inst.getArgOperand(1));
                FieldTypeDef fieldTypeDef = getFieldTypeDef(inst.getArgOperand(2));
                emitFieldAccess(fieldTypeDef, Opcodes.GETFIELD);
                return;
            }
            case SetObjectField:
            case SetBooleanField:
            case SetByteField:
            case SetCharField:
            case SetShortField:
            case SetIntField:
            case SetLongField:
            case SetFloatField:
            case SetDoubleField: {
                // 0: JNIEnv*; 1: jobject; 2: jfieldID; 3: jobject if SetObjectField
                emitReadJavaReference(inst.getArgOperand(1));
                FieldTypeDef fieldTypeDef = getFieldTypeDef(inst.getArgOperand(2));
                if (jniFun == JNIEnvFun.SetObjectField) {
                    emitReadJavaReference(inst.getArgOperand(3));
                } else {
                    emitReadValue(inst.getArgOperand(3));
                }
                emitFieldAccess(fieldTypeDef, Opcodes.PUTFIELD);
                return;
            }
            case CallStaticObjectMethod:
            case CallStaticBooleanMethod:
            case CallStaticByteMethod:
            case CallStaticCharMethod:
            case CallStaticShortMethod:
            case CallStaticIntMethod:
            case CallStaticLongMethod:
            case CallStaticFloatMethod:
            case CallStaticDoubleMethod:
            case CallStaticVoidMethod: {
                // 0: JNIEnv*; 1: jclass; 2: jmethodID;
                MethodTypeDef methodTypeDef = getMethodTypeDef(inst.getArgOperand(2));
                emitInvokeJava(inst, methodTypeDef, 3, Opcodes.INVOKESTATIC);
                return;
            }
            case GetStaticObjectField:
            case GetStaticBooleanField:
            case GetStaticByteField:
            case GetStaticCharField:
            case GetStaticShortField:
            case GetStaticIntField:
            case GetStaticLongField:
            case GetStaticFloatField:
            case GetStaticDoubleField: {
                // 0: JNIEnv*; 1: jclass; 2: jfieldID
                FieldTypeDef fieldTypeDef = getFieldTypeDef(inst.getArgOperand(2));
                if (!fieldTypeDef.isStatic()) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot access an instance field via " + jniFun);
                }
                emitFieldAccess(fieldTypeDef, Opcodes.GETSTATIC);
                return;
            }
            case SetStaticObjectField:
            case SetStaticBooleanField:
            case SetStaticByteField:
            case SetStaticCharField:
            case SetStaticShortField:
            case SetStaticIntField:
            case SetStaticLongField:
            case SetStaticFloatField:
            case SetStaticDoubleField: {
                // 0: JNIEnv*; 1: jclass; 2: jfieldID; 3: value
                FieldTypeDef fieldTypeDef = getFieldTypeDef(inst.getArgOperand(2));
                if (!fieldTypeDef.isStatic()) {
                    throw LLVMToBytecode.unsupportedValue(inst, "Cannot access an instance field via " + jniFun);
                }
                if (jniFun == JNIEnvFun.SetStaticObjectField) {
                    emitReadJavaReference(inst.getArgOperand(3));
                } else {
                    emitReadValue(inst.getArgOperand(3));
                }
                emitFieldAccess(fieldTypeDef, Opcodes.PUTSTATIC);
                break;
            }
            case GetArrayLength: {
                // 0: JNIEnv*; 1: array;
                emitReadJavaReference(inst.getArgOperand(1));
                addInstruction(new InsnNode(Opcodes.ARRAYLENGTH));
                return;
            }
            case NewObjectArray: {
                // 0: JNIEnv*; 1: size; 2: elementClass Name; 3: initial element
                String name = getJClassTypeDef(inst.getArgOperand(2)).getInternalName();
                emitReadValue(inst.getArgOperand(1));
                addInstruction(new TypeInsnNode(Opcodes.ANEWARRAY, name));
                addInstruction(new InsnNode(Opcodes.DUP));
                emitReadJavaReference(inst.getArgOperand(3));
                emitCallJavaRuntime("jniFillObjectArray", "([Ljava/lang/Object;Ljava/lang/Object;)V");
                return;
            }
            case GetObjectArrayElement: {
                // 0: JNIEnv*; 1: array; 2: index;
                Value arrayValue = inst.getArgOperand(1);
                Value indexValue = inst.getArgOperand(2);
                getAccessibleJavaTagOrFail(arrayValue);
                emitReadValue(arrayValue);
                emitReadValue(indexValue);
                addInstruction(new InsnNode(Opcodes.AALOAD));
                return;
            }
            case SetObjectArrayElement: {
                // 0: JNIEnv*; 1: array; 2: index; 3: value
                Value arrayValue = inst.getArgOperand(1);
                Value indexValue = inst.getArgOperand(2);
                Value valueValue = inst.getArgOperand(3);
                getAccessibleJavaTagOrFail(arrayValue);
                getAccessibleJavaTagOrFail(valueValue);
                emitReadValue(arrayValue);
                emitReadValue(indexValue);
                emitReadValue(valueValue);
                addInstruction(new InsnNode(Opcodes.AASTORE));
                return;
            }
            case NewBooleanArray: {
                // 0: JNIEnv*; 1: size
                emitReadValue(inst.getArgOperand(1));
                addInstruction(new IntInsnNode(Opcodes.NEWARRAY, 4));
                return;
            }
            case NewByteArray: {
                // 0: JNIEnv*; 1: size
                emitReadValue(inst.getArgOperand(1));
                addInstruction(new IntInsnNode(Opcodes.NEWARRAY, 8));
                return;
            }
            case NewCharArray: {
                // 0: JNIEnv*; 1: size
                emitReadValue(inst.getArgOperand(1));
                addInstruction(new IntInsnNode(Opcodes.NEWARRAY, 5));
                return;
            }
            case NewShortArray: {
                // 0: JNIEnv*; 1: size
                emitReadValue(inst.getArgOperand(1));
                addInstruction(new IntInsnNode(Opcodes.NEWARRAY, 9));
                return;
            }
            case NewIntArray: {
                // 0: JNIEnv*; 1: size
                emitReadValue(inst.getArgOperand(1));
                addInstruction(new IntInsnNode(Opcodes.NEWARRAY, 10));
                return;
            }
            case NewLongArray: {
                // 0: JNIEnv*; 1: size
                emitReadValue(inst.getArgOperand(1));
                addInstruction(new IntInsnNode(Opcodes.NEWARRAY, 11));
                return;
            }
            case NewFloatArray: {
                // 0: JNIEnv*; 1: size
                emitReadValue(inst.getArgOperand(1));
                addInstruction(new IntInsnNode(Opcodes.NEWARRAY, 6));
                return;
            }
            case NewDoubleArray: {
                // 0: JNIEnv*; 1: size
                emitReadValue(inst.getArgOperand(1));
                addInstruction(new IntInsnNode(Opcodes.NEWARRAY, 7));
                return;
            }
            default:
                throw LLVMToBytecode.unsupportedValue(inst, "TODO: support JNIFunction " + jniFun + " in the future");
        }
    }

    boolean isConstantPointerNull(Value value) {
        return value instanceof ConstantPointerNull;
    }

    private JNIEnvFun getJNIFunction(Value value) {
        if (value instanceof GetElementPtrInst) {
            GetElementPtrInst gep = (GetElementPtrInst) value;
            return getJNIFunction(gep);
        }
        return null;
    }

    private JNIEnvFun getJNIFunction(GetElementPtrInst gep) {
        ConstantInt idx = (ConstantInt) gep.getOperand(2);
        int index = Math.toIntExact(idx.getSExtValue());
        return JNIEnvFun.getJNIEnvFunByIndex(index);
    }

    private JNIEnvFun getJNIFunction(CallBase inst) {
        Value called = inst.getCalledOperand();

        if (called instanceof LoadInst) {
            // JNI C interface
            if (isJNIEnFunType(called)) {
                LoadInst loadInst = (LoadInst) called;
                Value pointer = loadInst.getPointerOperand();
                return getJNIFunction(pointer);
            }
            return null;
        } else if (called instanceof Function) {
            // JNI CXX interface
            Function function = (Function) called;
            String name = function.getName();
            return JNIEnvFun.getJNIEnvFunByCxxName(name);
        }

        return null;
    }

    /**
     * https://llvm.org/docs/LangRef.html#llvm-memcpy-intrinsic
     * @param inst
     */
    private void emitMemcpy(IntrinsicInst inst) {
        Value dest = inst.getArgOperand(0);
        assertJavaKind(JavaKind.Long, dest);
        Value src = inst.getArgOperand(1);
        assertJavaKind(JavaKind.Long, src);
        Value length = inst.getArgOperand(2);
        Value isVolatile = inst.getArgOperand(3);
        if (isVolatile instanceof ConstantInt) {
            ConstantInt c = (ConstantInt) isVolatile;
            if (c.getZExtValue() != 0) {
                // TODO: do not know how to perform volatile memcpy
                throw LLVMToBytecode.unsupportedValue(inst, "do not know how to perform volatile memcpy");
            }
        } else {
            throw LLVMToBytecode.unsupportedValue(inst,  "do not know whether memcpy is volatile");
        }
        emitReadValue(src);
        emitReadValue(dest);
        JavaKind lengthKind = javaKindOrFail(length);
        emitReadValue(length);
        switch (lengthKind) {
            case Integer:
                addInstruction(new InsnNode(Opcodes.I2L));
                break;
            case Long:
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
        emitCallJavaRuntime("copyMemory", "(JJJ)V");
    }

    /**
     * https://llvm.org/docs/LangRef.html#llvm-memmove-intrinsic
     * @param inst
     */
    private void emitMemmove(IntrinsicInst inst) {
        Value dest = inst.getArgOperand(0);
        assertJavaKind(JavaKind.Long, dest);
        Value src = inst.getArgOperand(1);
        assertJavaKind(JavaKind.Long, src);
        Value length = inst.getArgOperand(2);
        Value isVolatile = inst.getArgOperand(3);
        if (isVolatile instanceof ConstantInt) {
            ConstantInt c = (ConstantInt) isVolatile;
            if (c.getZExtValue() != 0) {
                // TODO: do not know how to perform volatile memcpy
                throw LLVMToBytecode.unsupportedValue(inst, "do not know how to perform volatile memmove");
            }
        } else {
            throw LLVMToBytecode.unsupportedValue(inst,  "do not know whether memmove is volatile");
        }
        emitReadValue(src);
        emitReadValue(dest);
        JavaKind lengthKind = javaKindOrFail(length);
        emitReadValue(length);
        switch (lengthKind) {
            case Integer:
                addInstruction(new InsnNode(Opcodes.I2L));
                break;
            case Long:
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
        emitCallJavaRuntime("moveMemory", "(JJJ)V");
    }

    /**
     * https://llvm.org/docs/LangRef.html#llvm-memset-intrinsics
     * @param inst
     */
    private void emitMemset(IntrinsicInst inst) {
        Value dest = inst.getArgOperand(0);
        assertJavaKind(JavaKind.Long, dest);
        Value value = inst.getArgOperand(1);
        assertJavaKind(JavaKind.Byte, value);
        Value length = inst.getArgOperand(2);
        Value isVolatile = inst.getArgOperand(3);
        if (isVolatile instanceof ConstantInt) {
            ConstantInt c = (ConstantInt) isVolatile;
            if (c.getZExtValue() != 0) {
                // TODO: donot know how to perform volatile memcpy
                throw LLVMToBytecode.unsupportedValue(inst);
            }
        } else {
            throw LLVMToBytecode.unsupportedValue(inst);
        }
        emitReadValue(dest);
        emitReadValue(value);
        emitReadValue(length);
        JavaKind lengthKind = javaKindOrFail(length);
        if (lengthKind == JavaKind.Long) {
            // nop
        } else if (lengthKind == JavaKind.Integer) {
            addInstruction(new InsnNode(Opcodes.I2L));
        } else {
            throw LLVMToBytecode.unsupportedValue(inst);
        }
        emitCallJavaRuntime("memset", "(JBJ)V");
    }

    private void emitIntrinsicInst(IntrinsicInst inst) {
        IntrinsicID intrinsicID = inst.getIntrinsicID();
        switch (intrinsicID) {
            case not_intrinsic:
                throw new IllegalStateException();
            case dbg_addr:
            case dbg_declare:
            case dbg_label:
            case dbg_value:
            case debugtrap:
                return;
            case memcpy:
                emitMemcpy(inst);
                return;
            case memset:
                emitMemset(inst);
                return;
            case memmove:
                emitMemmove(inst);
                return;
            case ctlz:
                emitCountBits(inst, "ctlz");
                return;
            case ctpop:
                emitCountBits(inst, "ctpop");
                return;
            case cttz:
                emitCountBits(inst, "cttz");
                return;
            case lifetime_start:
            case lifetime_end:
                // noop: alloca are managed by try-resource in wrapper method
                return;
            case assume:
                // nothing needed for llvm.assume
                return;
            default:
                throw LLVMToBytecode.unsupportedValue(inst, "unsupported intrinsic " + intrinsicID);
        }
    }

    private void emitCountBits(IntrinsicInst inst, String type) {
        Value value = inst.getOperand(0);
        JavaKind kind = javaKindOrFail(value);
        String desc;
        switch (kind) {
            case Byte:
                desc = "(Z)Z";
                break;
            case Short:
                desc = "(S)S";
                break;
            case Integer:
                desc = "(I)I";
                break;
            case Long:
                desc = "(J)J";
                break;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
        emitReadValue(value);
        emitCallJavaRuntime(type, desc);
    }

    private void emitUnaryOperator(UnaryOperator inst) {
        assert inst.getOpcode() == Opcode.FNeg;
        Value arg = inst.getOperand(0);
        emitReadValue(arg);
        JavaKind kind = javaKindOrFail(arg);
        switch (kind) {
            case Float:
                addInstruction(new InsnNode(Opcodes.FNEG));
                return;
            case Double:
                addInstruction(new InsnNode(Opcodes.DNEG));
                return;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }


    private void emitResumeInst(ResumeInst inst) {
        throw LLVMToBytecode.unsupportedValue(inst);
    }

    protected void emitThrow(String exceptionName) {
        addInstruction(new TypeInsnNode(Opcodes.NEW, exceptionName));
        addInstruction(new InsnNode(Opcodes.DUP));
        addInstruction(new MethodInsnNode(Opcodes.INVOKESPECIAL, exceptionName, "<init>", "()V", false));
        addInstruction(new InsnNode(Opcodes.ATHROW));
    }

    private void emitUnreachableInst(UnreachableInst inst) {
        emitThrow(UnreachableException.DESCRIPTOR);
    }

    private void emitInvokeInst(InvokeInst inst) {
        if (!options.supportInvokeUnwind()) {
            throw LLVMToBytecode.unsupportedValue(inst, "TODO: Invoke unwind is not supported");
        }
        emitCallBase(inst);
    }

    private void emitIndirectBrInst(IndirectBrInst inst) {
        throw LLVMToBytecode.unsupportedValue(inst);
    }

    private void emitSwitchInst(SwitchInst inst) {
        {
            // check whether condition type is supported
            Value condition = inst.getCondition();
            Type type = condition.getType();
            if (!IntegerType.isa(type)) {
                throw LLVMToBytecode.unsupportedValue(inst, "condition must be an integer");
            }
            IntegerType integerType = IntegerType.cast(type);
            if (integerType.getBitWidth() > 32) {
                if (integerType.getBitWidth() == 64) {
                    int numCases = inst.getNumCases();
                    Map<Integer, LabelNode> hasPhiSuccessor = new HashMap<>();
                    for (int i = 1 ; i <= numCases; i++) {
                        // load #1
                        // load #2
                        // lcmp
                        // ifeq dest
                        emitReadValue(condition);
                        emitReadValue(inst.getCaseValue(i));
                        addInstruction(new InsnNode(Opcodes.LCMP));
                        BasicBlock dest = inst.getSuccessor(i);
                        if (hasPhi(dest)) {
                            LabelNode phiDestLabel = new LabelNode();
                            hasPhiSuccessor.put(i, phiDestLabel);
                            addInstruction(new JumpInsnNode(Opcodes.IFEQ, phiDestLabel));
                        } else {
                            addInstruction(new JumpInsnNode(Opcodes.IFEQ, getBlockLabel(dest)));
                        }
                    }

                    BasicBlock defaultDest = inst.getDefaultDest();
                    if (hasPhi(defaultDest)) {
                        processPHINode(inst.getParent(), defaultDest);
                    }
                    addInstruction(new JumpInsnNode(Opcodes.GOTO, getBlockLabel(defaultDest)));
                    // put PhiNode here.
                    for (Map.Entry<Integer, LabelNode> entry : hasPhiSuccessor.entrySet()) {
                        int i = entry.getKey();
                        LabelNode label = entry.getValue();
                        addInstruction(label);
                        BasicBlock succ = inst.getSuccessor(i);
                        processPHINode(inst.getParent(), succ);
                        addInstruction(new JumpInsnNode(Opcodes.GOTO, getBlockLabel(succ)));
                    }
                    return;
                }
                throw LLVMToBytecode.unsupportedValue(inst, "condition must be an 32-bit integer");
            }
            emitReadValue(condition);
        }
        {
            // construct bytecode instruction
            BasicBlock defaultDest = inst.getDefaultDest();
            int numCases = inst.getNumCases();
            SwitchPair[] pairs = new SwitchPair[numCases];
            for (int i = 1; i <= numCases; i++) {
                Value caseValue = inst.getCaseValue(i);
                BasicBlock caseDest = inst.getSuccessor(i);
                if (ConstantInt.isa(caseValue)) {
                    pairs[i - 1] = new SwitchPair(Math.toIntExact(ConstantInt.cast(caseValue).getSExtValue()), getBlockLabel(caseDest));
                } else {
                    throw LLVMToBytecode.unsupportedValue(caseValue, "not a supported switch compare value");
                }
            }
            Arrays.sort(pairs);
            int[] keys = new int[numCases];
            LabelNode[] labels = new LabelNode[numCases];
            for (int i = 0; i < numCases; i++) {
                keys[i] = pairs[i].key;
                labels[i] = pairs[i].label;
            }
            addInstruction(new LookupSwitchInsnNode(
                    getBlockLabel(defaultDest), keys, labels
            ));
        }
    }

    static class SwitchPair implements Comparable<SwitchPair> {
        int key;
        LabelNode label;

        public SwitchPair(int key, LabelNode label) {
            this.key = key;
            this.label = label;
        }

        @Override
        public int compareTo(SwitchPair o) {
            return key - o.key;
        }
    }

    private boolean hasPhi(BasicBlock block) {
        try (CXXValueScope scope = new CXXValueScope()) {
            BasicBlock.InstList instList = block.getInstList();
            for (BasicBlock.InstList.Iter iter : instList) {
                if (iter.get().getOpcode() == Opcode.PHI) {
                    return true;
                }
            }
            return false;
        }
    }

    private void emitBranchInst(BranchInst inst) {
        if (inst.isUnconditional()) {
            BasicBlock succ = inst.getSuccessor();
            processPHINode(inst.getParent(), succ);
            LabelNode label = getBlockLabel(succ);
            addInstruction(new JumpInsnNode(Opcodes.GOTO, label));
        } else {
            BasicBlock falseSucc = inst.getFalseSuccessor();
            BasicBlock trueSucc = inst.getTrueSuccessor();
            LabelNode falseLabel = getBlockLabel(falseSucc);
            LabelNode trueLabel = getBlockLabel(trueSucc);

            // read i1 from 8bits store
            emitReadValue(inst.getCondition());

            boolean trueSuccHasPHI = hasPhi(trueSucc);
            boolean falseSuccHasPHI = hasPhi(falseSucc);

            {
                LabelNode trueSuccPHILabel = null;
                if (trueSuccHasPHI) {
                    trueSuccPHILabel = new LabelNode();
                    addInstruction(new JumpInsnNode(Opcodes.IFNE, trueSuccPHILabel));
                } else {
                    addInstruction(new JumpInsnNode(Opcodes.IFNE, trueLabel));
                }
                if (falseSuccHasPHI) {
                    processPHINode(inst.getParent(), falseSucc);
                }
                addInstruction(new JumpInsnNode(Opcodes.GOTO, falseLabel));
                if (trueSuccHasPHI) {
                    addInstruction(trueSuccPHILabel);
                    processPHINode(inst.getParent(), trueSucc);
                    addInstruction(new JumpInsnNode(Opcodes.GOTO, trueLabel));
                }
            }
        }
    }

    protected void emitNull() {
        addInstruction(new InsnNode(Opcodes.ACONST_NULL));
    }

    protected void emitReturnInst(ReturnInst inst) {
        Value returnValue = inst.getReturnValue();
        JavaKind kind = returnValue == null? JavaKind.Void : javaKindOrFail(returnValue);
        switch (kind) {
            case Void:
                addInstruction(new InsnNode(Opcodes.RETURN));
                return;
            case Boolean:
            case Byte:
                emitReadValue(returnValue);
                addInstruction(new InsnNode(Opcodes.I2B));
                addInstruction(new InsnNode(Opcodes.IRETURN));
                return;
            case Short:
                emitReadValue(returnValue);
                addInstruction(new InsnNode(Opcodes.I2S));
                addInstruction(new InsnNode(Opcodes.IRETURN));
                return;
            case Integer:
                emitReadValue(returnValue);
                addInstruction(new InsnNode(Opcodes.IRETURN));
                return;
            case Float:
                emitReadValue(returnValue);
                addInstruction(new InsnNode(Opcodes.FRETURN));
                return;
            case Long:
                emitReadValue(returnValue);
                addInstruction(new InsnNode(Opcodes.LRETURN));
                return;
            case Double:
                emitReadValue(returnValue);
                addInstruction(new InsnNode(Opcodes.DRETURN));
                return;
            case Object:
            case Array:
                if (isConstantPointerNull(returnValue)) {
                    emitNull();
                    return;
                }
                getAccessibleJavaTagOrFail(returnValue);
                emitReadValue(returnValue);
                addInstruction(new InsnNode(Opcodes.ARETURN));
                return;
            default:
                throw LLVMToBytecode.unsupportedValue(inst);
        }
    }


}
