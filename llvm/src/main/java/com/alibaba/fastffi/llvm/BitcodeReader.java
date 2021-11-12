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
package com.alibaba.fastffi.llvm;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.stdcxx.UniquePtr;

@FFIGen
@FFILibrary(value = "llvm::BitcodeReader", namespace = "llvm")
@CXXHead("llvm/Bitcode/BitcodeReader.h")
public interface BitcodeReader {

    BitcodeReader INSTANCE = FFITypeFactory.getLibrary(BitcodeReader.class);

    @CXXValue
    Expected<UniquePtr<Module>> parseBitcodeFile(@CXXValue MemoryBufferRef Buffer,
                                                 @CXXReference LLVMContext Context);

}
