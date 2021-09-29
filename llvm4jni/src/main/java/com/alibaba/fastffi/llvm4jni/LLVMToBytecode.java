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


import com.alibaba.fastffi.llvm.Function;
import com.alibaba.fastffi.llvm.FunctionType;
import com.alibaba.fastffi.llvm.GlobalVariable;
import com.alibaba.fastffi.llvm.IntegerType;
import com.alibaba.fastffi.llvm.PointerType;
import com.alibaba.fastffi.llvm.StructType;
import com.alibaba.fastffi.llvm.Type;
import com.alibaba.fastffi.llvm.TypeID;
import com.alibaba.fastffi.llvm.Value;
import com.alibaba.fastffi.llvm4jni.body.LibraryMethod;
import com.alibaba.fastffi.llvm4jni.body.MethodBody;

import java.util.ArrayList;
import java.util.List;


public class LLVMToBytecode {

    public static JavaKind[] javaKindsNoIllegal(Type[] types) {
        JavaKind[] kinds = new JavaKind[types.length];
        for (int i = 0; i < kinds.length; i++) {
            kinds[i] = javaKindOrFail(types[i]);
        }
        return kinds;
    }

    public static JavaKind[] javaKindsNoIllegal(Iterable<Type> types) {
        List<JavaKind> kinds = new ArrayList<>();
        for (Type type : types) {
            kinds.add(javaKindOrFail(type));
        }
        return kinds.toArray(new JavaKind[0]);
    }

    public static JavaFunctionType getJavaFunctionType(FunctionType functionType) {
        return new JavaFunctionType(javaKindOrFail(functionType.getReturnType()),
                javaKindsNoIllegal(functionType.getParamTypes()), functionType.isVarArg());
    }

    public static JavaFunctionType getJavaFunctionType(JavaKind returnKind, JavaKind[] argumentsKind) {
        return new JavaFunctionType(returnKind, argumentsKind, false);
    }

    public static JavaFunctionType getJavaFunctionType(JavaKind returnKind, JavaKind[] argumentsKind, boolean isVararg) {
        return new JavaFunctionType(returnKind, argumentsKind, isVararg);
    }

    public static JavaKind sameJavaKindOrFail(Value first, Value ... others) {
        JavaKind kind = javaKindOrFail(first);
        for (int i = 0; i < others.length; i++) {
            if (javaKindOrFail(others[i]) != kind) {
                throw unsupportedValue(others[i]);
            }
        }
        return kind;
    }

    public static JavaKind javaKindOrFail(Value value) {
        return javaKindOrFail(value.getType());
    }

    public static JavaKind javaKindOrFail(Type type) {
        JavaKind kind = javaKind(type);
        if (kind == JavaKind.Illegal) {
            throw unsupportedType(type, "cannot get JavaKind for " + type.getTypeID());
        }
        return kind;
    }

    public static RuntimeException unsupportedValue(Value value) {
        return new UnsupportedIRException(value);
    }

    public static RuntimeException unsupportedValue(Value value, String reason) {
        return new UnsupportedIRException(value, reason);
    }

    public static RuntimeException unsupportedType(Type type, String reason) {
        return new UnsupportedIRException(type, reason);
    }

    public static String printType(Type type) {
        if (type instanceof IntegerType) {
            return "int" + ((IntegerType) type).getBitWidth();
        }
        if (type instanceof StructType) {
            StructType structType = (StructType) type;
            String name = structType.getName();
            if (name != null && !name.isEmpty())
                return name;
            StringBuilder sb = new StringBuilder();
            sb.append("struct {");
            int numElements = structType.getNumElements();
            if (numElements > 0) {
                sb.append(printType(structType.getTypeAtIndex(0)));
                for (int i = 1; i < numElements; i++) {
                    sb.append(", ").append(printType(structType.getTypeAtIndex(i)));
                }
            }
            sb.append("}");
            return sb.toString();
        }
        if (type.getTypeID() == TypeID.FloatTyID) {
            return "float";
        }
        if (type.getTypeID() == TypeID.DoubleTyID) {
            return "double";
        }
        if (type instanceof FunctionType) {
            FunctionType functionType = (FunctionType) type;
            StringBuilder sb = new StringBuilder();
            sb.append(printType(functionType.getReturnType()))
                    .append(" (");
            int numParams = functionType.getNumParams();
            if (numParams > 0) {
                sb.append(printType(functionType.getParamType(0)));
                for (int i = 1; i < numParams; i++) {
                    sb.append(", ").append(printType(functionType.getParamType(i)));
                }
            }
            sb.append(")");
            return sb.toString();
        }
        if (type instanceof PointerType) {
            PointerType pointerType = (PointerType) type;
            return printType(pointerType.getPointerElementType()) + "*";
        }
        return type.toString();
    }

