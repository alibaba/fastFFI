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


import com.alibaba.fastffi.llvm.Function;
import com.alibaba.fastffi.llvm4jni.Universe;
import com.alibaba.fastffi.llvm4jni.Utils;
import com.alibaba.fastffi.llvm4jni.JNINaming;
import com.alibaba.fastffi.llvm4jni.JavaNativeBytecodeGenerator;
import com.alibaba.fastffi.llvm4jni.Logger;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputClass extends ClassBody {

    /**
     * one-to-many mapping between method names and methods.
     */
    Map<String, List<NativeMethod>> nameToMethods = new HashMap<>();

    public InputClass(ClassNode classNode) {
        super(classNode);
    }

    public void addNativeMethod(NativeMethod nativeMethod) {
        Utils.addToMapList(nameToMethods, nativeMethod.getName(), nativeMethod);
    }

    public boolean needsSaving() {
        for (List<NativeMethod> nativeMethods : nameToMethods.values()) {
            for (NativeMethod nativeMethod : nativeMethods) {
                if (nativeMethod.needsSaving()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void lookupNativeMethods(Universe javaScope, Map<String, Function> nameToFunctions) {
        outer: for (Map.Entry<String, List<NativeMethod>> entry : nameToMethods.entrySet()) {
            List<NativeMethod> methods = entry.getValue();
            if (methods.size() == 1) {
                NativeMethod nativeMethod = methods.get(0);
                String nativeName = JNINaming.encodeNativeMethodName(nativeMethod.getClassName(), nativeMethod.getName(), null);
                Function fd = nameToFunctions.get(nativeName);
                if (fd != null) {
                    new JavaNativeBytecodeGenerator(javaScope, nativeMethod, fd).generate();
                    continue outer;
                }
                // fallback to full name
            }
            for (NativeMethod nativeMethod : methods) {
                String nativeName = JNINaming.encodeNativeMethodName(nativeMethod.getClassName(), nativeMethod.getName(), nativeMethod.getDescriptor());
                Function fd = nameToFunctions.get(nativeName);
                if (fd != null) {
                    new JavaNativeBytecodeGenerator(javaScope, nativeMethod, fd).generate();
                } else {
                    Logger.warn("Cannot find native function " + nativeName + " in the LLVM module");
                }
            }
        }
    }
}
