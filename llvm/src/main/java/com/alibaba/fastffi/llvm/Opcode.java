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

/**
 * Note that Opcode is spilitted in multiple enums.
 * We use jint instead
 */
@FFITypeAlias("jint")
@FFITypeRefiner("com.alibaba.fastffi.llvm.Opcode.getOpcode")
public enum Opcode implements CXXEnum {
    Ret(Library.INSTANCE.Ret()),
    Br(Library.INSTANCE.Br()),
    Switch(Library.INSTANCE.Switch()),
    IndirectBr(Library.INSTANCE.IndirectBr()),
    Invoke(Library.INSTANCE.Invoke()),
    Resume(Library.INSTANCE.Resume()),
    Unreachable(Library.INSTANCE.Unreachable()),
    CleanupRet(Library.INSTANCE.CleanupRet()),
    CatchRet(Library.INSTANCE.CatchRet()),
    CatchSwitch(Library.INSTANCE.CatchSwitch()),
    CallBr(Library.INSTANCE.CallBr()),
    FNeg(Library.INSTANCE.FNeg()),
    Add(Library.INSTANCE.Add()),
    FAdd(Library.INSTANCE.FAdd()),
    Sub(Library.INSTANCE.Sub()),
    FSub(Library.INSTANCE.FSub()),
    Mul(Library.INSTANCE.Mul()),
    FMul(Library.INSTANCE.FMul()),
    UDiv(Library.INSTANCE.UDiv()),
    SDiv(Library.INSTANCE.SDiv()),
    FDiv(Library.INSTANCE.FDiv()),
    URem(Library.INSTANCE.URem()),
    SRem(Library.INSTANCE.SRem()),
    FRem(Library.INSTANCE.FRem()),
    Shl(Library.INSTANCE.Shl()),
    LShr(Library.INSTANCE.LShr()),
    AShr(Library.INSTANCE.AShr()),
    And(Library.INSTANCE.And()),
    Or(Library.INSTANCE.Or()),
    Xor(Library.INSTANCE.Xor()),
    Alloca(Library.INSTANCE.Alloca()),
    Load(Library.INSTANCE.Load()),
    Store(Library.INSTANCE.Store()),
    GetElementPtr(Library.INSTANCE.GetElementPtr()),
    Fence(Library.INSTANCE.Fence()),
    AtomicCmpXchg(Library.INSTANCE.AtomicCmpXchg()),
    AtomicRMW(Library.INSTANCE.AtomicRMW()),
    Trunc(Library.INSTANCE.Trunc()),
    ZExt(Library.INSTANCE.ZExt()),
    SExt(Library.INSTANCE.SExt()),
    FPToUI(Library.INSTANCE.FPToUI()),
    FPToSI(Library.INSTANCE.FPToSI()),
    UIToFP(Library.INSTANCE.UIToFP()),
    SIToFP(Library.INSTANCE.SIToFP()),
    FPTrunc(Library.INSTANCE.FPTrunc()),
    FPExt(Library.INSTANCE.FPExt()),
    PtrToInt(Library.INSTANCE.PtrToInt()),
    IntToPtr(Library.INSTANCE.IntToPtr()),
    BitCast(Library.INSTANCE.BitCast()),
    AddrSpaceCast(Library.INSTANCE.AddrSpaceCast()),
    CleanupPad(Library.INSTANCE.CleanupPad()),
    CatchPad(Library.INSTANCE.CatchPad()),
    ICmp(Library.INSTANCE.ICmp()),
    FCmp(Library.INSTANCE.FCmp()),
    PHI(Library.INSTANCE.PHI()),
    Call(Library.INSTANCE.Call()),
    Select(Library.INSTANCE.Select()),
    UserOp1(Library.INSTANCE.UserOp1()),
    UserOp2(Library.INSTANCE.UserOp2()),
    VAArg(Library.INSTANCE.VAArg()),
    ExtractElement(Library.INSTANCE.ExtractElement()),
    InsertElement(Library.INSTANCE.InsertElement()),
    ShuffleVector(Library.INSTANCE.ShuffleVector()),
    ExtractValue(Library.INSTANCE.ExtractValue()),
    InsertValue(Library.INSTANCE.InsertValue()),
    LandingPad(Library.INSTANCE.LandingPad()),
    Freeze(Library.INSTANCE.Freeze());

