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
package com.alibaba.fastffi.llvm.bitcode;

import com.alibaba.fastffi.CXXStackObject;
import com.alibaba.fastffi.llvm.LLVMContext;
import com.alibaba.fastffi.llvm.Module;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;


import static com.alibaba.fastffi.llvm.TestUtils.getResourceAsFile;


public class BitcodeParserTest {

    static boolean verbose = false;

    static void iterateModule(Module module) {
        module.forEachFunction( function -> {
            if (verbose) System.out.println(function.getName());
            function.forEachBasicBlock(basicBlock -> {
                basicBlock.forEachInstruction(instruction -> {
                    if (verbose) {
                        System.out.println(instruction.getClass());
                        System.out.println(instruction.getOpcode());
                        instruction.getValueID();
                        System.out.println(instruction.getType().getTypeID());
                    }
                });
            });
        });
        module.forEachGlobal( global -> {
            if (verbose) System.out.println(global.getName());
        });
    }

    @Test
    public void testParseEmbeddedBitcodeFile() throws IOException {
        try (CXXStackObject<LLVMContext> contextObject = new CXXStackObject<LLVMContext>(LLVMContext.newContext())) {
            Module module = BitcodeParser.parseEmbeddedBitcode(contextObject.get(),
                    getResourceAsFile("libgraph.so").getAbsolutePath());
            Assert.assertTrue(module != null && !module.isNull());
            iterateModule(module);
        }
    }

    @Test
    public void testParseBitcodeFile() throws IOException {
        try (CXXStackObject<LLVMContext> contextObject = new CXXStackObject<LLVMContext>(LLVMContext.newContext())) {
            Module module = BitcodeParser.parseBitcodeFile(contextObject.get(),
                    getResourceAsFile("libsimdjson.bc").getAbsolutePath());
            Assert.assertTrue(module != null && !module.isNull());
            iterateModule(module);
        }
    }
}
