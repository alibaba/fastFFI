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
import com.alibaba.fastffi.llvm.BasicBlock;
import com.alibaba.fastffi.llvm.BranchInst;
import com.alibaba.fastffi.llvm.CallBase;
import com.alibaba.fastffi.llvm.Function;
import com.alibaba.fastffi.llvm.Instruction;
import com.alibaba.fastffi.llvm.IntrinsicInst;
import com.alibaba.fastffi.llvm.ReturnInst;
import com.alibaba.fastffi.llvm.UnreachableInst;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class SimpleIndirectCallPolicy implements IndirectCallPolicy {

    static Set<String> whitelist = new HashSet<>();
    static {
        // https://libcxxabi.llvm.org/spec.html
        // http://itanium-cxx-abi.github.io/cxx-abi/abi.html
        // Guard variables
        whitelist.add("__cxa_guard_acquire");
        whitelist.add("__cxa_guard_release");
        whitelist.add("__cxa_guard_abort");
        whitelist.add("__cxa_atexit");
    }

    enum Result {
        Enable,
        Disable;
    }

    Map<Function, Result> analyzed = new HashMap<>();

    @Override
    public boolean supportIndirectCall(Universe universe, CallBase inst) {
        BasicBlock basicBlock = inst.getParent();
        Instruction terminator = basicBlock.getTerminator();
        if (terminator instanceof UnreachableInst) {
            return true;
        }
        {
            Function function = inst.getCalledFunction();
            if (function != null) {
                String name = function.getName();
                if (whitelist.contains(name)) {
                    return true;
                }
            }
        }
        {
            Function function = basicBlock.getParent();
            Result result = analyzed.get(function);
            if (result != null) {
                return result == Result.Enable;
            }
            if (analyze(universe, function)) {
                analyzed.put(function, Result.Enable);
                return true;
            } else {
                analyzed.put(function, Result.Disable);
                return false;
            }
        }
    }

    /**
     * BFS from entry point; Add to queue when the basic block does not have indirect call;
     * Return true if there is a normal return path that is composed of all such basic blocks.
     * @param function
     * @return
     */
    boolean analyze(Universe universe, Function function) {
        BasicBlock entryBlock = function.getEntryBlock();
        Set<BasicBlock> visited = new HashSet<>();
        LinkedList<BasicBlock> queue = new LinkedList<>();
        queue.addLast(entryBlock);
        visited.add(entryBlock);
        while (!queue.isEmpty()) {
            BasicBlock current = queue.removeFirst();
            if (hasIndirectCall(universe, current)) {
                continue;
            }
            Instruction terminator = current.getTerminator();
            if (terminator instanceof ReturnInst) {
                return true;
            }
            if (terminator instanceof UnreachableInst) {
                // we are in the end but this is not a return
                continue;
            }
            if (!(terminator instanceof BranchInst)) {
                // all other terminators are not supported;
                return false;
            }
            BranchInst branchInst = (BranchInst) terminator;
            int numOfSuccessors = branchInst.getNumSuccessors();
            for (int i = 0; i < numOfSuccessors; i++) {
                BasicBlock successor = branchInst.getSuccessor(i);
                if (!visited.contains(successor)) {
                    visited.add(successor);
                    queue.addLast(successor);
                }
            }
        }
        // multiple exit
        return false;
    }

    private boolean hasIndirectCall(Universe universe, BasicBlock current) {
        try (CXXValueScope scope = new CXXValueScope()) {
            BasicBlock.InstList instList = current.getInstList();
            for (BasicBlock.InstList.Iter it : instList) {
                Instruction inst = it.get();
                if (isCallIndirect(universe, inst)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCallIndirect(Universe universe, Instruction inst) {
        if (inst instanceof CallBase) {
            return isCallIndirect(universe, (CallBase) inst);
        }
        return false;
    }

    private boolean isCallIndirect(Universe universe, CallBase inst) {
        if (inst instanceof IntrinsicInst) {
            return false;
        }
        Function callee = inst.getCalledFunction();
        if (callee == null) {
            // not a function
            return true;
        }
        if (universe.supportDirectCall(callee)) {
            // an external function
            return false;
        }
        // assume that we must call this function directly.
        return true;
    }
}
