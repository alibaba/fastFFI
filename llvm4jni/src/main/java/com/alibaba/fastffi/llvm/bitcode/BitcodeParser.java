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
import com.alibaba.fastffi.CXXValueScope;
import com.alibaba.fastffi.llvm.Binary;
import com.alibaba.fastffi.llvm.BitcodeReader;
import com.alibaba.fastffi.llvm.ErrorOr;
import com.alibaba.fastffi.llvm.Expected;
import com.alibaba.fastffi.llvm.LLVMContext;
import com.alibaba.fastffi.llvm.MemoryBuffer;
import com.alibaba.fastffi.llvm.Module;
import com.alibaba.fastffi.llvm.ObjectCasting;
import com.alibaba.fastffi.llvm.ObjectFile;
import com.alibaba.fastffi.llvm.OwningBinary;
import com.alibaba.fastffi.llvm.StdString;
import com.alibaba.fastffi.llvm.StringRef;
import com.alibaba.fastffi.llvm.UniquePtr;

public class BitcodeParser {

    /**
     * Use -flto to compile each source file
     * Use -fuse-ld=${LLVM11-HOME}/bin/<lld> -Xlinker -mllvm=-lto-embed-bitcode
     * lld is platform specific. For example, use ld.lld on Linux;
     * See https://reviews.llvm.org/D68213
     * @param context
     * @param file
     * @return
     */
    public static Module parseEmbeddedBitcode(LLVMContext context, String file) {
        try (CXXValueScope scope = new CXXValueScope()) {
            Expected<OwningBinary<Binary>> owningBinaryExpected = Binary.createBinary(file);
            if (owningBinaryExpected.bool() == false) {
                return null;
            }
            Binary binary = owningBinaryExpected.get().getBinary();
            ObjectFile objectFile = ObjectCasting.dyn_cast(binary);
            if (objectFile.isNull()) {
                return null;
            }

            try (CXXStackObject<StdString> stdString = new CXXStackObject<>(StdString.create(objectFile.isMachO() ? "__LLVM,__bitcode" : ".llvmbc"))) {
                for (ObjectFile.SectionIterator iterator : objectFile.sections()) {
                    ObjectFile.SectionRef sectionRef = iterator.get();
                    Expected<StringRef> nameExpected = sectionRef.getName();
                    if (!nameExpected.bool()) {
                        continue;
                    }
                    StringRef name = nameExpected.get();
                    if (!name.equals(stdString.get())) {
                        continue;
                    }
                    Expected<StringRef> ContextsOrErr = sectionRef.getContents();
                    if (!ContextsOrErr.bool()) {
                        return null;
                    }

                    UniquePtr<MemoryBuffer> MB = MemoryBuffer.getMemBuffer(ContextsOrErr.get());
                    Expected<UniquePtr<Module>> ModuleOrErr = BitcodeReader.INSTANCE.parseBitcodeFile(MB.get().getMemBufferRef(), context);
                    if (!ModuleOrErr.bool()) {
                        return null;
                    }
                    return ModuleOrErr.get().release();
                }
            }

            return null;
        }
    }

    public static Module parseBitcodeFile(LLVMContext context, String file) {
        try (CXXValueScope scope = new CXXValueScope()) {
            ErrorOr<UniquePtr<MemoryBuffer>> MB = MemoryBuffer.getFile(file);
            if (!MB.bool()) {
                return null;
            }

            Expected<UniquePtr<Module>> ModuleOrErr = BitcodeReader.INSTANCE.parseBitcodeFile(MB.get().get().getMemBufferRef(), context);
            if (!ModuleOrErr.bool()) {
                return null;
            }
            return ModuleOrErr.get().release();
        }
    }
}
