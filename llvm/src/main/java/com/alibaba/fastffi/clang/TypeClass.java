package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;

@FFITypeAlias("clang::Type::TypeClass")
@FFITypeRefiner("TypeClass.get")
public enum TypeClass implements CXXEnum {
    Adjusted(Library.INSTANCE.Adjusted()),
    Decayed(Library.INSTANCE.Decayed()),
    ConstantArray(Library.INSTANCE.ConstantArray()),
    DependentSizedArray(Library.INSTANCE.DependentSizedArray()),
    IncompleteArray(Library.INSTANCE.IncompleteArray()),
    VariableArray(Library.INSTANCE.VariableArray()),
    Atomic(Library.INSTANCE.Atomic()),
    Attributed(Library.INSTANCE.Attributed()),
    BlockPointer(Library.INSTANCE.BlockPointer()),
    Builtin(Library.INSTANCE.Builtin()),
    Complex(Library.INSTANCE.Complex()),
    Decltype(Library.INSTANCE.Decltype()),
    Auto(Library.INSTANCE.Auto()),
    DeducedTemplateSpecialization(Library.INSTANCE.DeducedTemplateSpecialization()),
    DependentAddressSpace(Library.INSTANCE.DependentAddressSpace()),
    DependentExtInt(Library.INSTANCE.DependentExtInt()),
    DependentName(Library.INSTANCE.DependentName()),
    DependentSizedExtVector(Library.INSTANCE.DependentSizedExtVector()),
    DependentTemplateSpecialization(Library.INSTANCE.DependentTemplateSpecialization()),
    DependentVector(Library.INSTANCE.DependentVector()),
    Elaborated(Library.INSTANCE.Elaborated()),
    ExtInt(Library.INSTANCE.ExtInt()),
    FunctionNoProto(Library.INSTANCE.FunctionNoProto()),
    FunctionProto(Library.INSTANCE.FunctionProto()),
    InjectedClassName(Library.INSTANCE.InjectedClassName()),
    MacroQualified(Library.INSTANCE.MacroQualified()),
    ConstantMatrix(Library.INSTANCE.ConstantMatrix()),
    DependentSizedMatrix(Library.INSTANCE.DependentSizedMatrix()),
    MemberPointer(Library.INSTANCE.MemberPointer()),
    ObjCObjectPointer(Library.INSTANCE.ObjCObjectPointer()),
    ObjCObject(Library.INSTANCE.ObjCObject()),
    ObjCInterface(Library.INSTANCE.ObjCInterface()),
    ObjCTypeParam(Library.INSTANCE.ObjCTypeParam()),
    PackExpansion(Library.INSTANCE.PackExpansion()),
    Paren(Library.INSTANCE.Paren()),
    Pipe(Library.INSTANCE.Pipe()),
    Pointer(Library.INSTANCE.Pointer()),
    LValueReference(Library.INSTANCE.LValueReference()),
    RValueReference(Library.INSTANCE.RValueReference()),
    SubstTemplateTypeParmPack(Library.INSTANCE.SubstTemplateTypeParmPack()),
    SubstTemplateTypeParm(Library.INSTANCE.SubstTemplateTypeParm()),
    Enum(Library.INSTANCE.Enum()),
    Record(Library.INSTANCE.Record()),
    TemplateSpecialization(Library.INSTANCE.TemplateSpecialization()),
    TemplateTypeParm(Library.INSTANCE.TemplateTypeParm()),
    TypeOfExpr(Library.INSTANCE.TypeOfExpr()),
    TypeOf(Library.INSTANCE.TypeOf()),
    Typedef(Library.INSTANCE.Typedef()),
    UnaryTransform(Library.INSTANCE.UnaryTransform()),
    UnresolvedUsing(Library.INSTANCE.UnresolvedUsing()),
    Vector(Library.INSTANCE.Vector()),
    ExtVector(Library.INSTANCE.ExtVector())
    ;

    @FFIGen
    @FFILibrary(value = "clang::Type::TypeClass", namespace = "clang::Type::TypeClass")
    @CXXHead("clang/AST/Type.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter int Adjusted();
        @FFIGetter int Decayed();
        @FFIGetter int ConstantArray();
        @FFIGetter int DependentSizedArray();
        @FFIGetter int IncompleteArray();
        @FFIGetter int VariableArray();
        @FFIGetter int Atomic();
        @FFIGetter int Attributed();
        @FFIGetter int BlockPointer();
        @FFIGetter int Builtin();
        @FFIGetter int Complex();
        @FFIGetter int Decltype();
        @FFIGetter int Auto();
        @FFIGetter int DeducedTemplateSpecialization();
        @FFIGetter int DependentAddressSpace();
        @FFIGetter int DependentExtInt();
        @FFIGetter int DependentName();
        @FFIGetter int DependentSizedExtVector();
        @FFIGetter int DependentTemplateSpecialization();
        @FFIGetter int DependentVector();
        @FFIGetter int Elaborated();
        @FFIGetter int ExtInt();
        @FFIGetter int FunctionNoProto();
        @FFIGetter int FunctionProto();
        @FFIGetter int InjectedClassName();
        @FFIGetter int MacroQualified();
        @FFIGetter int ConstantMatrix();
        @FFIGetter int DependentSizedMatrix();
        @FFIGetter int MemberPointer();
        @FFIGetter int ObjCObjectPointer();
        @FFIGetter int ObjCObject();
        @FFIGetter int ObjCInterface();
        @FFIGetter int ObjCTypeParam();
        @FFIGetter int PackExpansion();
        @FFIGetter int Paren();
        @FFIGetter int Pipe();
        @FFIGetter int Pointer();
        @FFIGetter int LValueReference();
        @FFIGetter int RValueReference();
        @FFIGetter int SubstTemplateTypeParmPack();
        @FFIGetter int SubstTemplateTypeParm();
        @FFIGetter int Enum();
        @FFIGetter int Record();
        @FFIGetter int TemplateSpecialization();
        @FFIGetter int TemplateTypeParm();
        @FFIGetter int TypeOfExpr();
        @FFIGetter int TypeOf();
        @FFIGetter int Typedef();
        @FFIGetter int UnaryTransform();
        @FFIGetter int UnresolvedUsing();
        @FFIGetter int Vector();
        @FFIGetter int ExtVector();
    }

    int value;
    TypeClass(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    static final CXXEnumMap<TypeClass> map = new CXXEnumMap<>(values());
    public static TypeClass get(int value) {
        return map.get(value);
    }
}
