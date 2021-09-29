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
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFISkip;
import com.alibaba.fastffi.FFITypeFactory;

/**
 * Casting from Type to a proper subtype of Type
 */
@FFIGen
@CXXHead("llvm/IR/Value.h")
@CXXHead("llvm/IR/Constant.h")
@CXXHead("llvm/IR/Constants.h")
@CXXHead("llvm/IR/BasicBlock.h")
@CXXHead("llvm/IR/Function.h")
@CXXHead("llvm/IR/GlobalVariable.h")
@CXXHead("llvm/IR/GlobalIFunc.h")
@CXXHead("llvm/IR/GlobalIndirectSymbol.h")
@CXXHead("llvm/IR/GlobalAlias.h")
@CXXHead("llvm/IR/Instruction.h")
@CXXHead("llvm/IR/IntrinsicInst.h")
@CXXHead("llvm/IR/InstrTypes.h")
@CXXHead("llvm/IR/Instructions.h")
@FFILibrary(value = "llvm::Value::Casting", namespace = "llvm")
public interface ValueCasting {

    ValueCastingGen INSTANCE = (ValueCastingGen) FFITypeFactory.getLibrary(ValueCasting.class);

    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BasicBlock"},
            java = {"Value", "BasicBlock"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::User"},
            java = {"Value", "User"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Argument"},
            java = {"Value", "Argument"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::MetadataAsValue"},
            java = {"Value", "MetadataAsValue"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Constant"},
            java = {"Value", "Constant"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantAggregate"},
            java = {"Value", "ConstantAggregate"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantVector"},
            java = {"Value", "ConstantVector"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantStruct"},
            java = {"Value", "ConstantStruct"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantArray"},
            java = {"Value", "ConstantArray"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalValue"},
            java = {"Value", "GlobalValue"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalObject"},
            java = {"Value", "GlobalObject"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalVariable"},
            java = {"Value", "GlobalVariable"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Function"},
            java = {"Value", "Function"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalIndirectSymbol"},
            java = {"Value", "GlobalIndirectSymbol"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalAlias"},
            java = {"Value", "GlobalAlias"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantData"},
            java = {"Value", "ConstantData"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UndefValue"},
            java = {"Value", "UndefValue"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantInt"},
            java = {"Value", "ConstantInt"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantAggregateZero"},
            java = {"Value", "ConstantAggregateZero"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantTokenNone"},
            java = {"Value", "ConstantTokenNone"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantPointerNull"},
            java = {"Value", "ConstantPointerNull"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantFP"},
            java = {"Value", "ConstantFP"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantDataSequential"},
            java = {"Value", "ConstantDataSequential"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Instruction"},
            java = {"Value", "Instruction"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CallBase"},
            java = {"Value", "CallBase"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CallInst"},
            java = {"Value", "CallInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::InvokeInst"},
            java = {"Value", "InvokeInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CallBrInst"},
            java = {"Value", "CallBrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::IntrinsicInst"},
            java = {"Value", "IntrinsicInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::IndirectBrInst"},
            java = {"Value", "IndirectBrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ReturnInst"},
            java = {"Value", "ReturnInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GetElementPtrInst"},
            java = {"Value", "GetElementPtrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AtomicCmpXchgInst"},
            java = {"Value", "AtomicCmpXchgInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ExtractElementInst"},
            java = {"Value", "ExtractElementInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::InsertValueInst"},
            java = {"Value", "InsertValueInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UnreachableInst"},
            java = {"Value", "UnreachableInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FuncletPadInst"},
            java = {"Value", "FuncletPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CatchPadInst"},
            java = {"Value", "CatchPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CleanupPadInst"},
            java = {"Value", "CleanupPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AtomicRMWInst"},
            java = {"Value", "AtomicRMWInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SelectInst"},
            java = {"Value", "SelectInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::StoreInst"},
            java = {"Value", "StoreInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FenceInst"},
            java = {"Value", "FenceInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::InsertElementInst"},
            java = {"Value", "InsertElementInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UnaryInstruction"},
            java = {"Value", "UnaryInstruction"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AllocaInst"},
            java = {"Value", "AllocaInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CastInst"},
            java = {"Value", "CastInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ExtractValueInst"},
            java = {"Value", "ExtractValueInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FreezeInst"},
            java = {"Value", "FreezeInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::LoadInst"},
            java = {"Value", "LoadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UnaryOperator"},
            java = {"Value", "UnaryOperator"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::VAArgInst"},
            java = {"Value", "VAArgInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AddrSpaceCastInst"},
            java = {"Value", "AddrSpaceCastInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BitCastInst"},
            java = {"Value", "BitCastInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPToSIInst"},
            java = {"Value", "FPToSIInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPToUIInst"},
            java = {"Value", "FPToUIInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPTruncInst"},
            java = {"Value", "FPTruncInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::IntToPtrInst"},
            java = {"Value", "IntToPtrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::PtrToIntInst"},
            java = {"Value", "PtrToIntInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SExtInst"},
            java = {"Value", "SExtInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SIToFPInst"},
            java = {"Value", "SIToFPInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::TruncInst"},
            java = {"Value", "TruncInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UIToFPInst"},
            java = {"Value", "UIToFPInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ZExtInst"},
            java = {"Value", "ZExtInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CatchReturnInst"},
            java = {"Value", "CatchReturnInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CleanupReturnInst"},
            java = {"Value", "CleanupReturnInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CmpInst"},
            java = {"Value", "CmpInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ICmpInst"},
            java = {"Value", "ICmpInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FCmpInst"},
            java = {"Value", "FCmpInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ResumeInst"},
            java = {"Value", "ResumeInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SwitchInst"},
            java = {"Value", "SwitchInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantExpr"},
            java = {"Value", "ConstantExpr"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalIFunc"},
            java = {"Value", "GlobalIFunc"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BinaryOperator"},
            java = {"Value", "BinaryOperator"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BranchInst"},
            java = {"Value", "BranchInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::LandingPadInst"},
            java = {"Value", "LandingPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::PHINode"},
            java = {"Value", "PHINode"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPExtInst"},
            java = {"Value", "FPExtInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BlockAddress"},
            java = {"Value", "BlockAddress"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantDataArray"},
            java = {"Value", "ConstantDataArray"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantDataVector"},
            java = {"Value", "ConstantDataVector"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CatchSwitchInst"},
            java = {"Value", "CatchSwitchInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ShuffleVectorInst"},
            java = {"Value", "ShuffleVectorInst"}
    )
    <@FFISkip From, To> To cast(From from, @FFISkip To unused);

    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BasicBlock"},
            java = {"Value", "BasicBlock"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::User"},
            java = {"Value", "User"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Argument"},
            java = {"Value", "Argument"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::MetadataAsValue"},
            java = {"Value", "MetadataAsValue"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Constant"},
            java = {"Value", "Constant"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantAggregate"},
            java = {"Value", "ConstantAggregate"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantVector"},
            java = {"Value", "ConstantVector"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantStruct"},
            java = {"Value", "ConstantStruct"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantArray"},
            java = {"Value", "ConstantArray"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalValue"},
            java = {"Value", "GlobalValue"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalObject"},
            java = {"Value", "GlobalObject"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalVariable"},
            java = {"Value", "GlobalVariable"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Function"},
            java = {"Value", "Function"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalIndirectSymbol"},
            java = {"Value", "GlobalIndirectSymbol"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalAlias"},
            java = {"Value", "GlobalAlias"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantData"},
            java = {"Value", "ConstantData"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UndefValue"},
            java = {"Value", "UndefValue"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantInt"},
            java = {"Value", "ConstantInt"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantAggregateZero"},
            java = {"Value", "ConstantAggregateZero"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantTokenNone"},
            java = {"Value", "ConstantTokenNone"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantPointerNull"},
            java = {"Value", "ConstantPointerNull"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantFP"},
            java = {"Value", "ConstantFP"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantDataSequential"},
            java = {"Value", "ConstantDataSequential"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Instruction"},
            java = {"Value", "Instruction"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CallBase"},
            java = {"Value", "CallBase"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CallInst"},
            java = {"Value", "CallInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::InvokeInst"},
            java = {"Value", "InvokeInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CallBrInst"},
            java = {"Value", "CallBrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::IntrinsicInst"},
            java = {"Value", "IntrinsicInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::IndirectBrInst"},
            java = {"Value", "IndirectBrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ReturnInst"},
            java = {"Value", "ReturnInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GetElementPtrInst"},
            java = {"Value", "GetElementPtrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AtomicCmpXchgInst"},
            java = {"Value", "AtomicCmpXchgInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ExtractElementInst"},
            java = {"Value", "ExtractElementInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::InsertValueInst"},
            java = {"Value", "InsertValueInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UnreachableInst"},
            java = {"Value", "UnreachableInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FuncletPadInst"},
            java = {"Value", "FuncletPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CatchPadInst"},
            java = {"Value", "CatchPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CleanupPadInst"},
            java = {"Value", "CleanupPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AtomicRMWInst"},
            java = {"Value", "AtomicRMWInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SelectInst"},
            java = {"Value", "SelectInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::StoreInst"},
            java = {"Value", "StoreInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FenceInst"},
            java = {"Value", "FenceInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::InsertElementInst"},
            java = {"Value", "InsertElementInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UnaryInstruction"},
            java = {"Value", "UnaryInstruction"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AllocaInst"},
            java = {"Value", "AllocaInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CastInst"},
            java = {"Value", "CastInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ExtractValueInst"},
            java = {"Value", "ExtractValueInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FreezeInst"},
            java = {"Value", "FreezeInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::LoadInst"},
            java = {"Value", "LoadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UnaryOperator"},
            java = {"Value", "UnaryOperator"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::VAArgInst"},
            java = {"Value", "VAArgInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AddrSpaceCastInst"},
            java = {"Value", "AddrSpaceCastInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BitCastInst"},
            java = {"Value", "BitCastInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPToSIInst"},
            java = {"Value", "FPToSIInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPToUIInst"},
            java = {"Value", "FPToUIInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPTruncInst"},
            java = {"Value", "FPTruncInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::IntToPtrInst"},
            java = {"Value", "IntToPtrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::PtrToIntInst"},
            java = {"Value", "PtrToIntInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SExtInst"},
            java = {"Value", "SExtInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SIToFPInst"},
            java = {"Value", "SIToFPInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::TruncInst"},
            java = {"Value", "TruncInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UIToFPInst"},
            java = {"Value", "UIToFPInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ZExtInst"},
            java = {"Value", "ZExtInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CatchReturnInst"},
            java = {"Value", "CatchReturnInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CleanupReturnInst"},
            java = {"Value", "CleanupReturnInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CmpInst"},
            java = {"Value", "CmpInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ICmpInst"},
            java = {"Value", "ICmpInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FCmpInst"},
            java = {"Value", "FCmpInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ResumeInst"},
            java = {"Value", "ResumeInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SwitchInst"},
            java = {"Value", "SwitchInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantExpr"},
            java = {"Value", "ConstantExpr"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalIFunc"},
            java = {"Value", "GlobalIFunc"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BinaryOperator"},
            java = {"Value", "BinaryOperator"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BranchInst"},
            java = {"Value", "BranchInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::LandingPadInst"},
            java = {"Value", "LandingPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::PHINode"},
            java = {"Value", "PHINode"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPExtInst"},
            java = {"Value", "FPExtInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BlockAddress"},
            java = {"Value", "BlockAddress"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantDataArray"},
            java = {"Value", "ConstantDataArray"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantDataVector"},
            java = {"Value", "ConstantDataVector"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CatchSwitchInst"},
            java = {"Value", "CatchSwitchInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ShuffleVectorInst"},
            java = {"Value", "ShuffleVectorInst"}
    )
    <@FFISkip From, To> To dyn_cast(From from, @FFISkip To unused);

    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BasicBlock"},
            java = {"Value", "BasicBlock"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::User"},
            java = {"Value", "User"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Argument"},
            java = {"Value", "Argument"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::MetadataAsValue"},
            java = {"Value", "MetadataAsValue"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Constant"},
            java = {"Value", "Constant"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantAggregate"},
            java = {"Value", "ConstantAggregate"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantVector"},
            java = {"Value", "ConstantVector"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantStruct"},
            java = {"Value", "ConstantStruct"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantArray"},
            java = {"Value", "ConstantArray"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalValue"},
            java = {"Value", "GlobalValue"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalObject"},
            java = {"Value", "GlobalObject"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalVariable"},
            java = {"Value", "GlobalVariable"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Function"},
            java = {"Value", "Function"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalIndirectSymbol"},
            java = {"Value", "GlobalIndirectSymbol"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalAlias"},
            java = {"Value", "GlobalAlias"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantData"},
            java = {"Value", "ConstantData"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UndefValue"},
            java = {"Value", "UndefValue"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantInt"},
            java = {"Value", "ConstantInt"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantAggregateZero"},
            java = {"Value", "ConstantAggregateZero"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantTokenNone"},
            java = {"Value", "ConstantTokenNone"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantPointerNull"},
            java = {"Value", "ConstantPointerNull"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantFP"},
            java = {"Value", "ConstantFP"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantDataSequential"},
            java = {"Value", "ConstantDataSequential"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::Instruction"},
            java = {"Value", "Instruction"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CallBase"},
            java = {"Value", "CallBase"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CallInst"},
            java = {"Value", "CallInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::InvokeInst"},
            java = {"Value", "InvokeInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CallBrInst"},
            java = {"Value", "CallBrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::IntrinsicInst"},
            java = {"Value", "IntrinsicInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::IndirectBrInst"},
            java = {"Value", "IndirectBrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ReturnInst"},
            java = {"Value", "ReturnInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GetElementPtrInst"},
            java = {"Value", "GetElementPtrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AtomicCmpXchgInst"},
            java = {"Value", "AtomicCmpXchgInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ExtractElementInst"},
            java = {"Value", "ExtractElementInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::InsertValueInst"},
            java = {"Value", "InsertValueInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UnreachableInst"},
            java = {"Value", "UnreachableInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FuncletPadInst"},
            java = {"Value", "FuncletPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CatchPadInst"},
            java = {"Value", "CatchPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CleanupPadInst"},
            java = {"Value", "CleanupPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AtomicRMWInst"},
            java = {"Value", "AtomicRMWInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SelectInst"},
            java = {"Value", "SelectInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::StoreInst"},
            java = {"Value", "StoreInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FenceInst"},
            java = {"Value", "FenceInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::InsertElementInst"},
            java = {"Value", "InsertElementInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UnaryInstruction"},
            java = {"Value", "UnaryInstruction"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AllocaInst"},
            java = {"Value", "AllocaInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CastInst"},
            java = {"Value", "CastInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ExtractValueInst"},
            java = {"Value", "ExtractValueInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FreezeInst"},
            java = {"Value", "FreezeInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::LoadInst"},
            java = {"Value", "LoadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UnaryOperator"},
            java = {"Value", "UnaryOperator"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::VAArgInst"},
            java = {"Value", "VAArgInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::AddrSpaceCastInst"},
            java = {"Value", "AddrSpaceCastInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BitCastInst"},
            java = {"Value", "BitCastInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPToSIInst"},
            java = {"Value", "FPToSIInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPToUIInst"},
            java = {"Value", "FPToUIInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPTruncInst"},
            java = {"Value", "FPTruncInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::IntToPtrInst"},
            java = {"Value", "IntToPtrInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::PtrToIntInst"},
            java = {"Value", "PtrToIntInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SExtInst"},
            java = {"Value", "SExtInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SIToFPInst"},
            java = {"Value", "SIToFPInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::TruncInst"},
            java = {"Value", "TruncInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::UIToFPInst"},
            java = {"Value", "UIToFPInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ZExtInst"},
            java = {"Value", "ZExtInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CatchReturnInst"},
            java = {"Value", "CatchReturnInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CleanupReturnInst"},
            java = {"Value", "CleanupReturnInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CmpInst"},
            java = {"Value", "CmpInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ICmpInst"},
            java = {"Value", "ICmpInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FCmpInst"},
            java = {"Value", "FCmpInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ResumeInst"},
            java = {"Value", "ResumeInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::SwitchInst"},
            java = {"Value", "SwitchInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantExpr"},
            java = {"Value", "ConstantExpr"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::GlobalIFunc"},
            java = {"Value", "GlobalIFunc"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BinaryOperator"},
            java = {"Value", "BinaryOperator"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BranchInst"},
            java = {"Value", "BranchInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::LandingPadInst"},
            java = {"Value", "LandingPadInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::PHINode"},
            java = {"Value", "PHINode"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::FPExtInst"},
            java = {"Value", "FPExtInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::BlockAddress"},
            java = {"Value", "BlockAddress"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantDataArray"},
            java = {"Value", "ConstantDataArray"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ConstantDataVector"},
            java = {"Value", "ConstantDataVector"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::CatchSwitchInst"},
            java = {"Value", "CatchSwitchInst"}
    )
    @CXXTemplate(
            cxx = {"llvm::Value", "llvm::ShuffleVectorInst"},
            java = {"Value", "ShuffleVectorInst"}
    )
    <@FFISkip From, To> boolean isa(From from, @FFISkip To unused);
}
