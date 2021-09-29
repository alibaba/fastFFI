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
import com.alibaba.fastffi.llvm.Argument;
import com.alibaba.fastffi.llvm.BasicBlock;
import com.alibaba.fastffi.llvm.BitCastInst;
import com.alibaba.fastffi.llvm.CallBase;
import com.alibaba.fastffi.llvm.Function;
import com.alibaba.fastffi.llvm.FunctionType;
import com.alibaba.fastffi.llvm.Instruction;
import com.alibaba.fastffi.llvm.IntToPtrInst;
import com.alibaba.fastffi.llvm.Opcode;
import com.alibaba.fastffi.llvm.PtrToIntInst;
import com.alibaba.fastffi.llvm.Type;
import com.alibaba.fastffi.llvm.Value;
import com.alibaba.fastffi.llvm4jni.body.InvokerClass;
import com.alibaba.fastffi.llvm4jni.body.LibraryClass;
import com.alibaba.fastffi.llvm4jni.body.LibraryMethod;
import com.alibaba.fastffi.llvm4jni.body.MethodBody;
import com.alibaba.fastffi.llvm4jni.runtime.JavaRuntime;
import com.alibaba.fastffi.llvm4jni.runtime.LibraryClassHelper;
import com.alibaba.fastffi.llvm4jni.runtime.Stack;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static com.alibaba.fastffi.llvm4jni.LLVMToBytecode.unsupportedValue;

/**
 * A bytecode generate for LLVM IR function
 */
public abstract class LLVMBaseBytecodeGenerator extends AbstractBytecodeGenerator {

    protected MethodBody methodBody;
    protected MethodNode wrappeeMethod;
    protected List<AbstractInsnNode> wrapperInstructions;

    protected Function function;
    public LLVMBaseBytecodeGenerator(Universe javaScope, MethodBody methodBody, Function function) {
        super(javaScope);
        this.function = function;
        this.methodBody = methodBody;
    }

    public boolean generate() {
        try (CXXValueScope scope = new CXXValueScope()) {
            if (!sanityCheck()) {
                Logger.warn("Cannot support function " + getTargetMethodName());
                return false;
            }
            collectInfo();
            buildFrameIndexMapping();
            visit(function);
            int installedSize = installBytecode(instructions);
            Logger.info("Successfully generate code for " + function.getName() +
                    ", code size (before optimization=" + instructions.size() +
                    ", after optimization=" + installedSize +
                    "), needsStackPointer=" + needStackPointer);
            return true;
        } catch (UnsupportedIRException e) {
            Logger.warn("Cannot generate code for " + function.getName() + " due to " + e.getMessage());
            return false;
        } catch (UnsupportedFunctionException e) {
            Logger.warn("Cannot generate code for " + function.getName() + " due to " + e.getMessage());
            return false;
        }
    }