    /**
     * This function will identify JNI types as Java managed types.
     * jobject -> object
     * @param type
     * @return
     */
    public static JavaKind javaKind(Type type) {
        TypeID typeID = type.getTypeID();
        switch (typeID) {
            case VoidTyID:
                return JavaKind.Void;
            case HalfTyID:
                return JavaKind.Illegal;
            case FloatTyID:
                return JavaKind.Float;
            case DoubleTyID:
                return JavaKind.Double;
            case X86_FP80TyID:
            case FP128TyID:
            case PPC_FP128TyID:
            case LabelTyID:
            case MetadataTyID:
            case X86_MMXTyID:
            case TokenTyID:
                return JavaKind.Illegal;
            case IntegerTyID:
                IntegerType integerType = (IntegerType) type;
                switch (integerType.getBitWidth()) {
                    case 1:
                        return JavaKind.Boolean;
                    case 8:
                        return JavaKind.Byte;
                    case 16:
                        return JavaKind.Short;
                    case 32:
                        return JavaKind.Integer;
                    case 64:
                        return JavaKind.Long;
                    default:
                        return JavaKind.Illegal;
                }
            case FunctionTyID:
            case StructTyID:
            case ArrayTyID:
                return JavaKind.Illegal;
            case PointerTyID: {
                return JavaKind.Long;
            }
            case FixedVectorTyID:
            case ScalableVectorTyID:
                return JavaKind.Illegal;
        }
        return JavaKind.Illegal;
    }

    public static boolean isSupportJavaType(Type type) {
        return javaKind(type) != JavaKind.Illegal;
    }

    /**
     * varargs are supported via
     * @param type
     * @return
     */
    public static boolean support(FunctionType type) {
        Type returnType = type.getReturnType();
        if (!isSupportJavaType(returnType)) {
            return false;
        }
        for (Type argType : type.getParamTypes()) {
            if (!isSupportJavaType(argType)) {
                return false;
            }
        }
        return true;
    }


    public static String toDescriptor(JavaKind kind) {
        switch (kind) {
            case Void:
                return "V";
            case Byte:
                return "B";
            case Boolean:
                return "Z";
            case Short:
                return "S";
            case Character:
                return "C";
            case Integer:
                return "I";
            case Float:
                return "F";
            case Long:
                return "J";
            case Double:
                return "D";
            default:
                throw new RuntimeException("Do not support Java kind " + kind + " in FFI.");
        }
    }

