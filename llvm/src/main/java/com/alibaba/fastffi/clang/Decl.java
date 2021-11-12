package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;

@FFIGen
@FFITypeAlias("clang::Decl")
@CXXHead("clang/AST/DeclBase.h")
@CXXHead("clang/AST/ASTContext.h")
@CXXHead("clang/Basic/SourceManager.h")
@FFITypeRefiner("com.alibaba.fastffi.clang.DeclTypeRefiner.refine")
public interface Decl extends FFIPointer {

    static DeclContext cast(Decl decl) {
        return DeclCasting.INSTANCE.cast(decl, (DeclContext) null);
    }

    @CXXReference ASTContext getASTContext();
    @CXXValue Kind getKind();
    Decl getNextDeclInContext();
    DeclContext getDeclContext();

    boolean isImplicit();

    @FFIExpr("!{0}->isImplicit() && {0}->getASTContext().getSourceManager().isInMainFile({0}->getLocation())")
    boolean isExplicitlyDeclaredInMainFile();

    @CXXValue AccessSpecifier getAccess();

    @CXXValue SourceLocation getBeginLoc();
    @CXXValue SourceLocation getEndLoc();
    @CXXValue SourceLocation getLocation();

    @FFITypeAlias("clang::Decl::Kind")
    @FFITypeRefiner("com.alibaba.fastffi.clang.Decl.Kind.get")
    enum Kind implements CXXEnum {

        AccessSpec(Library.INSTANCE.AccessSpec()),
        Block(Library.INSTANCE.Block()),
        Captured(Library.INSTANCE.Captured()),
        ClassScopeFunctionSpecialization(Library.INSTANCE.ClassScopeFunctionSpecialization()),
        Empty(Library.INSTANCE.Empty()),
        Export(Library.INSTANCE.Export()),
        ExternCContext(Library.INSTANCE.ExternCContext()),
        FileScopeAsm(Library.INSTANCE.FileScopeAsm()),
        Friend(Library.INSTANCE.Friend()),
        FriendTemplate(Library.INSTANCE.FriendTemplate()),
        Import(Library.INSTANCE.Import()),
        LifetimeExtendedTemporary(Library.INSTANCE.LifetimeExtendedTemporary()),
        LinkageSpec(Library.INSTANCE.LinkageSpec()),
        Label(Library.INSTANCE.Label()),
        Namespace(Library.INSTANCE.Namespace()),
        NamespaceAlias(Library.INSTANCE.NamespaceAlias()),
        ObjCCompatibleAlias(Library.INSTANCE.ObjCCompatibleAlias()),
        ObjCCategory(Library.INSTANCE.ObjCCategory()),
        ObjCCategoryImpl(Library.INSTANCE.ObjCCategoryImpl()),
        ObjCImplementation(Library.INSTANCE.ObjCImplementation()),
        ObjCInterface(Library.INSTANCE.ObjCInterface()),
        ObjCProtocol(Library.INSTANCE.ObjCProtocol()),
        ObjCMethod(Library.INSTANCE.ObjCMethod()),
        ObjCProperty(Library.INSTANCE.ObjCProperty()),
        BuiltinTemplate(Library.INSTANCE.BuiltinTemplate()),
        Concept(Library.INSTANCE.Concept()),
        ClassTemplate(Library.INSTANCE.ClassTemplate()),
        FunctionTemplate(Library.INSTANCE.FunctionTemplate()),
        TypeAliasTemplate(Library.INSTANCE.TypeAliasTemplate()),
        VarTemplate(Library.INSTANCE.VarTemplate()),
        TemplateTemplateParm(Library.INSTANCE.TemplateTemplateParm()),
        Enum(Library.INSTANCE.Enum()),
        Record(Library.INSTANCE.Record()),
        CXXRecord(Library.INSTANCE.CXXRecord()),
        ClassTemplateSpecialization(Library.INSTANCE.ClassTemplateSpecialization()),
        ClassTemplatePartialSpecialization(Library.INSTANCE.ClassTemplatePartialSpecialization()),
        TemplateTypeParm(Library.INSTANCE.TemplateTypeParm()),
        ObjCTypeParam(Library.INSTANCE.ObjCTypeParam()),
        TypeAlias(Library.INSTANCE.TypeAlias()),
        Typedef(Library.INSTANCE.Typedef()),
        UnresolvedUsingTypename(Library.INSTANCE.UnresolvedUsingTypename()),
        Using(Library.INSTANCE.Using()),
        UsingDirective(Library.INSTANCE.UsingDirective()),
        UsingPack(Library.INSTANCE.UsingPack()),
        UsingShadow(Library.INSTANCE.UsingShadow()),
        ConstructorUsingShadow(Library.INSTANCE.ConstructorUsingShadow()),
        Binding(Library.INSTANCE.Binding()),
        Field(Library.INSTANCE.Field()),
        ObjCAtDefsField(Library.INSTANCE.ObjCAtDefsField()),
        ObjCIvar(Library.INSTANCE.ObjCIvar()),
        Function(Library.INSTANCE.Function()),
        CXXDeductionGuide(Library.INSTANCE.CXXDeductionGuide()),
        CXXMethod(Library.INSTANCE.CXXMethod()),
        CXXConstructor(Library.INSTANCE.CXXConstructor()),
        CXXConversion(Library.INSTANCE.CXXConversion()),
        CXXDestructor(Library.INSTANCE.CXXDestructor()),
        MSProperty(Library.INSTANCE.MSProperty()),
        NonTypeTemplateParm(Library.INSTANCE.NonTypeTemplateParm()),
        Var(Library.INSTANCE.Var()),
        Decomposition(Library.INSTANCE.Decomposition()),
        ImplicitParam(Library.INSTANCE.ImplicitParam()),
        OMPCapturedExpr(Library.INSTANCE.OMPCapturedExpr()),
        ParmVar(Library.INSTANCE.ParmVar()),
        VarTemplateSpecialization(Library.INSTANCE.VarTemplateSpecialization()),
        VarTemplatePartialSpecialization(Library.INSTANCE.VarTemplatePartialSpecialization()),
        EnumConstant(Library.INSTANCE.EnumConstant()),
        IndirectField(Library.INSTANCE.IndirectField()),
        MSGuid(Library.INSTANCE.MSGuid()),
        OMPDeclareMapper(Library.INSTANCE.OMPDeclareMapper()),
        OMPDeclareReduction(Library.INSTANCE.OMPDeclareReduction()),
        UnresolvedUsingValue(Library.INSTANCE.UnresolvedUsingValue()),
        OMPAllocate(Library.INSTANCE.OMPAllocate()),
        OMPRequires(Library.INSTANCE.OMPRequires()),
        OMPThreadPrivate(Library.INSTANCE.OMPThreadPrivate()),
        ObjCPropertyImpl(Library.INSTANCE.ObjCPropertyImpl()),
        PragmaComment(Library.INSTANCE.PragmaComment()),
        PragmaDetectMismatch(Library.INSTANCE.PragmaDetectMismatch()),
        RequiresExprBody(Library.INSTANCE.RequiresExprBody()),
        StaticAssert(Library.INSTANCE.StaticAssert()),
        TranslationUnit(Library.INSTANCE.TranslationUnit())
        ;

