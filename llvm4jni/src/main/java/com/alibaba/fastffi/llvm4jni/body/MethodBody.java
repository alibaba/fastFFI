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


import com.alibaba.fastffi.llvm4jni.InstructionOptimizer;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

public class MethodBody {

    public enum Status {
        None,
        Ongoing,
        Succeeded,
        Failed;
    }

    public final ClassBody classBody;
    public final MethodNode methodNode;
    MethodNode wrappeeMethodNode;

    Status status = Status.None;

    boolean needsSaving;

    public MethodBody(ClassBody classBody, MethodNode methodNode) {
        this.classBody = classBody;
        this.methodNode = methodNode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        switch (status) {
            case Ongoing:
                if (this.status == Status.None) {
                    this.status = status;
                    return;
                }
                break;
            case Succeeded:
                if (this.status == Status.Ongoing) {
                    this.status = status;
                    return;
                }
                break;
            case Failed:
                if (this.status == Status.Ongoing || this.status == Status.None) {
                    this.status = status;
                    return;
                }
            default:
        }
        throw new IllegalStateException("Cannot transit from " + this.status + " to " + status);
    }

    public boolean needsGeneration() {
        return this.status == Status.None;
    }

    public boolean isGenerated() {
        return this.status == Status.Succeeded;
    }

    public String toString() {
        return String.format("%s::%s%s", classBody.classNode.name, methodNode.name, methodNode.desc);
    }

    public void resetStatus() {
        this.status = Status.None;
    }


    public boolean needsSaving() {
        return needsSaving;
    }

    public ClassBody getClassBody() {
        return classBody;
    }

    public String getClassName() {
        return getClassBody().getName();
    }

    public boolean isNative() {
        return Modifier.isNative(methodNode.access);
    }

    public MethodNode getMethodNode() {
        return methodNode;
    }

    public boolean isStatic() {
        return Modifier.isStatic(methodNode.access);
    }

    public void setInsnList(InsnList insnList) {
        if (methodNode.instructions.size() != 0) {
            throw new IllegalStateException("Already have instructions");
        }
        methodNode.access = methodNode.access & ~Modifier.NATIVE;
        methodNode.access = methodNode.access & ~Modifier.ABSTRACT;
        methodNode.instructions = insnList;

        // post optimization
        InstructionOptimizer.reallocateVarIndex(this.methodNode);
        needsSaving = true;
    }

    /**
     * This method need a wrappee method node
     * @param insnList
     * @param wrappeeMethodNode
     */
    public void setInsnList(InsnList insnList, MethodNode wrappeeMethodNode) {
        if (methodNode.instructions.size() != 0) {
            throw new IllegalStateException("Already have instructions");
        }
        methodNode.access = methodNode.access & ~Modifier.NATIVE;
        methodNode.access = methodNode.access & ~Modifier.ABSTRACT;
        methodNode.instructions = insnList;

        this.wrappeeMethodNode = wrappeeMethodNode;
        this.wrappeeMethodNode.access = methodNode.access;
        this.classBody.classNode.methods.add(wrappeeMethodNode);

        // post optimization
        InstructionOptimizer.reallocateVarIndex(this.methodNode);
        InstructionOptimizer.reallocateVarIndex(this.wrappeeMethodNode);
        this.needsSaving = true;
    }
}
