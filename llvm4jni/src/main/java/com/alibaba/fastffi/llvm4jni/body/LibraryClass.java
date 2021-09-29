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
package com.alibaba.fastffi.llvm4jni.body;


import com.alibaba.fastffi.llvm.DataLayout;
import com.alibaba.fastffi.llvm.Function;
import com.alibaba.fastffi.llvm.GlobalVariable;
import com.alibaba.fastffi.llvm.Type;
import com.alibaba.fastffi.llvm4jni.Universe;
import com.alibaba.fastffi.llvm4jni.Utils;
import com.alibaba.fastffi.llvm4jni.JNINaming;
import com.alibaba.fastffi.llvm4jni.JavaFunctionType;
import com.alibaba.fastffi.llvm4jni.JavaKind;
import com.alibaba.fastffi.llvm4jni.LLVMToBytecode;
import com.alibaba.fastffi.llvm4jni.Logger;
import com.alibaba.fastffi.llvm4jni.runtime.ConstantSymbol;
import com.alibaba.fastffi.llvm4jni.runtime.FunctionSymbol;
import com.alibaba.fastffi.llvm4jni.runtime.GlobalSymbol;
import com.alibaba.fastffi.llvm4jni.runtime.InvokerMark;
import com.alibaba.fastffi.llvm4jni.runtime.LibraryClassHelper;
import com.alibaba.fastffi.llvm4jni.runtime.LibrarySymbol;
import com.alibaba.fastffi.llvm4jni.runtime.RuntimeConstant;
import com.alibaba.fastffi.llvm4jni.runtime.RuntimeGlobal;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Java class representing a library.
 * Each supported LLVM global has a static field.
 * Each supported LLVM function has a static method.
 */
public class LibraryClass extends ClassBody {

    static AtomicInteger libraryCounter = new AtomicInteger(0);

    DataLayout dataLayout;
    Path libraryPath;
    Map<String, FieldNode> nameToField = new HashMap<>();
    Map<String, LibraryMethod> nameToMethod = new HashMap<>();
    Map<String, InvokerClass> nameToInvoker = new HashMap<>();
    Universe universe;
    public LibraryClass(Universe universe, DataLayout dataLayout, Path libraryPath) {
        super(new ClassNode());
        this.universe = universe;
        this.dataLayout = dataLayout;
        this.libraryPath = libraryPath;
        initializeClassNode();
    }

    private void initializeClassNode() {
        classNode.name = createLibraryClassName(universe.getModuleName());
        classNode.version = 52;
        classNode.access = Modifier.PUBLIC;
        classNode.superName = "java/lang/Object";
        if (libraryPath != null) {
            classNode.visibleAnnotations = new ArrayList<>(1);
            AnnotationNode annotationNode = new AnnotationNode(Utils.getBinaryDescriptor(LibrarySymbol.class));
            annotationNode.values = Arrays.asList("value", libraryPath.getFileName().toString());
            classNode.visibleAnnotations.add(annotationNode);
        }

        {
            MethodNode defaultConstructor = new MethodNode(Modifier.PUBLIC, "<init>", "()V", null, null);
            classNode.methods.add(defaultConstructor);
            InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
            insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
            insnList.add(new InsnNode(Opcodes.RETURN));
            defaultConstructor.instructions = insnList;
        }
    }