    protected boolean supportIndirectCall(CallBase inst) {
        if (methodBody.isNative()) {
            List<AnnotationNode> annotationNodes = methodBody.methodNode.visibleAnnotations;
            if (annotationNodes != null && !annotationNodes.isEmpty()) {
                for (AnnotationNode node : annotationNodes) {
                    if (node != null
                            && node.desc != null
                            && node.desc.equals("Lcom/alibaba/ffi/CXXOperator;")) {
                        for (int i = 0; i < node.values.size();) {
                            String name = (String) node.values.get(i++);
                            Object value = node.values.get(i++);
                            if (name.equals("value") && value.equals("delete")) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return super.supportIndirectCall(inst);
    }

    protected boolean sanityCheck() {
        FunctionType type = function.getFunctionType();
        if (!LLVMToBytecode.support(type)) {
            Logger.warn("Cannot support function " + function);
            return false;
        }
        return true;
    }


    /**
     * TODO: Build call graph and do inter-procedural analysis
     */
    protected void collectInfo() {
        forEachInstruction(this::collectInfo);
    }

    protected void forEachInstruction(Consumer<Instruction> consumer) {
        function.forEachBasicBlock(basicBlock -> {
            basicBlock.forEachInstruction(consumer);
        });
    }

    private void visit(Function function) {
        Logger.debug("Visit Function: " + function.getName());
        function.forEachBasicBlock(b -> visit(b));
    }

    private void visit(BasicBlock basicBlock) {
        BasicBlock.InstList instList = basicBlock.getInstList();
        if (!options.supportInvokeUnwind()) {
            try (CXXValueScope scope = new CXXValueScope()) {
                Iterator<BasicBlock.InstList.Iter> iterator = instList.iterator();
                while (iterator.hasNext()) {
                    Instruction inst = iterator.next().get();
                    if (inst.getOpcode() == Opcode.LandingPad) {
                        return; //
                    }
                }
            }
        }
        addInstruction(getBlockLabel(basicBlock));

        Logger.debug("Visit BasicBlock: " + basicBlock.hashCode());
        basicBlock.forEachInstruction(instruction -> visit(instruction));
    }

    /**
     * Whether the target Java method is static or not.
     * @return
     */
    protected final boolean isTargetStatic() {
        return methodBody.isStatic();
    }

    private void createWrappeeMethodNode(List<AbstractInsnNode> bytecode) {
        // invoke wrappee
        wrappeeMethod = new MethodNode();
        wrappeeMethod.desc = "(J" + methodBody.methodNode.desc.substring(1);
        wrappeeMethod.name = methodBody.methodNode.name + "_LLVM_";
        wrappeeMethod.exceptions = Collections.emptyList();
        wrappeeMethod.access = methodBody.methodNode.access;
        wrappeeMethod.instructions = toInsnList(bytecode);
        if (wrappeeMethod.instructions.size() > options.maximumBytecodeSize()) {
            throw new UnsupportedFunctionException("Function is too large: " + wrappeeMethod.instructions.size());
        }
    }

    public String getWrappeeMethodName() {
        return getWrapperMethodName() + "_LLVM_";
    }
    public String getWrapperMethodName() {
        return methodBody.methodNode.name;
    }
    public String getTargetMethodName() {
        if (needStackPointer) {
            return getWrappeeMethodName();
        }
        return getWrapperMethodName();
    }

    public int getArgumentSize() {
        int numArgs = function.getNumArgs();
        if (isTargetNative()) {
            if (numArgs < 2) {
                throw new IllegalStateException("A Java native method must have JNIEnv* and (jclass or jobject) as arguments");
            }
        }

        int argIndex = 0;
        if (!isTargetStatic()) {
            argIndex++; // skip receiver
        }
        int beginIndex = isTargetNative() ? 2 : 0;
        for (int i = beginIndex; i < numArgs; i++) {
            JavaKind argKind = javaKindOrFail(function.getArg(i));
            switch (argKind) {
                case Byte:
                case Boolean:
                case Short:
                case Integer:
                    argIndex++;
                    break;
                case Float:
                    argIndex++;
                    break;
                case Long:
                    argIndex += 2;
                    break;
                case Double:
                    argIndex += 2;
                    break;
                case Object:
                case Array:
                    assert isTargetNative();
                    argIndex +=1;
                    break;
                default:
                    throw new IllegalArgumentException("Illegal argument type " + argKind + " for argument " + i);
            }
        }
        return argIndex;
    }

    /**
     * Whether the target Java method is native or not.
     * @return
     */
    protected final boolean isTargetNative() {
        return methodBody.isNative();
    }

    /**
     * int foo(int a1, int a2) {
     *   StackPointer sp;
     *   try () {
     *     return wrappee((sp = pushFrame()), a1, a2);
     *   } finally {
     *     popFrame();
     *   }
     * }
     * @return
     */
    private void createWrapperMethod() {
        wrapperInstructions = new ArrayList<>();
        LabelNode tryBegin = new LabelNode();
        LabelNode tryEnd = new LabelNode();
        LabelNode tryHandler = new LabelNode();

        int stackIndex = getArgumentSize();
        int basePointerIndex = stackIndex + 1;

        if (!needStackPointer) {
            throw new IllegalStateException("Sanity check");
        }

        String stackBinaryName = Stack.class.getName().replace('.', '/');
        // 1. getStack first
        wrapperInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, JavaRuntime.DESCRIPTOR, "getStack", "()L" + stackBinaryName + ";"));
        wrapperInstructions.add(new VarInsnNode(Opcodes.ASTORE, stackIndex));

        // 2. allocate frame
        long frameSize = -unallocatedAllocaOffset;
        if (frameSize <= 0) {
            throw LLVMToBytecode.unsupportedValue(function, "Invalid frame size " + frameSize);
        }
        wrapperInstructions.add(new VarInsnNode(Opcodes.ALOAD, stackIndex));
        wrapperInstructions.add(new LdcInsnNode(frameSize));
        wrapperInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, stackBinaryName,
                "pushFrame", "(J)J"));
        wrapperInstructions.add(new VarInsnNode(Opcodes.LSTORE, basePointerIndex));

        FunctionType functionType = function.getFunctionType();
        int numParams = functionType.getNumParams();
        if (isTargetNative()) {
            if (numParams < 2) {
                throw new IllegalStateException("A Java native method must have JNIEnv* and (jclass or jobject) as arguments");
            }
        }

        // Begin try-catch-block
        wrapperInstructions.add(tryBegin);

        int argIndex = 0;
        if (!methodBody.isStatic()) {
            // load receiver
            wrapperInstructions.add(new VarInsnNode(Opcodes.ALOAD, argIndex));
            argIndex ++;
        }

        // first passed parameter is the base pointer
        wrapperInstructions.add(new VarInsnNode(Opcodes.LLOAD, basePointerIndex));

        // skip JNIEnv and jclass or jobject for JNI native method
        int beginIndex = isTargetNative() ? 2 : 0;

        for (int i = beginIndex; i < numParams; i++) {
            Type argType = functionType.getParamType(i);
            // TODO: skip JNIEnv and jclass or jobject
            JavaKind argKind = javaKindOrFail(argType);
            switch (argKind) {
                case Byte:
                case Boolean:
                case Short:
                case Integer:
                    wrapperInstructions.add(new VarInsnNode(Opcodes.ILOAD, argIndex));
                    argIndex++;
                    break;
                case Float:
                    wrapperInstructions.add(new VarInsnNode(Opcodes.FLOAD, argIndex));
                    argIndex++;
                    break;
                case Long:
                    wrapperInstructions.add(new VarInsnNode(Opcodes.LLOAD, argIndex));
                    argIndex += 2;
                    break;
                case Double:
                    wrapperInstructions.add(new VarInsnNode(Opcodes.DLOAD, argIndex));
                    argIndex += 2;
                    break;
                default:
                    throw new IllegalArgumentException("Illegal argument type " + argType);
            }
        }

        // TODO: This is implementation dependent
        if (stackIndex != argIndex) {
            throw new IllegalStateException("Bad arguments");
        }
        argIndex ++; // skip stack
        argIndex += 2; // skip base pointer

        int invokeOpcode = Opcodes.INVOKESTATIC;
        if (!methodBody.isStatic()) {
            invokeOpcode = Opcodes.INVOKESPECIAL;
        }

        wrapperInstructions.add(new MethodInsnNode(invokeOpcode, methodBody.getClassBody().getName(), wrappeeMethod.name, wrappeeMethod.desc, false));

        JavaKind returnKind = javaKindOrFail(functionType.getReturnType());
        // TODO: check java signature
        // save return value;
        int retIndex = argIndex;
        switch (returnKind) {
            case Void:
                break;
            case Byte:
            case Boolean:
            case Short:
            case Integer:
                wrapperInstructions.add(new VarInsnNode(Opcodes.ISTORE, retIndex));
                argIndex++;
                break;
            case Float:
                wrapperInstructions.add(new VarInsnNode(Opcodes.FSTORE, retIndex));
                argIndex++;
                break;
            case Long:
                wrapperInstructions.add(new VarInsnNode(Opcodes.LSTORE, retIndex));
                argIndex += 2;
                break;
            case Double:
                wrapperInstructions.add(new VarInsnNode(Opcodes.DSTORE, retIndex));
                argIndex += 2;
                break;
            default:
                throw new RuntimeException();
        }

        wrapperInstructions.add(tryEnd);
        int expIndex = argIndex;

        wrapperInstructions.add(new VarInsnNode(Opcodes.ALOAD, stackIndex));
        wrapperInstructions.add(new VarInsnNode(Opcodes.LLOAD, basePointerIndex));
        wrapperInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, stackBinaryName, "popFrame", "(J)V"));

