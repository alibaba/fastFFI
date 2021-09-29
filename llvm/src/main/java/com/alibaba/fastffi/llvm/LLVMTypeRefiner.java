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

public class LLVMTypeRefiner {

    public static Constant refine(Constant c) {
        return (Constant) refine((Value) c);
    }

    public static Value refine(Value v) {
        if (v == null) {
            return null;
        }
        ValueTy valueTy = v.getValueID();
        switch (valueTy) {
            case FunctionVal:
                return Function.dyn_cast(v);
            case GlobalAliasVal:
                return GlobalAlias.dyn_cast(v);
            case GlobalIFuncVal:
                return GlobalIFunc.dyn_cast(v);
            case GlobalVariableVal:
                return GlobalVariable.dyn_cast(v);
            case BlockAddressVal:
                return BlockAddress.dyn_cast(v);
            case ConstantExprVal:
                return ConstantExpr.dyn_cast(v);
            case ConstantArrayVal:
                return ConstantArray.dyn_cast(v);
            case ConstantStructVal:
                return ConstantStruct.dyn_cast(v);
            case ConstantVectorVal:
                return ConstantVector.dyn_cast(v);
            case UndefValueVal:
                return UndefValue.dyn_cast(v);
            case ConstantAggregateZeroVal:
                return ConstantAggregateZero.dyn_cast(v);
            case ConstantDataArrayVal:
                return ConstantDataArray.dyn_cast(v);
            case ConstantDataVectorVal:
                return ConstantDataVector.dyn_cast(v);
            case ConstantIntVal:
                return ConstantInt.dyn_cast(v);
            case ConstantFPVal:
                return ConstantFP.dyn_cast(v);
            case ConstantPointerNullVal:
                return ConstantPointerNull.dyn_cast(v);
            case ConstantTokenNoneVal:
                return ConstantTokenNone.dyn_cast(v);
            case ArgumentVal:
                return Argument.dyn_cast(v);
            case BasicBlockVal:
                return BasicBlock.dyn_cast(v);
            case MetadataAsValueVal:
                return MetadataAsValue.dyn_cast(v);
            case InlineAsmVal:
                return null;
            case MemoryUseVal:
            case MemoryDefVal:
            case MemoryPhiVal:
                throw new IllegalArgumentException("Unsupported valueId " + valueTy);
            case InstructionVal:
                return refine(Instruction.dyn_cast(v));
            default:
                throw new IllegalArgumentException("Invalid valueId " + valueTy);
        }
    }

