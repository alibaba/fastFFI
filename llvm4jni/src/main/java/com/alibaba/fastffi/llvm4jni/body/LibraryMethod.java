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

import com.alibaba.fastffi.llvm4jni.JavaFunctionType;
import org.objectweb.asm.tree.MethodNode;

/**
 * Each (supported) LLVM function (excepted JNI function)
 * has a corresponding library method.
 */
public class LibraryMethod extends MethodBody {

    public enum BytecodeType {
        /**
         * A fully translated LLVM IR function
         */
        Direct,
        /**
         * A fall back indirect call to external
         */
        ExternalDirect,
        /**
         * Call to a function pointer
         */
        Indirect,
        /**
         *
         */
        GetFunctionPointer,
        /**
         *
         */
        ConstantInitializer
        ;
    }

    BytecodeType bytecodeType;
    JavaFunctionType functionType;
    public LibraryMethod(LibraryClass classBody, MethodNode methodNode, JavaFunctionType functionType) {
        super(classBody, methodNode);
        this.functionType = functionType;
    }

    public void setBytecodeType(BytecodeType bytecodeType) {
        if (this.bytecodeType != null) {
            throw new IllegalStateException();
        }
        this.bytecodeType = bytecodeType;
    }

    public BytecodeType getBytecodeType() {
        return this.bytecodeType;
    }

    public JavaFunctionType getFunctionType() {
        return functionType;
    }

    @Override
    public LibraryClass getClassBody() {
        return (LibraryClass) super.getClassBody();
    }

    public boolean isNative() {
        return false;
    }

}
