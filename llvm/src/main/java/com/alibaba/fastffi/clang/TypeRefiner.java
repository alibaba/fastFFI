package com.alibaba.fastffi.clang;

public class TypeRefiner {
    public static Type refine(Type type) {
        if (type == null) {
            return null;
        }
        TypeClass typeClass = type.getTypeClass();
        switch (typeClass) {
            case Adjusted:
                return AdjustedType.dyn_cast(type);
            case Decayed:
                return DecayedType.dyn_cast(type);
            case ConstantArray:
                return ConstantArrayType.dyn_cast(type);
            case DependentSizedArray:
                return DependentSizedArrayType.dyn_cast(type);
            case IncompleteArray:
                return IncompleteArrayType.dyn_cast(type);
            case VariableArray:
                return VariableArrayType.dyn_cast(type);
            case Atomic:
                return AtomicType.dyn_cast(type);
            case Attributed:
                return AttributedType.dyn_cast(type);
            case BlockPointer:
                return BlockPointerType.dyn_cast(type);
            case Builtin:
                return BuiltinType.dyn_cast(type);
            case Complex:
                return ComplexType.dyn_cast(type);
            case Decltype:
                return DecltypeType.dyn_cast(type);
            case Auto:
                return AutoType.dyn_cast(type);
            case DeducedTemplateSpecialization:
                return DeducedTemplateSpecializationType.dyn_cast(type);
            case DependentAddressSpace:
                return DependentAddressSpaceType.dyn_cast(type);
            case DependentExtInt:
                return DependentExtIntType.dyn_cast(type);
            case DependentName:
                return DependentNameType.dyn_cast(type);
            case DependentSizedExtVector:
                return DependentSizedExtVectorType.dyn_cast(type);
            case DependentTemplateSpecialization:
                return DependentTemplateSpecializationType.dyn_cast(type);
            case DependentVector:
                return DependentVectorType.dyn_cast(type);
            case Elaborated:
                return ElaboratedType.dyn_cast(type);
            case ExtInt:
                return ExtIntType.dyn_cast(type);
            case FunctionNoProto:
                return FunctionNoProtoType.dyn_cast(type);
            case FunctionProto:
                return FunctionProtoType.dyn_cast(type);
            case InjectedClassName:
                return InjectedClassNameType.dyn_cast(type);
            case MacroQualified:
                return MacroQualifiedType.dyn_cast(type);
            case ConstantMatrix:
                return ConstantMatrixType.dyn_cast(type);
            case DependentSizedMatrix:
                return DependentSizedMatrixType.dyn_cast(type);
            case MemberPointer:
                return MemberPointerType.dyn_cast(type);
            case ObjCObjectPointer:
            case ObjCObject:
            case ObjCInterface:
            case ObjCTypeParam:
                break;
            case PackExpansion:
                return PackExpansionType.dyn_cast(type);
            case Paren:
                return ParenType.dyn_cast(type);
            case Pipe:
                return PipeType.dyn_cast(type);
            case Pointer:
                return PointerType.dyn_cast(type);
            case LValueReference:
                return LValueReferenceType.dyn_cast(type);
            case RValueReference:
                return RValueReferenceType.dyn_cast(type);
            case SubstTemplateTypeParmPack:
                return SubstTemplateTypeParmPackType.dyn_cast(type);
            case SubstTemplateTypeParm:
                return SubstTemplateTypeParmType.dyn_cast(type);
            case Enum:
                return EnumType.dyn_cast(type);
            case Record:
                return RecordType.dyn_cast(type);
            case TemplateSpecialization:
                return TemplateSpecializationType.dyn_cast(type);
            case TemplateTypeParm:
                return TemplateTypeParmType.dyn_cast(type);
            case TypeOfExpr:
                return TypeOfExprType.dyn_cast(type);
            case TypeOf:
                return TypeOfType.dyn_cast(type);
            case Typedef:
                return TypedefType.dyn_cast(type);
            case UnaryTransform:
                return UnaryTransformType.dyn_cast(type);
            case UnresolvedUsing:
                return UnresolvedUsingType.dyn_cast(type);
            case Vector:
                return VectorType.dyn_cast(type);
            case ExtVector:
                return ExtVectorType.dyn_cast(type);
        }
        throw new IllegalArgumentException("Unsupported type: " + typeClass);
    }
}
