package com.alibaba.fastffi.clang;

public class DeclTypeRefiner {

    static boolean doRefine = true;

    public static Decl refine(Decl decl) {
        if (!doRefine) {
            return decl;
        }
        if (decl == null) {
            return null;
        }
        Decl.Kind kind = decl.getKind();
        switch (kind) {
            case AccessSpec:
                return AccessSpecDecl.dyn_cast(decl);
            case Block:
                return BlockDecl.dyn_cast(decl);
            case Captured:
                return CapturedDecl.dyn_cast(decl);
            case ClassScopeFunctionSpecialization:
                return ClassScopeFunctionSpecializationDecl.dyn_cast(decl);
            case Empty:
                return EmptyDecl.dyn_cast(decl);
            case Export:
                return ExportDecl.dyn_cast(decl);
            case ExternCContext:
                return ExternCContextDecl.dyn_cast(decl);
            case FileScopeAsm:
                return FileScopeAsmDecl.dyn_cast(decl);
            case Friend:
                return FriendDecl.dyn_cast(decl);
            case FriendTemplate:
                return FriendTemplateDecl.dyn_cast(decl);
            case Import:
                return ImportDecl.dyn_cast(decl);
            case LifetimeExtendedTemporary:
                break;
            case LinkageSpec:
                return LinkageSpecDecl.dyn_cast(decl);
            case Label:
                return LabelDecl.dyn_cast(decl);
            case Namespace:
                return NamespaceDecl.dyn_cast(decl);
            case NamespaceAlias:
                return NamespaceAliasDecl.dyn_cast(decl);
            case ObjCCompatibleAlias:
            case ObjCCategory:
            case ObjCCategoryImpl:
            case ObjCImplementation:
            case ObjCInterface:
            case ObjCProtocol:
            case ObjCMethod:
            case ObjCProperty:
                break;
            case BuiltinTemplate:
                return BuiltinTemplateDecl.dyn_cast(decl);
            case Concept:
                return ConceptDecl.dyn_cast(decl);
            case ClassTemplate:
                return ClassTemplateDecl.dyn_cast(decl);
            case FunctionTemplate:
                return FunctionTemplateDecl.dyn_cast(decl);
            case TypeAliasTemplate:
                return TypeAliasTemplateDecl.dyn_cast(decl);
            case VarTemplate:
                return VarTemplateDecl.dyn_cast(decl);
            case TemplateTemplateParm:
                return TemplateTemplateParmDecl.dyn_cast(decl);
            case Enum:
                return EnumDecl.dyn_cast(decl);
            case Record:
                return RecordDecl.dyn_cast(decl);
            case CXXRecord:
                return CXXRecordDecl.dyn_cast(decl);
            case ClassTemplateSpecialization:
                return ClassTemplateSpecializationDecl.dyn_cast(decl);
            case ClassTemplatePartialSpecialization:
                return ClassTemplatePartialSpecializationDecl.dyn_cast(decl);
            case TemplateTypeParm:
                return TemplateTypeParmDecl.dyn_cast(decl);
            case ObjCTypeParam:
                break;
            case TypeAlias:
                return TypeAliasDecl.dyn_cast(decl);
            case Typedef:
                return TypedefDecl.dyn_cast(decl);
            case UnresolvedUsingTypename:
                return UnresolvedUsingTypenameDecl.dyn_cast(decl);
            case Using:
                return UsingDecl.dyn_cast(decl);
            case UsingDirective:
                return UsingDirectiveDecl.dyn_cast(decl);
            case UsingPack:
                return UsingPackDecl.dyn_cast(decl);
            case UsingShadow:
                return UsingShadowDecl.dyn_cast(decl);
            case ConstructorUsingShadow:
                return ConstructorUsingShadowDecl.dyn_cast(decl);
            case Binding:
                return BindingDecl.dyn_cast(decl);
            case Field:
                return FieldDecl.dyn_cast(decl);
            case ObjCAtDefsField:
            case ObjCIvar:
                break;
            case Function:
                return FunctionDecl.dyn_cast(decl);
            case CXXDeductionGuide:
                return CXXDeductionGuideDecl.dyn_cast(decl);
            case CXXMethod:
                return CXXMethodDecl.dyn_cast(decl);
            case CXXConstructor:
                return CXXConstructorDecl.dyn_cast(decl);
            case CXXConversion:
                return CXXConversionDecl.dyn_cast(decl);
            case CXXDestructor:
                return CXXDestructorDecl.dyn_cast(decl);
            case MSProperty:
                return MSPropertyDecl.dyn_cast(decl);
            case NonTypeTemplateParm:
                return NonTypeTemplateParmDecl.dyn_cast(decl);
            case Var:
                return VarDecl.dyn_cast(decl);
            case Decomposition:
                return DecompositionDecl.dyn_cast(decl);
            case ImplicitParam:
                return ImplicitParamDecl.dyn_cast(decl);
            case OMPCapturedExpr:
                break;
            case ParmVar:
                return ParmVarDecl.dyn_cast(decl);
            case VarTemplateSpecialization:
                return VarTemplateSpecializationDecl.dyn_cast(decl);
            case VarTemplatePartialSpecialization:
                return VarTemplatePartialSpecializationDecl.dyn_cast(decl);
            case EnumConstant:
                return EnumConstantDecl.dyn_cast(decl);
            case IndirectField:
                return IndirectFieldDecl.dyn_cast(decl);
            case MSGuid:
                return MSGuidDecl.dyn_cast(decl);
            case OMPDeclareMapper:
            case OMPDeclareReduction:
            case UnresolvedUsingValue:
                return UnresolvedUsingTypenameDecl.dyn_cast(decl);
            case OMPAllocate:
            case OMPRequires:
            case OMPThreadPrivate:
            case ObjCPropertyImpl:
                break;
            case PragmaComment:
                return PragmaCommentDecl.dyn_cast(decl);
            case PragmaDetectMismatch:
                return PragmaDetectMismatchDecl.dyn_cast(decl);
            case RequiresExprBody:
                return RequiresExprBodyDecl.dyn_cast(decl);
            case StaticAssert:
                return StaticAssertDecl.dyn_cast(decl);
            case TranslationUnit:
                return TranslationUnitDecl.dyn_cast(decl);
            default:
                break;
        }

        throw new IllegalArgumentException("Not a supported Decl: " + decl.getKind());
    }
}
