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

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;

@FFITypeAlias("llvm::CallingConv::ID")
@FFITypeRefiner("com.alibaba.fastffi.llvm.CallingConvID.getCallingConv")
public enum CallingConvID implements CXXEnum {

    C(Library.INSTANCE.C()),
    Fast(Library.INSTANCE.Fast()),
    Cold(Library.INSTANCE.Cold()),
    GHC(Library.INSTANCE.GHC()),
    HiPE(Library.INSTANCE.HiPE()),
    WebKit_JS(Library.INSTANCE.WebKit_JS()),
    AnyReg(Library.INSTANCE.AnyReg()),
    PreserveMost(Library.INSTANCE.PreserveMost()),
    PreserveAll(Library.INSTANCE.PreserveAll()),
    Swift(Library.INSTANCE.Swift()),
    CXX_FAST_TLS(Library.INSTANCE.CXX_FAST_TLS()),
    Tail(Library.INSTANCE.Tail()),
    CFGuard_Check(Library.INSTANCE.CFGuard_Check()),
    FirstTargetCC(Library.INSTANCE.FirstTargetCC()),
    X86_StdCall(Library.INSTANCE.X86_StdCall()),
    X86_FastCall(Library.INSTANCE.X86_FastCall()),
    ARM_APCS(Library.INSTANCE.ARM_APCS()),
    ARM_AAPCS(Library.INSTANCE.ARM_AAPCS()),
    ARM_AAPCS_VFP(Library.INSTANCE.ARM_AAPCS_VFP()),
    MSP430_INTR(Library.INSTANCE.MSP430_INTR()),
    X86_ThisCall(Library.INSTANCE.X86_ThisCall()),
    PTX_Kernel(Library.INSTANCE.PTX_Kernel()),
    PTX_Device(Library.INSTANCE.PTX_Device()),
    SPIR_FUNC(Library.INSTANCE.SPIR_FUNC()),
    SPIR_KERNEL(Library.INSTANCE.SPIR_KERNEL()),
    Intel_OCL_BI(Library.INSTANCE.Intel_OCL_BI()),
    X86_64_SysV(Library.INSTANCE.X86_64_SysV()),
    Win64(Library.INSTANCE.Win64()),
    X86_VectorCall(Library.INSTANCE.X86_VectorCall()),
    HHVM(Library.INSTANCE.HHVM()),
    HHVM_C(Library.INSTANCE.HHVM_C()),
    X86_INTR(Library.INSTANCE.X86_INTR()),
    AVR_INTR(Library.INSTANCE.AVR_INTR()),
    AVR_SIGNAL(Library.INSTANCE.AVR_SIGNAL()),
    AVR_BUILTIN(Library.INSTANCE.AVR_BUILTIN()),
    AMDGPU_VS(Library.INSTANCE.AMDGPU_VS()),
    AMDGPU_GS(Library.INSTANCE.AMDGPU_GS()),
    AMDGPU_PS(Library.INSTANCE.AMDGPU_PS()),
    AMDGPU_CS(Library.INSTANCE.AMDGPU_CS()),
    AMDGPU_KERNEL(Library.INSTANCE.AMDGPU_KERNEL()),
    X86_RegCall(Library.INSTANCE.X86_RegCall()),
    AMDGPU_HS(Library.INSTANCE.AMDGPU_HS()),
    MSP430_BUILTIN(Library.INSTANCE.MSP430_BUILTIN()),
    AMDGPU_LS(Library.INSTANCE.AMDGPU_LS()),
    AMDGPU_ES(Library.INSTANCE.AMDGPU_ES()),
    AArch64_VectorCall(Library.INSTANCE.AArch64_VectorCall()),
    AArch64_SVE_VectorCall(Library.INSTANCE.AArch64_SVE_VectorCall()),
    WASM_EmscriptenInvoke(Library.INSTANCE.WASM_EmscriptenInvoke()),
    MaxID(Library.INSTANCE.MaxID());

    @FFIGen
    @CXXHead("llvm/IR/CallingConv.h")
    @FFILibrary(value = "llvm::CallingConv::ID", namespace = "llvm::CallingConv")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter int C();
        @FFIGetter int Fast();
        @FFIGetter int Cold();
        @FFIGetter int GHC();
        @FFIGetter int HiPE();
        @FFIGetter int WebKit_JS();
        @FFIGetter int AnyReg();
        @FFIGetter int PreserveMost();
        @FFIGetter int PreserveAll();
        @FFIGetter int Swift();
        @FFIGetter int CXX_FAST_TLS();
        @FFIGetter int Tail();
        @FFIGetter int CFGuard_Check();
        @FFIGetter int FirstTargetCC();
        @FFIGetter int X86_StdCall();
        @FFIGetter int X86_FastCall();
        @FFIGetter int ARM_APCS();
        @FFIGetter int ARM_AAPCS();
        @FFIGetter int ARM_AAPCS_VFP();
        @FFIGetter int MSP430_INTR();
        @FFIGetter int X86_ThisCall();
        @FFIGetter int PTX_Kernel();
        @FFIGetter int PTX_Device();
        @FFIGetter int SPIR_FUNC();
        @FFIGetter int SPIR_KERNEL();
        @FFIGetter int Intel_OCL_BI();
        @FFIGetter int X86_64_SysV();
        @FFIGetter int Win64();
        @FFIGetter int X86_VectorCall();
        @FFIGetter int HHVM();
        @FFIGetter int HHVM_C();
        @FFIGetter int X86_INTR();
        @FFIGetter int AVR_INTR();
        @FFIGetter int AVR_SIGNAL();
        @FFIGetter int AVR_BUILTIN();
        @FFIGetter int AMDGPU_VS();
        @FFIGetter int AMDGPU_GS();
        @FFIGetter int AMDGPU_PS();
        @FFIGetter int AMDGPU_CS();
        @FFIGetter int AMDGPU_KERNEL();
        @FFIGetter int X86_RegCall();
        @FFIGetter int AMDGPU_HS();
        @FFIGetter int MSP430_BUILTIN();
        @FFIGetter int AMDGPU_LS();
        @FFIGetter int AMDGPU_ES();
        @FFIGetter int AArch64_VectorCall();
        @FFIGetter int AArch64_SVE_VectorCall();
        @FFIGetter int WASM_EmscriptenInvoke();
        @FFIGetter int MaxID();
    }

    int value;
    CallingConvID(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isDefaultC() {
        return this == C;
    }

    public static CXXEnumMap<CallingConvID> map = new CXXEnumMap<>(values());

    public static CallingConvID getCallingConv(int value) {
        return map.get(value);
    }
}
