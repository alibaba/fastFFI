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


import com.alibaba.fastffi.llvm.Type;
import com.alibaba.fastffi.llvm.Value;

public class UnsupportedIRException extends UnsupportedFunctionException {

    public UnsupportedIRException(Value node) {
        super("Unsupported LLVM IR: " + node);
    }

    public UnsupportedIRException(Value node, String reason) {
        super("Unsupported LLVM IR: " + node + ", reason=" + reason);
    }

    public UnsupportedIRException(Type type) {
        super("Unsupported LLVM Type: " + LLVMToBytecode.printType(type));
    }

    public UnsupportedIRException(Type type, String reason) {
        super("Unsupported LLVM Type: " + LLVMToBytecode.printType(type) + ", reason=" + reason);
    }
}