    @FFIGen
    @CXXHead("llvm/IR/Instruction.h")
    @FFILibrary(value = "llvm::Instruction::Opcode", namespace = "llvm::Instruction")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter
        int Ret();
        @FFIGetter
        int Br();
        @FFIGetter
        int Switch();
        @FFIGetter
        int IndirectBr();
        @FFIGetter
        int Invoke();
        @FFIGetter
        int Resume();
        @FFIGetter
        int Unreachable();
        @FFIGetter
        int CleanupRet();
        @FFIGetter
        int CatchRet();
        @FFIGetter
        int CatchSwitch();
        @FFIGetter
        int CallBr();
        @FFIGetter
        int FNeg();
        @FFIGetter
        int Add();
        @FFIGetter
        int FAdd();
        @FFIGetter
        int Sub();
        @FFIGetter
        int FSub();
        @FFIGetter
        int Mul();
        @FFIGetter
        int FMul();
        @FFIGetter
        int UDiv();
        @FFIGetter
        int SDiv();
        @FFIGetter
        int FDiv();
        @FFIGetter
        int URem();
        @FFIGetter
        int SRem();
        @FFIGetter
        int FRem();
        @FFIGetter
        int Shl();
        @FFIGetter
        int LShr();
        @FFIGetter
        int AShr();
        @FFIGetter
        int And();
        @FFIGetter
        int Or();
        @FFIGetter
        int Xor();
        @FFIGetter
        int Alloca();
        @FFIGetter
        int Load();
        @FFIGetter
        int Store();
        @FFIGetter
        int GetElementPtr();
        @FFIGetter
        int Fence();
        @FFIGetter
        int AtomicCmpXchg();
        @FFIGetter
        int AtomicRMW();
        @FFIGetter
        int Trunc();
        @FFIGetter
        int ZExt();
        @FFIGetter
        int SExt();
        @FFIGetter
        int FPToUI();
        @FFIGetter
        int FPToSI();
        @FFIGetter
        int UIToFP();
        @FFIGetter
        int SIToFP();
        @FFIGetter
        int FPTrunc();
        @FFIGetter
        int FPExt();
        @FFIGetter
        int PtrToInt();
        @FFIGetter
        int IntToPtr();
        @FFIGetter
        int BitCast();
        @FFIGetter
        int AddrSpaceCast();
        @FFIGetter
        int CleanupPad();
        @FFIGetter
        int CatchPad();
        @FFIGetter
        int ICmp();
        @FFIGetter
        int FCmp();
        @FFIGetter
        int PHI();
        @FFIGetter
        int Call();
        @FFIGetter
        int Select();
        @FFIGetter
        int UserOp1();
        @FFIGetter
        int UserOp2();
        @FFIGetter
        int VAArg();
        @FFIGetter
        int ExtractElement();
        @FFIGetter
        int InsertElement();
        @FFIGetter
        int ShuffleVector();
        @FFIGetter
        int ExtractValue();
        @FFIGetter
        int InsertValue();
        @FFIGetter
        int LandingPad();
        @FFIGetter
        int Freeze();
    }

    int opcode;
    Opcode(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getValue() {
        return getOpcode();
    }

    public boolean isTermOps() {
        return this.opcode >= Ret.opcode && this.opcode <= CallBr.opcode;
    }

    public boolean isUnaryOps() {
        return this.opcode == FNeg.opcode;
    }

    public static CXXEnumMap<Opcode> map = new CXXEnumMap<>(values());

    public static Opcode getOpcode(int opcode) {
        return map.get(opcode);
    }
}