        // TODO: check java signature
        switch (returnKind) {
            case Void:
                wrapperInstructions.add(new InsnNode(Opcodes.RETURN));
                break;
            case Byte:
            case Boolean:
            case Short:
            case Integer:
                wrapperInstructions.add(new VarInsnNode(Opcodes.ILOAD, retIndex));
                wrapperInstructions.add(new InsnNode(Opcodes.IRETURN));
                break;
            case Float:
                wrapperInstructions.add(new VarInsnNode(Opcodes.FLOAD, retIndex));
                wrapperInstructions.add(new InsnNode(Opcodes.FRETURN));
                break;
            case Long:
                wrapperInstructions.add(new VarInsnNode(Opcodes.LLOAD, retIndex));
                wrapperInstructions.add(new InsnNode(Opcodes.LRETURN));
                break;
            case Double:
                wrapperInstructions.add(new VarInsnNode(Opcodes.DLOAD, retIndex));
                wrapperInstructions.add(new InsnNode(Opcodes.DRETURN));
                break;
            default:
                throw new RuntimeException();
        }

        // generate catch and throw
        {
            wrapperInstructions.add(tryHandler);
            // save exception
            wrapperInstructions.add(new VarInsnNode(Opcodes.ASTORE, expIndex));
            // load Stack
            wrapperInstructions.add(new VarInsnNode(Opcodes.ALOAD, stackIndex));
            // load saved base pointer
            wrapperInstructions.add(new VarInsnNode(Opcodes.LLOAD, basePointerIndex));
            wrapperInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, stackBinaryName, "popFrame", "(J)V"));
            wrapperInstructions.add(new VarInsnNode(Opcodes.ALOAD, expIndex));
            wrapperInstructions.add(new InsnNode(Opcodes.ATHROW));
            methodBody.methodNode.tryCatchBlocks = new ArrayList<>();
            // type is null to catch anything
            methodBody.methodNode.tryCatchBlocks.add(new TryCatchBlockNode(tryBegin, tryEnd, tryHandler, null));
        }
    }

    protected int installBytecode(List<AbstractInsnNode> bytecode) {
        if (needStackPointer) {
            createWrappeeMethodNode(bytecode);
            createWrapperMethod();
            methodBody.setInsnList(toInsnList(wrapperInstructions), wrappeeMethod);
            return wrappeeMethod.instructions.size();
        } else {
            InsnList insnList = toInsnList(bytecode);
            if (insnList.size() > options.maximumBytecodeSize()) {
                throw new UnsupportedFunctionException("Function is too large: " + insnList.size());
            }
            methodBody.setInsnList(insnList);
            return insnList.size();
        }
    }

    protected int getStackBasePointerLocalIndex() {
        if (needStackPointer) {
            return isTargetStatic() ? 0 : 1;
        }
        throw new IllegalStateException("Cannot get stack pointer when it is not needed.");
    }

    /**
     */
    protected void buildFrameIndexMapping() {
        unallocatedLocalIndex = 0; // start with 0
        if (!isTargetStatic()) {
            unallocatedLocalIndex += 1; // receiver use the first index
        }
        if (needStackPointer) {
            // a long represented the base pointer
            unallocatedLocalIndex += 2;
        }
        int beginArgIndex = isTargetNative() ? 2 : 0;
        int numArgs = function.getNumArgs();
        if (beginArgIndex > numArgs) {
            throw new IllegalStateException("Require at least " + beginArgIndex + " args but only have " + numArgs);
        }
        for (int i = beginArgIndex; i < numArgs; i++) {
            Argument argument = function.getArg(i);
            allocateLocalIndex(argument);
        }
        forEachInstruction(inst -> {
            {
                Opcode opcode = inst.getOpcode();
                switch (opcode) {
                    case PtrToInt: {
                        PtrToIntInst casted = (PtrToIntInst) inst;
                        Value from = casted.getOperand(0);
                        if (javaKindOrFail(from) == JavaKind.Long) {
                            int fromIndex = getJavaLocalIndex(from);
                            valueToLocalIndex.put(casted, fromIndex);
                        }
                        break;
                    }
                    case IntToPtr:{
                        IntToPtrInst casted = (IntToPtrInst) inst;
                        Value from = casted.getOperand(0);
                        if (javaKindOrFail(from) == JavaKind.Long) {
                            int fromIndex = getJavaLocalIndex(from);
                            valueToLocalIndex.put(casted, fromIndex);
                        }
                        break;
                    }
                    case BitCast: {
                        BitCastInst casted = (BitCastInst) inst;
                        JavaKind srcKind = javaKindOrFail(casted.getSrcTy());
                        JavaKind dstKind = javaKindOrFail(casted.getDestTy());
                        if (srcKind == dstKind) {
                            Value from = casted.getOperand(0);
                            int fromIndex = getJavaLocalIndex(from);
                            valueToLocalIndex.put(casted, fromIndex);
                        }
                        break;
                    }
                }
            }
        });
    }

    public static void generateGetFunctionPointer(LibraryMethod methodBody, Function function) {
        LibraryClass libraryClass = methodBody.getClassBody();
        FieldNode functionPointerField = libraryClass.getOrCreateFunctionField(function);
        List<AbstractInsnNode> instructions = new ArrayList<>();
        instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, libraryClass.getName(), functionPointerField.name, functionPointerField.desc));
        instructions.add(new InsnNode(Opcodes.LCONST_0));
        instructions.add(new InsnNode(Opcodes.LCMP));
        LabelNode falseLabel = new LabelNode();
        instructions.add(new JumpInsnNode(Opcodes.IFNE, falseLabel));
        Path libraryPath = libraryClass.getLibraryPath();
        if (libraryPath != null) {
            instructions.add(new LdcInsnNode(libraryPath.getFileName().toString()));
        } else {
            instructions.add(new InsnNode(Opcodes.ACONST_NULL));
        }
        instructions.add(new LdcInsnNode(function.getName()));
        instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Utils.getBinaryName(LibraryClassHelper.class), "getSymbolAddress", "(Ljava/lang/String;Ljava/lang/String;)J"));
        instructions.add(new FieldInsnNode(Opcodes.PUTSTATIC, libraryClass.getName(), functionPointerField.name, functionPointerField.desc));
        instructions.add(falseLabel);
        instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, libraryClass.getName(), functionPointerField.name, functionPointerField.desc));
        instructions.add(new InsnNode(Opcodes.LRETURN));
        methodBody.setInsnList(InstructionOptimizer.toInsnList(instructions));
    }

    public static void generateCallIndirectFunctionStubForDirectCall(LibraryMethod methodBody, Function function) {
        LibraryClass libraryClass = methodBody.getClassBody();
        FunctionType functionType = function.getFunctionType();
        if (functionType.isVarArg()) {
            throw new IllegalArgumentException("TODO: All vararg are called indirectly.");
        }
        JavaFunctionType javaFunctionType = LLVMToBytecode.getJavaFunctionType(functionType);
        generateCallIndirectFunctionStubCommon(methodBody, javaFunctionType, instructions -> {
            LibraryMethod getFunctionPointerMethod = libraryClass.getOrCreateGetFunctionPointerMethod(function);
            instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, libraryClass.getName(),
                    getFunctionPointerMethod.methodNode.name, getFunctionPointerMethod.methodNode.desc));
        });
        methodBody.setBytecodeType(LibraryMethod.BytecodeType.ExternalDirect);
    }

    public static void generateCallIndirectFunctionStubForIndirectCall(LibraryMethod methodBody, JavaFunctionType javaFunctionType) {
        generateCallIndirectFunctionStubCommon(methodBody, javaFunctionType, instructions -> {});
    }

    private static void generateCallIndirectFunctionStubCommon(LibraryMethod methodBody, JavaFunctionType javaFunctionType, Consumer<List<AbstractInsnNode>> loadFunctionAddress) {
        List<AbstractInsnNode> instructions = new ArrayList<>();
        LibraryClass libraryClass = methodBody.getClassBody();

        // getstatic: fetch the invoker
        InvokerClass invokerClass = libraryClass.getOrCreateInvokerClass(javaFunctionType);
        FieldNode invokerField = libraryClass.getOrCreateInvokerField(invokerClass);
        instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, libraryClass.getName(), invokerField.name, invokerField.desc));

        loadFunctionAddress.accept(instructions);

        // Load arguments
        JavaKind[] argumentTypes = methodBody.getFunctionType().getArgumentKinds();
        int argIndex = 0;
        for (int i = 0; i < argumentTypes.length; i++) {
            JavaKind argKind = argumentTypes[i];
            switch (argKind) {
                case Byte:
                case Boolean:
                case Short:
                case Integer:
                    instructions.add(new VarInsnNode(Opcodes.ILOAD, argIndex));
                    argIndex++;
                    break;
                case Float:
                    instructions.add(new VarInsnNode(Opcodes.FLOAD, argIndex));
                    argIndex++;
                    break;
                case Long:
                    instructions.add(new VarInsnNode(Opcodes.LLOAD, argIndex));
                    argIndex += 2;
                    break;
                case Double:
                    instructions.add(new VarInsnNode(Opcodes.DLOAD, argIndex));
                    argIndex += 2;
                    break;
                default:
                    throw new IllegalArgumentException("Illegal argument type " + argKind);
            }
        }

        // invoke invoker
        MethodNode invoker = invokerClass.getInvokerMethod();
        instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, invokerClass.getName(), invoker.name, invoker.desc, true));

        JavaKind returnKind = methodBody.getFunctionType().getReturnKind();
        switch (returnKind) {
            case Void:
                instructions.add(new InsnNode(Opcodes.RETURN));
                break;
            case Byte:
            case Boolean:
            case Short:
            case Integer:
                instructions.add(new InsnNode(Opcodes.IRETURN));
                break;
            case Float:
                instructions.add(new InsnNode(Opcodes.FRETURN));
                break;
            case Long:
                instructions.add(new InsnNode(Opcodes.LRETURN));
                break;
            case Double:
                instructions.add(new InsnNode(Opcodes.DRETURN));
                break;
            default:
                throw new RuntimeException();
        }
        methodBody.setInsnList(InstructionOptimizer.toInsnList(instructions));
    }
}