    public static String getLLVMFunctionMethodDescriptor(Function function) {
        FunctionType functionType = function.getFunctionType();
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Type type : functionType.getParamTypes()) {
            sb.append(toDescriptor(javaKindOrFail(type)));
        }
        sb.append(')');
        sb.append(toDescriptor(javaKindOrFail(functionType.getReturnType())));
        return sb.toString();
    }

    public static JavaFunctionType encodeIndirectFunctionMethodFunctionKind(JavaFunctionType javaFunctionType) {
        JavaKind[] arguments = javaFunctionType.getArgumentKinds();
        JavaKind returnKind = javaFunctionType.getReturnKind();
        JavaKind[] newArg = new JavaKind[arguments.length + 1];
        newArg[0] = JavaKind.Long; // the address of the function
        System.arraycopy(arguments, 0, newArg, 1, arguments.length);
        return new JavaFunctionType(returnKind, newArg, javaFunctionType.isVarargs());
    }

    public static String getIndirectFunctionMethodDescriptor(JavaFunctionType javaFunctionType) {
        StringBuilder sb = new StringBuilder();
        sb.append("(J"); // the first argument is the address of the function
        for (JavaKind kind : javaFunctionType.getArgumentKinds()) {
            sb.append(toDescriptor(kind));
        }
        sb.append(')');
        sb.append(toDescriptor(javaFunctionType.getReturnKind()));
        return sb.toString();
    }

    public static void generateLLVMFunctionMethod(Universe universe, LibraryMethod methodBody, Function function) {
        if (methodBody.methodNode.instructions.size() > 0) {
            return;
        }

        if (methodBody.getStatus() == MethodBody.Status.Failed) {
            throw unsupportedValue(function, "cannot call failed function " + function.getName());
        }

        if (methodBody.needsGeneration() && !function.isDeclaration()) {
            LLVMBaseBytecodeGenerator generator = new LLVMIRFunctionBytecodeGenerator(universe, methodBody, function);
            methodBody.setStatus(MethodBody.Status.Ongoing);
            if (generator.generate()) {
                methodBody.setStatus(MethodBody.Status.Succeeded);
                methodBody.setBytecodeType(LibraryMethod.BytecodeType.Direct);
                return;
            } else {
                methodBody.resetStatus();
            }
        }

        if (!universe.isAvailableInELF(function)) {
            methodBody.setStatus(MethodBody.Status.Failed);
            throw unsupportedValue(function, "cannot call indirect method " + function.getName());
        }

        // fallback
        if (methodBody.getStatus() == MethodBody.Status.None) {
            try {
                methodBody.setStatus(MethodBody.Status.Ongoing);
                LLVMBaseBytecodeGenerator.generateCallIndirectFunctionStubForDirectCall(methodBody, function);
                methodBody.setStatus(MethodBody.Status.Succeeded);
            } catch (UnsupportedFunctionException e) {
                methodBody.setStatus(MethodBody.Status.Failed);
                throw e;
            }
        }
    }

    public static void generateGetFunctionPointer(Universe universe, LibraryMethod methodBody, Function function) {
        if (!methodBody.needsGeneration()) {
            throw new IllegalStateException();
        }
        if (!universe.isAvailableInELF(function)) {
            throw unsupportedValue(function, "Cannot get function pointer of " + function.getName());
        }
        methodBody.setStatus(MethodBody.Status.Ongoing);
        LLVMBaseBytecodeGenerator.generateGetFunctionPointer(methodBody, function);
        methodBody.setBytecodeType(LibraryMethod.BytecodeType.GetFunctionPointer);
        methodBody.setStatus(MethodBody.Status.Succeeded);
    }

    public static void generateIndirectFunctionMethod(Universe universe, LibraryMethod methodBody, FunctionType functionType) {
        generateIndirectFunctionMethod(universe, methodBody, LLVMToBytecode.getJavaFunctionType(functionType));
    }

    public static void generateIndirectFunctionMethod(Universe universe, LibraryMethod methodBody, JavaFunctionType javaFunctionType) {
        if (methodBody.methodNode.instructions.size() > 0) {
            return;
        }
        methodBody.setStatus(MethodBody.Status.Ongoing);
        LLVMBaseBytecodeGenerator.generateCallIndirectFunctionStubForIndirectCall(methodBody, javaFunctionType);
        methodBody.setStatus(MethodBody.Status.Succeeded);
        methodBody.setBytecodeType(LibraryMethod.BytecodeType.Indirect);
    }

    public static void generateConstantInitializer(Universe universe, LibraryMethod methodBody, GlobalVariable globalVariable) {
        if (methodBody.methodNode.instructions.size() > 0) {
            throw new IllegalStateException();
        }
        try {
            methodBody.setStatus(MethodBody.Status.Ongoing);
            LLVMConstantInitializerGenerator.generate(universe, methodBody, globalVariable);
            methodBody.setStatus(MethodBody.Status.Succeeded);
            methodBody.setBytecodeType(LibraryMethod.BytecodeType.ConstantInitializer);
        } catch (UnsupportedFunctionException e) {
            if (Logger.debug()) e.printStackTrace();
            methodBody.setStatus(MethodBody.Status.Failed);
            throw e;
        }
    }
}
