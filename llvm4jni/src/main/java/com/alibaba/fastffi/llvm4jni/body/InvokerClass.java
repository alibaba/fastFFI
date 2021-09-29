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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

public class InvokerClass extends ClassBody {

    JavaFunctionType functionType;
    MethodNode invokerMethod;

    public InvokerClass(ClassNode classNode, JavaFunctionType functionType) {
        super(classNode);
        this.functionType = functionType;
    }

    public JavaFunctionType getFunctionType() {
        return functionType;
    }

    public MethodNode getInvokerMethod() {
        if (invokerMethod == null) {
            invokerMethod = new MethodNode();
            invokerMethod.name = "invoke";
            invokerMethod.desc = functionType.toDescriptor();
            invokerMethod.access = Modifier.PUBLIC | Modifier.ABSTRACT;
            classNode.methods.add(invokerMethod);
            {
                MethodNode template = new MethodNode();
                template.name = "invokeTemplate";
                String desc = functionType.toDescriptor();
                if (!desc.startsWith("(J")) {
                    throw new IllegalArgumentException("Must have leading function address");
                }
                template.desc = "(" + desc.substring(2);
                template.access = Modifier.PUBLIC | Modifier.ABSTRACT;
                classNode.methods.add(template);
            }
        }
        return invokerMethod;
    }

    @Override
    public boolean needsSaving() {
        return invokerMethod != null;
    }
}
