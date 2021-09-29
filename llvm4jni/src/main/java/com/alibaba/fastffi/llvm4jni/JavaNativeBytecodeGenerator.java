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


import com.alibaba.fastffi.llvm.Argument;
import com.alibaba.fastffi.llvm.Function;
import com.alibaba.fastffi.llvm.Value;
import com.alibaba.fastffi.llvm4jni.body.NativeMethod;
import com.alibaba.fastffi.llvm4jni.type.TypeDefException;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.VarInsnNode;

import static com.alibaba.fastffi.llvm4jni.LLVMToBytecode.unsupportedValue;

/**
 * The LLVM function corresponds to a Java native method.
 */
public class JavaNativeBytecodeGenerator extends LLVMBaseBytecodeGenerator {

    public JavaNativeBytecodeGenerator(Universe javaScope, NativeMethod methodBody, Function function) {
        super(javaScope, methodBody, function);
    }

    protected int allocateLocalIndex(Value value) {
        if (value instanceof Argument) {
            Argument arg = (Argument) value;
            if (arg.getArgNo() == 1) {
                if (!isTargetStatic()) {
                    throw new IllegalStateException("Do not allocate index for jobject this");
                }
                return allocateLocalIndex(value, JavaKind.Object);
            }
            Type[] argTypes = Type.getArgumentTypes(methodBody.getMethodNode().desc);
            Type argType = argTypes[arg.getArgNo() - 2];
            return allocateLocalIndex(value, JavaKind.fromAsmType(argType));
        }
        return allocateLocalIndex(value, javaKindOrFail(value));
    }

    public JavaKind getReturnJavaKindOrFail() {
        Type returnType = Type.getReturnType(methodBody.getMethodNode().desc);
        return JavaKind.fromAsmType(returnType);
    }

    public final JavaKind javaKindOrFail(Value value) {
        if (value instanceof Argument) {
            Argument arg = (Argument) value;
            if (arg.getArgNo() == 1) {
                return JavaKind.Object;
            }
            if (arg.getArgNo() == 0) {
                throw LLVMToBytecode.unsupportedValue(value, "Cannot access JNIEnv");
            }
            Type[] argTypes = Type.getArgumentTypes(methodBody.getMethodNode().desc);
            Type argType = argTypes[arg.getArgNo() - 2];
            JavaKind kind = JavaKind.fromAsmType(argType);
            if (kind == JavaKind.Boolean) {
                // jboolean is an alias of unsigned char
                kind = JavaKind.Byte;
            } else if (kind == JavaKind.Character) {
                kind = JavaKind.Short; // jchar is also 16 bits;
            }
            return kind;
        }
        return super.javaKindOrFail(value);
    }

    void collectJNIEntry(Type[] argTypes) {
        try {
            if (isTargetStatic()) {
                Argument arg = function.getArg(1);
                mapJavaTag(arg, JavaTag.jclass(arg, getTypeDef(methodBody.getClassBody().getDescriptor())));
            } else {
                Argument arg = function.getArg(1);
                mapJavaTag(arg, JavaTag.jobject(arg, getTypeDef(methodBody.getClassBody().getDescriptor())));
            }
            for (int i = 0; i < argTypes.length; i++) {
                Type type = argTypes[i];
                if (type.getSort() == Type.OBJECT) {
                    String binaryName = type.getInternalName();
                    Argument arg = function.getArg(i + 2);
                    switch (binaryName) {
                        case "java/lang/String":
                            mapJavaTag(arg, JavaTag.tag(arg, JavaTag.TagType.jstring, getTypeDef(type.getDescriptor())));
                            break;
                        case "java/lang/Class":
                            // TODO: assume the inexact class variable is java.lang.Class
                            mapJavaTagForJClass(arg, getTypeDef("java/lang/Object"));
                            break;
                        default:
                            mapJavaTag(arg, JavaTag.tag(arg, JavaTag.TagType.jobject, getTypeDef(type.getDescriptor())));
                            break;
                    }
                } else if (type.getSort() == Type.ARRAY) {
                    Argument arg = function.getArg(i + 2);
                    int d = type.getDimensions();
                    if (d == 1) {
                        Type elementType = type.getElementType();
                        JavaTag.TagType tagType;
                        switch (elementType.getSort()) {
                            case Type.BOOLEAN:
                                tagType = JavaTag.TagType.jbooleanArray;
                                break;
                            case Type.BYTE:
                                tagType = JavaTag.TagType.jbyteArray;
                                break;
                            case Type.SHORT:
                                tagType = JavaTag.TagType.jshortArray;
                                break;
                            case Type.CHAR:
                                tagType = JavaTag.TagType.jcharArray;
                                break;
                            case Type.INT:
                                tagType = JavaTag.TagType.jintArray;
                                break;
                            case Type.FLOAT:
                                tagType = JavaTag.TagType.jfloatArray;
                                break;
                            case Type.LONG:
                                tagType = JavaTag.TagType.jlongArray;
                                break;
                            case Type.DOUBLE:
                                tagType = JavaTag.TagType.jdoubleArray;
                                break;
                            case Type.OBJECT:
                                tagType = JavaTag.TagType.jobjectArray;
                                break;
                            default:
                                throw new IllegalStateException();
                        }
                        mapJavaTag(arg, JavaTag.tag(arg, tagType, getTypeDef(type.getDescriptor())));
                    } else {
                        mapJavaTag(arg, JavaTag.tag(arg, JavaTag.TagType.jobjectArray, getTypeDef(type.getDescriptor())));
                    }
                }
            }
        } catch (TypeDefException e) {
            // simply ignored
            if (Logger.debug()) e.printStackTrace();
        }
    }

    protected void collectInfo() {
        // all arguments that have a Java type must have a Java tag;
        Type[] argTypes = Type.getArgumentTypes(methodBody.getMethodNode().desc);
        int numArgs = function.getNumArgs();
        if (argTypes.length + 2 != numArgs) {
            throw new IllegalStateException("Function " + function.getName() + " has " + argTypes.length + " arguments, "
                + "but Java native method " + methodBody + " has " + numArgs + " arguments.");
        }
        {
            Argument jniEnv = function.getArg(0);
            mapJavaTag(jniEnv, JavaTag.jniEnv(jniEnv));
        }

        collectJNIEntry(argTypes);

        super.collectInfo();
    }

    protected void buildFrameIndexMapping() {
        super.buildFrameIndexMapping();
        if (isTargetStatic()) {
            // 0: JNIEnv*; 1: jclass;
            Argument jclass = function.getArg(1);
            if (!jclass.useEmpty()) {
                int index = super.getJavaLocalIndex(jclass);
                emitLdc(Type.getObjectType(methodBody.getClassName()));
                addInstruction(new VarInsnNode(Opcodes.ASTORE, index));
            }
        } else {
            // 0: JNIEnv*; 1: jobject;
            Argument jobject = function.getArg(1);
            valueToLocalIndex.put(jobject, 0);
        }
    }

    protected int getJavaLocalIndex(Value value) {
        if (value instanceof Argument) {
            Argument arg = (Argument) value;
            if (arg.getArgNo() == 0) {
                throw new UnsupportedIRException(value,
                        "JNIEnv is not accessible in translated Java methods");
            }
            if (arg.getArgNo() == 1) {
                if (!isTargetStatic()) {
                    if (super.getJavaLocalIndex(arg) != 0) {
                        throw new IllegalStateException();
                    }
                    // this
                    return 0;
                }
            }
        }
        return super.getJavaLocalIndex(value);
    }
}
