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

import org.objectweb.asm.tree.MethodNode;

public class NativeMethod extends MethodBody {

    public NativeMethod(InputClass holder, MethodNode methodNode) {
        super(holder, methodNode);
    }

    @Override
    public InputClass getClassBody() {
        return (InputClass) super.getClassBody();
    }

    public boolean isNative() {
        return true;
    }

    public String getName() {
        return methodNode.name;
    }

    public String getDescriptor() {
        return methodNode.desc;
    }

}
