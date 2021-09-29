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
package com.alibaba.fastffi.llvm4jni.runtime;

/**
 * A simplified version of LLVMStack that can be used separatedly.
 */
public class Stack {

    public static Stack getStack() {
        return stackThreadLocal.get();
    }

    private static final ThreadLocal<Stack> stackThreadLocal =
            new ThreadLocal<Stack>() {
                @Override protected Stack initialValue() {
                    return new Stack(81920 * 1024);
                }
            };

    private final long stackSize;

    private long lowerBounds;
    private long upperBounds;
    private boolean isAllocated;

    private long stackPointer;

    public Stack(long stackSize) {
        this.stackSize = stackSize;
    }

    public final long pushFrame(long frameSize) {
        if (!isAllocated) {
            allocate();
        }
        long basePointer = stackPointer;
        if (stackPointer - frameSize < lowerBounds) {
            throw new StackOverflowError(String.format("sp=%d, stack=[%d,%d], new frame size=%d", stackPointer, lowerBounds, upperBounds, frameSize));
        }
        stackPointer -= frameSize;
        return basePointer;
    }

    public final void popFrame(long basePointer) {
        this.stackPointer = basePointer;
        return;
    }

    private void allocate() {
        long size = stackSize;
        long stackAllocation = JavaRuntime.allocate(size);
        lowerBounds = stackAllocation;
        upperBounds = stackAllocation + size;
        isAllocated = true;
        stackPointer = upperBounds;  // allocate from top to bottom
    }
}