    /**
     * The class initializer performs three actions:
     * <ul>
     *     <li>Invoke LibraryClassHelper.initializeLibraryClass to </li>
     * </ul>
     */
    private void generateClassInitializer() {
        MethodNode classInitializer = new MethodNode(Modifier.STATIC, "<clinit>", "()V", null, null);
        classNode.methods.add(classInitializer);
        InsnList insnList = new InsnList();
        insnList.add(new LdcInsnNode(org.objectweb.asm.Type.getObjectType(classNode.name)));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Utils.getBinaryName(LibraryClassHelper.class),
                "initializeLibraryClass", "(Ljava/lang/Class;)V"));

        /**
         * GlobalVariables are initialized in the order in which they are arranged in the module.
         */
        universe.getModule().forEachGlobal(globalVariable -> {
            String fieldName = getGlobalFieldName(globalVariable);
            FieldNode fieldNode = nameToField.get(fieldName);
            if (fieldNode != null) {
                LibraryMethod constantInitializer = nameToMethod.get(fieldName);
                if (constantInitializer != null && constantInitializer.getStatus() == MethodBody.Status.Succeeded) {
                    MethodNode methodNode = constantInitializer.methodNode;
                    insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, fieldNode.name, fieldNode.desc));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Utils.getBinaryName(RuntimeGlobal.class), "getPointer", "()J"));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, methodNode.name, methodNode.desc));
                }
            }
        });

        insnList.add(new InsnNode(Opcodes.RETURN));
        classInitializer.instructions = insnList;
    }

    public void save(Path root) throws IOException {
        generateClassInitializer();
        for (MethodBody methodBody : nameToMethod.values()) {
            if (methodBody.isGenerated()) {
                MethodNode methodNode = methodBody.methodNode;
                if (methodNode.instructions.size() == 0) {
                    throw new IllegalStateException("Method " +  this + " has no instructions");
                }
                classNode.methods.add(methodNode);
            }
        }
        super.save(root);
    }

    public boolean needsSaving() {
        return !nameToField.isEmpty() || !nameToMethod.isEmpty();
    }

    private String createLibraryClassName(String libraryFileName) {
        return String.format("Library_%08x_%d", libraryFileName.hashCode(), libraryCounter.getAndIncrement());
    }

    /**
     * For global with a symbol in ELF
     * @param global
     * @return
     */
    public FieldNode getOrCreateGlobalField(GlobalVariable global) {
        String fieldName = getGlobalFieldName(global);
        FieldNode fieldNode = nameToField.get(fieldName);
        if (fieldNode == null) {
            fieldNode = allocateGlobalField(fieldName, global);
        }
        return fieldNode;
    }

    /**
     * For constant global that does not have a symbol in ELF
     * @param global
     * @return
     */
    public FieldNode getOrCreateGlobalConstantField(GlobalVariable global) {
        if (!global.isConstant()) {
            throw new IllegalArgumentException("Must be a constant global.");
        }
        String fieldName = getGlobalFieldName(global);
        FieldNode fieldNode = nameToField.get(fieldName);
        if (fieldNode == null) {
            fieldNode = allocateGlobalConstantField(fieldName, global);
        }
        return fieldNode;
    }

    static String removeDot(String name) {
        return name.replace('.', '_');
    }

    private String getGlobalFieldName(GlobalVariable global) {
        return "__LLVM_Global_Field__" + JNINaming.encode(removeDot(global.getName()));
    }

    private String getFunctionFieldName(Function function) {
        return "__LLVM_Function_Field__" + JNINaming.encode(removeDot(function.getName()));
    }

    private String getInvokerFieldName(InvokerClass invokerClass) {
        return "__LLVM_Invoker_Field__" + invokerClass.getFunctionType().toMangledName();
    }

    protected String getGetFunctionPointerMethodName(Function function) {
        return "__LLVM_getAddrOf" + JNINaming.encode(removeDot(function.getName()));
    }

    public FieldNode getOrCreateFunctionField(Function function) {
        String fieldName = getFunctionFieldName(function);
        FieldNode fieldNode = nameToField.get(fieldName);
        if (fieldNode == null) {
            fieldNode = allocateFunctionField(function);
        }
        return fieldNode;
    }


    public FieldNode getOrCreateInvokerField(InvokerClass invokerClass) {
        String fieldName = getInvokerFieldName(invokerClass);
        FieldNode fieldNode = nameToField.get(fieldName);
        if (fieldNode == null) {
            fieldNode = allocateInvokerField(fieldName, invokerClass);
        }
        return fieldNode;
    }

    String getModuleName() {
        return universe.getModuleName();
    }

    private FieldNode allocateInvokerField(String fieldName, InvokerClass invokerClass) {
        FieldNode fieldNode = nameToField.get(fieldName);
        if (fieldNode != null) {
            throw new RuntimeException("Cannot create two fields for the same symbol " + invokerClass.getName() + " in library " + getModuleName());
        }
        fieldNode = new FieldNode(Modifier.STATIC, fieldName, "L" + invokerClass.getName() + ";", null, null);
        classNode.fields.add(fieldNode);
        nameToField.put(fieldName, fieldNode);
        initializeInvokerFieldNode(fieldNode, invokerClass);
        return fieldNode;
    }

    private void initializeInvokerFieldNode(FieldNode fieldNode, InvokerClass invokerClass) {
        AnnotationNode annotationNode = new AnnotationNode(Utils.getBinaryDescriptor(InvokerMark.class));
        annotationNode.values = Collections.emptyList();
        fieldNode.visibleAnnotations = new ArrayList<>(1);
        fieldNode.visibleAnnotations.add(annotationNode);
    }

    private FieldNode allocateFunctionField(Function function) {
        if (!universe.isAvailableInELF(function)) {
            throw new IllegalStateException("Function " + function + " may not be available in ELF.");
        }
        String fieldName = function.getName();
        FieldNode fieldNode = nameToField.get(fieldName);
        if (fieldNode != null) {
            throw new RuntimeException("Cannot create two fields for the same symbol " + function.getName() + " in library " + getModuleName());
        }
        fieldNode = new FieldNode(Modifier.STATIC, fieldName, "J", null, null);
        classNode.fields.add(fieldNode);
        nameToField.put(fieldName, fieldNode);
        initializeFunctionFieldNode(fieldNode, function);
        return fieldNode;
    }

    /**
     * See https://llvm.org/docs/LangRef.html#data-layout
     * 64bit alignment works for almost supported Java types.
     * @param global
     * @param type
     * @return
     */
    private int getAlignment(GlobalVariable global, Type type) {
        return 8;
    }

    public FieldNode allocateGlobalField(String fieldName, GlobalVariable global) {
        if (!universe.isAvailableInELF(global)) {
            throw new IllegalStateException("Global " + global + " may not be available in ELF.");
        }
        FieldNode fieldNode = nameToField.get(fieldName);
        if (fieldNode != null) {
            throw new RuntimeException("Cannot create two fields for the same symbol " + global.getName() + " in library " + getModuleName());
        }
        fieldNode = new FieldNode(Modifier.PUBLIC | Modifier.STATIC, fieldName, "L" + RuntimeGlobal.DESCRIPTOR + ";", null, null);
        classNode.fields.add(fieldNode);
        nameToField.put(fieldName, fieldNode);
        initializeGlobalFieldNode(fieldNode, global);
        return fieldNode;
    }

    public FieldNode allocateGlobalConstantField(String fieldName, GlobalVariable global) {
        FieldNode fieldNode = nameToField.get(fieldName);
        if (fieldNode != null) {
            throw new RuntimeException("Cannot create two fields for the same symbol " + global.getName() + " in library " + getModuleName());
        }
        fieldNode = new FieldNode(Modifier.PUBLIC | Modifier.STATIC, fieldName, "L" + RuntimeConstant.DESCRIPTOR + ";", null, null);
        classNode.fields.add(fieldNode);
        nameToField.put(fieldName, fieldNode);
        Type type = global.getValueType();
        long size = dataLayout.getTypeAllocSize(type);
        int alignment = getAlignment(global, type);
        initializeGlobalConstantFieldNode(fieldNode, global, size, alignment);
        return fieldNode;
    }

    private void initializeGlobalFieldNode(FieldNode fieldNode, GlobalVariable global) {
        AnnotationNode annotationNode = new AnnotationNode(Utils.getBinaryDescriptor(GlobalSymbol.class));
        annotationNode.values = Arrays.asList("value", global.getName());
        fieldNode.visibleAnnotations = new ArrayList<>(1);
        fieldNode.visibleAnnotations.add(annotationNode);
    }

    private void initializeGlobalConstantFieldNode(FieldNode fieldNode, GlobalVariable global, long size, int alignment) {
        fieldNode.visibleAnnotations = new ArrayList<>(2);
        {
            AnnotationNode annotationNode = new AnnotationNode(Utils.getBinaryDescriptor(ConstantSymbol.class));
            annotationNode.values = Arrays.asList("value", global.getName(), "size", size, "alignment", alignment);
            fieldNode.visibleAnnotations.add(annotationNode);
        }
        {
            if (!global.hasInitializer()) {
                throw new IllegalStateException("Not a constant");
            }
            allocateConstantInitializerMethod(global);
        }
    }

    private void initializeFunctionFieldNode(FieldNode fieldNode, Function function) {
        AnnotationNode annotationNode = new AnnotationNode(Utils.getBinaryDescriptor(FunctionSymbol.class));
        annotationNode.values = Arrays.asList("value", function.getName());
        fieldNode.visibleAnnotations = new ArrayList<>(1);
        fieldNode.visibleAnnotations.add(annotationNode);
    }

    public LibraryMethod getOrCreateGetFunctionPointerMethod(Function function) {
        String methodName = getGetFunctionPointerMethodName(function);
        LibraryMethod methodBody = nameToMethod.get(methodName);
        if (methodBody == null) {
            methodBody = allocateGetFunctionPointerMethod(methodName, function);
        }
        return methodBody;
    }

    private LibraryMethod allocateConstantInitializerMethod(GlobalVariable globalVariable) {
        String descriptor = "(J)V";
        String fieldName = getGlobalFieldName(globalVariable);
        String methodName = fieldName;
        LibraryMethod methodBody = nameToMethod.get(methodName);
        if (methodBody != null) {
            throw new RuntimeException("Cannot create two methods for the same symbol " + globalVariable.getName() + " in library " + getModuleName());
        }
        MethodNode methodNode = new MethodNode(Modifier.STATIC | Modifier.PUBLIC, methodName, descriptor, null, null);
        methodBody = new LibraryMethod(this, methodNode, LLVMToBytecode.getJavaFunctionType(JavaKind.Void, new JavaKind[] {JavaKind.Long}, false));
        nameToMethod.put(methodName, methodBody);
        LLVMToBytecode.generateConstantInitializer(universe, methodBody, globalVariable);
        return methodBody;
    }

    private LibraryMethod allocateGetFunctionPointerMethod(String methodName, Function function) {
        String descriptor = "()J";
        LibraryMethod methodBody = nameToMethod.get(methodName);
        if (methodBody != null) {
            throw new RuntimeException("Cannot create two methods for the same symbol " + function.getName() + " in library " + getModuleName());
        }
        MethodNode methodNode = new MethodNode(Modifier.STATIC | Modifier.PUBLIC, methodName, descriptor, null, null);
        methodBody = new LibraryMethod(this, methodNode, LLVMToBytecode.getJavaFunctionType(JavaKind.Long, new JavaKind[0], false));
        nameToMethod.put(methodName, methodBody);
        LLVMToBytecode.generateGetFunctionPointer(universe, methodBody, function);
        return methodBody;
    }

    public LibraryMethod getOrCreateDirectFunctionMethod(Function function) {
        String methodName = function.getName();
        LibraryMethod methodBody = nameToMethod.get(methodName);
        if (methodBody == null) {
            methodBody = allocateDirectFunctionMethod(function);
        }
        return methodBody;
    }

    private LibraryMethod allocateDirectFunctionMethod(Function function) {
        String descriptor = LLVMToBytecode.getLLVMFunctionMethodDescriptor(function);
        String methodName = function.getName();
        LibraryMethod methodBody = nameToMethod.get(methodName);
        if (methodBody != null) {
            throw new RuntimeException("Cannot create two methods for the same symbol " + function.getName() + " in library " + getModuleName());
        }
        MethodNode methodNode = new MethodNode(Modifier.STATIC | Modifier.PUBLIC, methodName, descriptor, null, null);
        //classNode.methods.add(methodNode);
        methodBody = new LibraryMethod(this, methodNode, LLVMToBytecode.getJavaFunctionType(function.getFunctionType()));
        nameToMethod.put(methodName, methodBody);
        return methodBody;
    }

    private LibraryMethod allocateIndirectFunctionMethod(String methodName, JavaFunctionType javaFunctionType) {
        String descriptor = LLVMToBytecode.getIndirectFunctionMethodDescriptor(javaFunctionType);
        LibraryMethod methodBody = nameToMethod.get(methodName);
        if (methodBody != null) {
            throw new RuntimeException("Cannot create two methods for the same symbol " + methodName + " in library " + getModuleName());
        }
        MethodNode methodNode = new MethodNode(Modifier.STATIC | Modifier.PUBLIC, methodName, descriptor, null, null);
        //classNode.methods.add(methodNode);
        methodBody = new LibraryMethod(this, methodNode, LLVMToBytecode.encodeIndirectFunctionMethodFunctionKind(javaFunctionType));
        nameToMethod.put(methodName, methodBody);
        return methodBody;
    }

    protected String getIndirectFunctionMethodName(JavaFunctionType javaFunctionType) {
        return "__LLVM_Indirect_" + javaFunctionType.toMangledName();
    }

    /**
     * Every invoker class is an inner interface of the given library.
     * @param javaFunctionType
     * @return
     */
    protected String getInvokerClassName(JavaFunctionType javaFunctionType) {
        return this.getName() + "$Invoker" + javaFunctionType.toMangledName();
    }

    /**
     *
     * @param javaFunctionType does not include the function address
     * @return
     */
    public LibraryMethod getOrCreateIndirectFunctionMethod(JavaFunctionType javaFunctionType) {
        String methodName = getIndirectFunctionMethodName(javaFunctionType);
        LibraryMethod methodBody = nameToMethod.get(methodName);
        if (methodBody == null) {
            methodBody = allocateIndirectFunctionMethod(methodName, javaFunctionType);
        }
        return methodBody;
    }

    /**
     *
     * @param javaFunctionType include the function address
     * @return
     */
    public InvokerClass getOrCreateInvokerClass(JavaFunctionType javaFunctionType) {
        String invokerClassName = getInvokerClassName(javaFunctionType);
        InvokerClass invokerClass = nameToInvoker.get(invokerClassName);
        if (invokerClass == null) {
            ClassNode invokerClassNode = new ClassNode();
            String innerClassSimpleName = invokerClassName.substring(invokerClassName.lastIndexOf("$"));
            invokerClassNode.name = invokerClassName;
            invokerClassNode.version = 52;
            invokerClassNode.superName = "java/lang/Object";
            invokerClassNode.access = Modifier.PUBLIC | Modifier.INTERFACE | Modifier.ABSTRACT;
            invokerClass = new InvokerClass(invokerClassNode,
                    LLVMToBytecode.encodeIndirectFunctionMethodFunctionKind(javaFunctionType));
            classNode.innerClasses.add(new InnerClassNode(invokerClassName, getName(), innerClassSimpleName, Modifier.PUBLIC));
            nameToInvoker.put(invokerClassName, invokerClass);
        }
        return invokerClass;
    }

    public void dump() {
        if (libraryPath != null) {
            Logger.debug("@LibrarySymbol(" + libraryPath.getFileName() + ")");
        }
        Logger.debug(classNode.name);
        Logger.debug("  Fields:");
        for (FieldNode fieldNode : nameToField.values()) {
            Logger.debug("    %s %s", fieldNode.desc, fieldNode.name);
        }
        Logger.debug("  Methods:");
        for (LibraryMethod methodBody : nameToMethod.values()) {
            MethodNode methodNode = methodBody.getMethodNode();
            Logger.debug("    %s%s (%s)(%s)", methodNode.name, methodNode.desc,
                    methodBody.getBytecodeType(),
                    methodBody.getStatus());
        }
        Logger.debug("  Invokers:");
        for (InvokerClass invokerClass : nameToInvoker.values()) {
            Logger.debug("    @Invoker(\"%s\") %s", invokerClass.getFunctionType().toDescriptor(), invokerClass.getName());
        }
    }

    public Collection<InvokerClass> getInvokerClasses() {
        return this.nameToInvoker.values();
    }

    public Path getLibraryPath() {
        return libraryPath;
    }
}