    public static Instruction refine(Instruction inst) {
        if (inst == null) {
            return null;
        }
        Opcode opcode = inst.getOpcode();
        switch (opcode) {
            case Ret:
                return ReturnInst.dyn_cast(inst);
            case Br:
                return BranchInst.dyn_cast(inst);
            case Switch:
                return SwitchInst.dyn_cast(inst);
            case IndirectBr:
                return IndirectBrInst.dyn_cast(inst);
            case Invoke:
                return InvokeInst.dyn_cast(inst);
            case Resume:
                return ResumeInst.dyn_cast(inst);
            case Unreachable:
                return UnreachableInst.dyn_cast(inst);
            case CleanupRet:
                return CleanupReturnInst.dyn_cast(inst);
            case CatchSwitch:
                return CatchSwitchInst.dyn_cast(inst);
            case CallBr:
                return CallBrInst.dyn_cast(inst);
            case FNeg:
                return UnaryOperator.dyn_cast(inst);
            case Add:
            case FAdd:
            case Sub:
            case FSub:
            case Mul:
            case FMul:
            case UDiv:
            case SDiv:
            case FDiv:
            case URem:
            case SRem:
            case FRem:
            case Shl:
            case LShr:
            case AShr:
            case And:
            case Or:
            case Xor:
                return BinaryOperator.dyn_cast(inst);
            case Alloca:
                return AllocaInst.dyn_cast(inst);
            case Load:
                return LoadInst.dyn_cast(inst);
            case Store:
                return StoreInst.dyn_cast(inst);
            case GetElementPtr:
                return GetElementPtrInst.dyn_cast(inst);
            case Fence:
                return FenceInst.dyn_cast(inst);
            case AtomicCmpXchg:
                return AtomicCmpXchgInst.dyn_cast(inst);
            case AtomicRMW:
                return AtomicRMWInst.dyn_cast(inst);
            case Trunc:
                return TruncInst.dyn_cast(inst);
            case ZExt:
                return ZExtInst.dyn_cast(inst);
            case SExt:
                return SExtInst.dyn_cast(inst);
            case FPToUI:
                return FPToUIInst.dyn_cast(inst);
            case FPToSI:
                return FPToSIInst.dyn_cast(inst);
            case UIToFP:
                return UIToFPInst.dyn_cast(inst);
            case SIToFP:
                return SIToFPInst.dyn_cast(inst);
            case FPTrunc:
                return FPTruncInst.dyn_cast(inst);
            case FPExt:
                return FPExtInst.dyn_cast(inst);
            case PtrToInt:
                return PtrToIntInst.dyn_cast(inst);
            case IntToPtr:
                return IntToPtrInst.dyn_cast(inst);
            case BitCast:
                return BitCastInst.dyn_cast(inst);
            case AddrSpaceCast:
                return AddrSpaceCastInst.dyn_cast(inst);
            case CleanupPad:
                return CleanupPadInst.dyn_cast(inst);
            case CatchPad:
                return CatchPadInst.dyn_cast(inst);
            case ICmp:
                return ICmpInst.dyn_cast(inst);
            case FCmp:
                return FCmpInst.dyn_cast(inst);
            case PHI:
                return PHINode.dyn_cast(inst);
            case Call:
                if (IntrinsicInst.isa(inst)) {
                    return IntrinsicInst.cast(inst);
                }
                return CallInst.dyn_cast(inst);
            case Select:
                return SelectInst.dyn_cast(inst);
            case UserOp1:
            case UserOp2:
                throw new UnsupportedOperationException();
            case VAArg:
                return VAArgInst.dyn_cast(inst);
            case ExtractElement:
                return ExtractElementInst.dyn_cast(inst);
            case InsertElement:
                return InsertElementInst.dyn_cast(inst);
            case ShuffleVector:
                return ShuffleVectorInst.dyn_cast(inst);
            case ExtractValue:
                return ExtractValueInst.dyn_cast(inst);
            case InsertValue:
                return InsertValueInst.dyn_cast(inst);
            case LandingPad:
                return LandingPadInst.dyn_cast(inst);
            case Freeze:
                return FreezeInst.dyn_cast(inst);
            default:
                throw new IllegalArgumentException("Unsupported opcode: " + opcode);
        }
    }

    public static Type refine(Type type) {
        if (type == null) {
            return null;
        }
        TypeID typeID = type.getTypeID();
        switch (typeID) {
            case VoidTyID:
            case HalfTyID:
            case FloatTyID:
            case DoubleTyID:
            case X86_FP80TyID:
            case FP128TyID:
            case PPC_FP128TyID:
            case LabelTyID:
            case MetadataTyID:
            case X86_MMXTyID:
            case TokenTyID:
                return type;
            case IntegerTyID:
                return IntegerType.dyn_cast(type);
            case FunctionTyID:
                return FunctionType.dyn_cast(type);
            case StructTyID:
                return StructType.dyn_cast(type);
            case ArrayTyID:
                return ArrayType.dyn_cast(type);
            case PointerTyID:
                return PointerType.dyn_cast(type);
            case FixedVectorTyID:
                return FixedVectorType.dyn_cast(type);
            case ScalableVectorTyID:
                return ScalableVectorType.dyn_cast(type);
            default:
                throw new IllegalArgumentException("Unsupported typeId " + typeID);
        }
    }
}