        @FFIGen
        @CXXHead("clang/AST/DeclBase.h")
        @FFILibrary(value = "clang::Decl::Kind", namespace = "clang::Decl::Kind")
        interface Library {
            Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
            @FFIGetter
            int AccessSpec();
            @FFIGetter int Block();
            @FFIGetter int Captured();
            @FFIGetter int ClassScopeFunctionSpecialization();
            @FFIGetter int Empty();
            @FFIGetter int Export();
            @FFIGetter int ExternCContext();
            @FFIGetter int FileScopeAsm();
            @FFIGetter int Friend();
            @FFIGetter int FriendTemplate();
            @FFIGetter int Import();
            @FFIGetter int LifetimeExtendedTemporary();
            @FFIGetter int LinkageSpec();
            @FFIGetter int Label();
            @FFIGetter int Namespace();
            @FFIGetter int NamespaceAlias();
            @FFIGetter int ObjCCompatibleAlias();
            @FFIGetter int ObjCCategory();
            @FFIGetter int ObjCCategoryImpl();
            @FFIGetter int ObjCImplementation();
            @FFIGetter int ObjCInterface();
            @FFIGetter int ObjCProtocol();
            @FFIGetter int ObjCMethod();
            @FFIGetter int ObjCProperty();
            @FFIGetter int BuiltinTemplate();
            @FFIGetter int Concept();
            @FFIGetter int ClassTemplate();
            @FFIGetter int FunctionTemplate();
            @FFIGetter int TypeAliasTemplate();
            @FFIGetter int VarTemplate();
            @FFIGetter int TemplateTemplateParm();
            @FFIGetter int Enum();
            @FFIGetter int Record();
            @FFIGetter int CXXRecord();
            @FFIGetter int ClassTemplateSpecialization();
            @FFIGetter int ClassTemplatePartialSpecialization();
            @FFIGetter int TemplateTypeParm();
            @FFIGetter int ObjCTypeParam();
            @FFIGetter int TypeAlias();
            @FFIGetter int Typedef();
            @FFIGetter int UnresolvedUsingTypename();
            @FFIGetter int Using();
            @FFIGetter int UsingDirective();
            @FFIGetter int UsingPack();
            @FFIGetter int UsingShadow();
            @FFIGetter int ConstructorUsingShadow();
            @FFIGetter int Binding();
            @FFIGetter int Field();
            @FFIGetter int ObjCAtDefsField();
            @FFIGetter int ObjCIvar();
            @FFIGetter int Function();
            @FFIGetter int CXXDeductionGuide();
            @FFIGetter int CXXMethod();
            @FFIGetter int CXXConstructor();
            @FFIGetter int CXXConversion();
            @FFIGetter int CXXDestructor();
            @FFIGetter int MSProperty();
            @FFIGetter int NonTypeTemplateParm();
            @FFIGetter int Var();
            @FFIGetter int Decomposition();
            @FFIGetter int ImplicitParam();
            @FFIGetter int OMPCapturedExpr();
            @FFIGetter int ParmVar();
            @FFIGetter int VarTemplateSpecialization();
            @FFIGetter int VarTemplatePartialSpecialization();
            @FFIGetter int EnumConstant();
            @FFIGetter int IndirectField();
            @FFIGetter int MSGuid();
            @FFIGetter int OMPDeclareMapper();
            @FFIGetter int OMPDeclareReduction();
            @FFIGetter int UnresolvedUsingValue();
            @FFIGetter int OMPAllocate();
            @FFIGetter int OMPRequires();
            @FFIGetter int OMPThreadPrivate();
            @FFIGetter int ObjCPropertyImpl();
            @FFIGetter int PragmaComment();
            @FFIGetter int PragmaDetectMismatch();
            @FFIGetter int RequiresExprBody();
            @FFIGetter int StaticAssert();
            @FFIGetter int TranslationUnit();
        }

        int value;
        Kind(int value) {
            this.value = value;
        }

        private static CXXEnumMap<Kind> map = new CXXEnumMap<>(Kind.values());
        public static Kind get(int value) {
            return map.get(value);
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
