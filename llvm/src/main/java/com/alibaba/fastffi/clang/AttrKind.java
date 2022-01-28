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

@FFITypeAlias("clang::attr::Kind")
@FFITypeRefiner("AttrKind.get")
public enum AttrKind implements CXXEnum {
    AddressSpace(Library.INSTANCE.AddressSpace()),
    ArmMveStrictPolymorphism(Library.INSTANCE.ArmMveStrictPolymorphism()),
    CmseNSCall(Library.INSTANCE.CmseNSCall()),
    NoDeref(Library.INSTANCE.NoDeref()),
    ObjCGC(Library.INSTANCE.ObjCGC()),
    ObjCInertUnsafeUnretained(Library.INSTANCE.ObjCInertUnsafeUnretained()),
    ObjCKindOf(Library.INSTANCE.ObjCKindOf()),
    OpenCLConstantAddressSpace(Library.INSTANCE.OpenCLConstantAddressSpace()),
    OpenCLGenericAddressSpace(Library.INSTANCE.OpenCLGenericAddressSpace()),
    OpenCLGlobalAddressSpace(Library.INSTANCE.OpenCLGlobalAddressSpace()),
    OpenCLLocalAddressSpace(Library.INSTANCE.OpenCLLocalAddressSpace()),
    OpenCLPrivateAddressSpace(Library.INSTANCE.OpenCLPrivateAddressSpace()),
    Ptr32(Library.INSTANCE.Ptr32()),
    Ptr64(Library.INSTANCE.Ptr64()),
    SPtr(Library.INSTANCE.SPtr()),
    TypeNonNull(Library.INSTANCE.TypeNonNull()),
    TypeNullUnspecified(Library.INSTANCE.TypeNullUnspecified()),
    TypeNullable(Library.INSTANCE.TypeNullable()),
    UPtr(Library.INSTANCE.UPtr()),
    FallThrough(Library.INSTANCE.FallThrough()),
    NoMerge(Library.INSTANCE.NoMerge()),
    Suppress(Library.INSTANCE.Suppress()),
    AArch64VectorPcs(Library.INSTANCE.AArch64VectorPcs()),
    AcquireHandle(Library.INSTANCE.AcquireHandle()),
    AnyX86NoCfCheck(Library.INSTANCE.AnyX86NoCfCheck()),
    CDecl(Library.INSTANCE.CDecl()),
    FastCall(Library.INSTANCE.FastCall()),
    IntelOclBicc(Library.INSTANCE.IntelOclBicc()),
    LifetimeBound(Library.INSTANCE.LifetimeBound()),
    MSABI(Library.INSTANCE.MSABI()),
    NSReturnsRetained(Library.INSTANCE.NSReturnsRetained()),
    ObjCOwnership(Library.INSTANCE.ObjCOwnership()),
    Pascal(Library.INSTANCE.Pascal()),
    Pcs(Library.INSTANCE.Pcs()),
    PreserveAll(Library.INSTANCE.PreserveAll()),
    PreserveMost(Library.INSTANCE.PreserveMost()),
    RegCall(Library.INSTANCE.RegCall()),
    StdCall(Library.INSTANCE.StdCall()),
    SwiftCall(Library.INSTANCE.SwiftCall()),
    SysVABI(Library.INSTANCE.SysVABI()),
    ThisCall(Library.INSTANCE.ThisCall()),
    VectorCall(Library.INSTANCE.VectorCall()),
    SwiftContext(Library.INSTANCE.SwiftContext()),
    SwiftErrorResult(Library.INSTANCE.SwiftErrorResult()),
    SwiftIndirectResult(Library.INSTANCE.SwiftIndirectResult()),
    Annotate(Library.INSTANCE.Annotate()),
    CFConsumed(Library.INSTANCE.CFConsumed()),
    CarriesDependency(Library.INSTANCE.CarriesDependency()),
    NSConsumed(Library.INSTANCE.NSConsumed()),
    NonNull(Library.INSTANCE.NonNull()),
    OSConsumed(Library.INSTANCE.OSConsumed()),
    PassObjectSize(Library.INSTANCE.PassObjectSize()),
    ReleaseHandle(Library.INSTANCE.ReleaseHandle()),
    UseHandle(Library.INSTANCE.UseHandle()),
    AMDGPUFlatWorkGroupSize(Library.INSTANCE.AMDGPUFlatWorkGroupSize()),
    AMDGPUNumSGPR(Library.INSTANCE.AMDGPUNumSGPR()),
    AMDGPUNumVGPR(Library.INSTANCE.AMDGPUNumVGPR()),
    AMDGPUWavesPerEU(Library.INSTANCE.AMDGPUWavesPerEU()),
    ARMInterrupt(Library.INSTANCE.ARMInterrupt()),
    AVRInterrupt(Library.INSTANCE.AVRInterrupt()),
    AVRSignal(Library.INSTANCE.AVRSignal()),
    AcquireCapability(Library.INSTANCE.AcquireCapability()),
    AcquiredAfter(Library.INSTANCE.AcquiredAfter()),
    AcquiredBefore(Library.INSTANCE.AcquiredBefore()),
    AlignMac68k(Library.INSTANCE.AlignMac68k()),
    Aligned(Library.INSTANCE.Aligned()),
    AllocAlign(Library.INSTANCE.AllocAlign()),
    AllocSize(Library.INSTANCE.AllocSize()),
    AlwaysDestroy(Library.INSTANCE.AlwaysDestroy()),
    AlwaysInline(Library.INSTANCE.AlwaysInline()),
    AnalyzerNoReturn(Library.INSTANCE.AnalyzerNoReturn()),
    AnyX86Interrupt(Library.INSTANCE.AnyX86Interrupt()),
    AnyX86NoCallerSavedRegisters(Library.INSTANCE.AnyX86NoCallerSavedRegisters()),
    ArcWeakrefUnavailable(Library.INSTANCE.ArcWeakrefUnavailable()),
    ArgumentWithTypeTag(Library.INSTANCE.ArgumentWithTypeTag()),
    ArmBuiltinAlias(Library.INSTANCE.ArmBuiltinAlias()),
    Artificial(Library.INSTANCE.Artificial()),
    AsmLabel(Library.INSTANCE.AsmLabel()),
    AssertCapability(Library.INSTANCE.AssertCapability()),
    AssertExclusiveLock(Library.INSTANCE.AssertExclusiveLock()),
    AssertSharedLock(Library.INSTANCE.AssertSharedLock()),
    AssumeAligned(Library.INSTANCE.AssumeAligned()),
    Availability(Library.INSTANCE.Availability()),
    BPFPreserveAccessIndex(Library.INSTANCE.BPFPreserveAccessIndex()),
    Blocks(Library.INSTANCE.Blocks()),
    Builtin(Library.INSTANCE.Builtin()),
    C11NoReturn(Library.INSTANCE.C11NoReturn()),
    CFAuditedTransfer(Library.INSTANCE.CFAuditedTransfer()),
    CFGuard(Library.INSTANCE.CFGuard()),
    CFICanonicalJumpTable(Library.INSTANCE.CFICanonicalJumpTable()),
    CFReturnsNotRetained(Library.INSTANCE.CFReturnsNotRetained()),
    CFReturnsRetained(Library.INSTANCE.CFReturnsRetained()),
    CFUnknownTransfer(Library.INSTANCE.CFUnknownTransfer()),
    CPUDispatch(Library.INSTANCE.CPUDispatch()),
    CPUSpecific(Library.INSTANCE.CPUSpecific()),
    CUDAConstant(Library.INSTANCE.CUDAConstant()),
    CUDADevice(Library.INSTANCE.CUDADevice()),
    CUDADeviceBuiltinSurfaceType(Library.INSTANCE.CUDADeviceBuiltinSurfaceType()),
    CUDADeviceBuiltinTextureType(Library.INSTANCE.CUDADeviceBuiltinTextureType()),
    CUDAGlobal(Library.INSTANCE.CUDAGlobal()),
    CUDAHost(Library.INSTANCE.CUDAHost()),
    CUDAInvalidTarget(Library.INSTANCE.CUDAInvalidTarget()),
    CUDALaunchBounds(Library.INSTANCE.CUDALaunchBounds()),
    CUDAShared(Library.INSTANCE.CUDAShared()),
    CXX11NoReturn(Library.INSTANCE.CXX11NoReturn()),
    CallableWhen(Library.INSTANCE.CallableWhen()),
    Callback(Library.INSTANCE.Callback()),
    Capability(Library.INSTANCE.Capability()),
    CapturedRecord(Library.INSTANCE.CapturedRecord()),
    Cleanup(Library.INSTANCE.Cleanup()),
    CmseNSEntry(Library.INSTANCE.CmseNSEntry()),
    CodeSeg(Library.INSTANCE.CodeSeg()),
    Cold(Library.INSTANCE.Cold()),
    Common(Library.INSTANCE.Common()),
    Const(Library.INSTANCE.Const()),
    ConstInit(Library.INSTANCE.ConstInit()),
    Constructor(Library.INSTANCE.Constructor()),
    Consumable(Library.INSTANCE.Consumable()),
    ConsumableAutoCast(Library.INSTANCE.ConsumableAutoCast()),
    ConsumableSetOnRead(Library.INSTANCE.ConsumableSetOnRead()),
    Convergent(Library.INSTANCE.Convergent()),
    DLLExport(Library.INSTANCE.DLLExport()),
    DLLExportStaticLocal(Library.INSTANCE.DLLExportStaticLocal()),
    DLLImport(Library.INSTANCE.DLLImport()),
    DLLImportStaticLocal(Library.INSTANCE.DLLImportStaticLocal()),
    Deprecated(Library.INSTANCE.Deprecated()),
    Destructor(Library.INSTANCE.Destructor()),
    DiagnoseIf(Library.INSTANCE.DiagnoseIf()),
    DisableTailCalls(Library.INSTANCE.DisableTailCalls()),
    EmptyBases(Library.INSTANCE.EmptyBases()),
    EnableIf(Library.INSTANCE.EnableIf()),
    EnumExtensibility(Library.INSTANCE.EnumExtensibility()),
    ExcludeFromExplicitInstantiation(Library.INSTANCE.ExcludeFromExplicitInstantiation()),
    ExclusiveTrylockFunction(Library.INSTANCE.ExclusiveTrylockFunction()),
    ExternalSourceSymbol(Library.INSTANCE.ExternalSourceSymbol()),
    Final(Library.INSTANCE.Final()),
    FlagEnum(Library.INSTANCE.FlagEnum()),
    Flatten(Library.INSTANCE.Flatten()),
    Format(Library.INSTANCE.Format()),
    FormatArg(Library.INSTANCE.FormatArg()),
    GNUInline(Library.INSTANCE.GNUInline()),
    GuardedBy(Library.INSTANCE.GuardedBy()),
    GuardedVar(Library.INSTANCE.GuardedVar()),
    Hot(Library.INSTANCE.Hot()),
    IBAction(Library.INSTANCE.IBAction()),
    IBOutlet(Library.INSTANCE.IBOutlet()),
    IBOutletCollection(Library.INSTANCE.IBOutletCollection()),
    InitPriority(Library.INSTANCE.InitPriority()),
    InternalLinkage(Library.INSTANCE.InternalLinkage()),
    LTOVisibilityPublic(Library.INSTANCE.LTOVisibilityPublic()),
    LayoutVersion(Library.INSTANCE.LayoutVersion()),
    LockReturned(Library.INSTANCE.LockReturned()),
    LocksExcluded(Library.INSTANCE.LocksExcluded()),
    MIGServerRoutine(Library.INSTANCE.MIGServerRoutine()),
    MSAllocator(Library.INSTANCE.MSAllocator()),
    MSInheritance(Library.INSTANCE.MSInheritance()),
    MSNoVTable(Library.INSTANCE.MSNoVTable()),
    MSP430Interrupt(Library.INSTANCE.MSP430Interrupt()),
    MSStruct(Library.INSTANCE.MSStruct()),
    MSVtorDisp(Library.INSTANCE.MSVtorDisp()),
    MaxFieldAlignment(Library.INSTANCE.MaxFieldAlignment()),
    MayAlias(Library.INSTANCE.MayAlias()),
    MicroMips(Library.INSTANCE.MicroMips()),
    MinSize(Library.INSTANCE.MinSize()),
    MinVectorWidth(Library.INSTANCE.MinVectorWidth()),
    Mips16(Library.INSTANCE.Mips16()),
    MipsInterrupt(Library.INSTANCE.MipsInterrupt()),
    MipsLongCall(Library.INSTANCE.MipsLongCall()),
    MipsShortCall(Library.INSTANCE.MipsShortCall()),
    NSConsumesSelf(Library.INSTANCE.NSConsumesSelf()),
    NSReturnsAutoreleased(Library.INSTANCE.NSReturnsAutoreleased()),
    NSReturnsNotRetained(Library.INSTANCE.NSReturnsNotRetained()),
    Naked(Library.INSTANCE.Naked()),
    NoAlias(Library.INSTANCE.NoAlias()),
    NoCommon(Library.INSTANCE.NoCommon()),
    NoDebug(Library.INSTANCE.NoDebug()),
    NoDestroy(Library.INSTANCE.NoDestroy()),
    NoDuplicate(Library.INSTANCE.NoDuplicate()),
    NoInline(Library.INSTANCE.NoInline()),
    NoInstrumentFunction(Library.INSTANCE.NoInstrumentFunction()),
    NoMicroMips(Library.INSTANCE.NoMicroMips()),
    NoMips16(Library.INSTANCE.NoMips16()),
    NoReturn(Library.INSTANCE.NoReturn()),
    NoSanitize(Library.INSTANCE.NoSanitize()),
    NoSpeculativeLoadHardening(Library.INSTANCE.NoSpeculativeLoadHardening()),
    NoSplitStack(Library.INSTANCE.NoSplitStack()),
    NoStackProtector(Library.INSTANCE.NoStackProtector()),
    NoThreadSafetyAnalysis(Library.INSTANCE.NoThreadSafetyAnalysis()),
    NoThrow(Library.INSTANCE.NoThrow()),
    NoUniqueAddress(Library.INSTANCE.NoUniqueAddress()),
    NotTailCalled(Library.INSTANCE.NotTailCalled()),
    OMPAllocateDecl(Library.INSTANCE.OMPAllocateDecl()),
    OMPCaptureNoInit(Library.INSTANCE.OMPCaptureNoInit()),
    OMPDeclareTargetDecl(Library.INSTANCE.OMPDeclareTargetDecl()),
    OMPDeclareVariant(Library.INSTANCE.OMPDeclareVariant()),
    OMPThreadPrivateDecl(Library.INSTANCE.OMPThreadPrivateDecl()),
    OSConsumesThis(Library.INSTANCE.OSConsumesThis()),
    OSReturnsNotRetained(Library.INSTANCE.OSReturnsNotRetained()),
    OSReturnsRetained(Library.INSTANCE.OSReturnsRetained()),
    OSReturnsRetainedOnNonZero(Library.INSTANCE.OSReturnsRetainedOnNonZero()),
    OSReturnsRetainedOnZero(Library.INSTANCE.OSReturnsRetainedOnZero()),
    ObjCBridge(Library.INSTANCE.ObjCBridge()),
    ObjCBridgeMutable(Library.INSTANCE.ObjCBridgeMutable()),
    ObjCBridgeRelated(Library.INSTANCE.ObjCBridgeRelated()),
    ObjCException(Library.INSTANCE.ObjCException()),
    ObjCExplicitProtocolImpl(Library.INSTANCE.ObjCExplicitProtocolImpl()),
    ObjCExternallyRetained(Library.INSTANCE.ObjCExternallyRetained()),
    ObjCIndependentClass(Library.INSTANCE.ObjCIndependentClass()),
    ObjCMethodFamily(Library.INSTANCE.ObjCMethodFamily()),
    ObjCNSObject(Library.INSTANCE.ObjCNSObject()),
    ObjCPreciseLifetime(Library.INSTANCE.ObjCPreciseLifetime()),
    ObjCRequiresPropertyDefs(Library.INSTANCE.ObjCRequiresPropertyDefs()),
    ObjCRequiresSuper(Library.INSTANCE.ObjCRequiresSuper()),
    ObjCReturnsInnerPointer(Library.INSTANCE.ObjCReturnsInnerPointer()),
    ObjCRootClass(Library.INSTANCE.ObjCRootClass()),
    ObjCSubclassingRestricted(Library.INSTANCE.ObjCSubclassingRestricted()),
    OpenCLIntelReqdSubGroupSize(Library.INSTANCE.OpenCLIntelReqdSubGroupSize()),
    OpenCLKernel(Library.INSTANCE.OpenCLKernel()),
    OpenCLUnrollHint(Library.INSTANCE.OpenCLUnrollHint()),
    OptimizeNone(Library.INSTANCE.OptimizeNone()),
    Override(Library.INSTANCE.Override()),
    Owner(Library.INSTANCE.Owner()),
    Ownership(Library.INSTANCE.Ownership()),
    Packed(Library.INSTANCE.Packed()),
    ParamTypestate(Library.INSTANCE.ParamTypestate()),
    PatchableFunctionEntry(Library.INSTANCE.PatchableFunctionEntry()),
    Pointer(Library.INSTANCE.Pointer()),
    PragmaClangBSSSection(Library.INSTANCE.PragmaClangBSSSection()),
    PragmaClangDataSection(Library.INSTANCE.PragmaClangDataSection()),
    PragmaClangRelroSection(Library.INSTANCE.PragmaClangRelroSection()),
    PragmaClangRodataSection(Library.INSTANCE.PragmaClangRodataSection()),
    PragmaClangTextSection(Library.INSTANCE.PragmaClangTextSection()),
    PtGuardedBy(Library.INSTANCE.PtGuardedBy()),
    PtGuardedVar(Library.INSTANCE.PtGuardedVar()),
    Pure(Library.INSTANCE.Pure()),
    RISCVInterrupt(Library.INSTANCE.RISCVInterrupt()),
    Reinitializes(Library.INSTANCE.Reinitializes()),
    ReleaseCapability(Library.INSTANCE.ReleaseCapability()),
    ReqdWorkGroupSize(Library.INSTANCE.ReqdWorkGroupSize()),
    RequiresCapability(Library.INSTANCE.RequiresCapability()),
    Restrict(Library.INSTANCE.Restrict()),
    ReturnTypestate(Library.INSTANCE.ReturnTypestate()),
    ReturnsNonNull(Library.INSTANCE.ReturnsNonNull()),
    ReturnsTwice(Library.INSTANCE.ReturnsTwice()),
    SYCLKernel(Library.INSTANCE.SYCLKernel()),
    ScopedLockable(Library.INSTANCE.ScopedLockable()),
    Section(Library.INSTANCE.Section()),
    SelectAny(Library.INSTANCE.SelectAny()),
    Sentinel(Library.INSTANCE.Sentinel()),
    SetTypestate(Library.INSTANCE.SetTypestate()),
    SharedTrylockFunction(Library.INSTANCE.SharedTrylockFunction()),
    SpeculativeLoadHardening(Library.INSTANCE.SpeculativeLoadHardening()),
    TLSModel(Library.INSTANCE.TLSModel()),
    Target(Library.INSTANCE.Target()),
    TestTypestate(Library.INSTANCE.TestTypestate()),
    TransparentUnion(Library.INSTANCE.TransparentUnion()),
    TrivialABI(Library.INSTANCE.TrivialABI()),
    TryAcquireCapability(Library.INSTANCE.TryAcquireCapability()),
    TypeTagForDatatype(Library.INSTANCE.TypeTagForDatatype()),
    TypeVisibility(Library.INSTANCE.TypeVisibility()),
    Unavailable(Library.INSTANCE.Unavailable()),
    Uninitialized(Library.INSTANCE.Uninitialized()),
    Unused(Library.INSTANCE.Unused()),
    Used(Library.INSTANCE.Used()),
    Uuid(Library.INSTANCE.Uuid()),
    VecReturn(Library.INSTANCE.VecReturn()),
    VecTypeHint(Library.INSTANCE.VecTypeHint()),
    Visibility(Library.INSTANCE.Visibility()),
    WarnUnused(Library.INSTANCE.WarnUnused()),
    WarnUnusedResult(Library.INSTANCE.WarnUnusedResult()),
    Weak(Library.INSTANCE.Weak()),
    WeakImport(Library.INSTANCE.WeakImport()),
    WeakRef(Library.INSTANCE.WeakRef()),
    WebAssemblyExportName(Library.INSTANCE.WebAssemblyExportName()),
    WebAssemblyImportModule(Library.INSTANCE.WebAssemblyImportModule()),
    WebAssemblyImportName(Library.INSTANCE.WebAssemblyImportName()),
    WorkGroupSizeHint(Library.INSTANCE.WorkGroupSizeHint()),
    X86ForceAlignArgPointer(Library.INSTANCE.X86ForceAlignArgPointer()),
    XRayInstrument(Library.INSTANCE.XRayInstrument()),
    XRayLogArgs(Library.INSTANCE.XRayLogArgs()),
    AbiTag(Library.INSTANCE.AbiTag()),
    Alias(Library.INSTANCE.Alias()),
    AlignValue(Library.INSTANCE.AlignValue()),
    IFunc(Library.INSTANCE.IFunc()),
    InitSeg(Library.INSTANCE.InitSeg()),
    LoaderUninitialized(Library.INSTANCE.LoaderUninitialized()),
    LoopHint(Library.INSTANCE.LoopHint()),
    Mode(Library.INSTANCE.Mode()),
    NoBuiltin(Library.INSTANCE.NoBuiltin()),
    NoEscape(Library.INSTANCE.NoEscape()),
    OMPCaptureKind(Library.INSTANCE.OMPCaptureKind()),
    OMPDeclareSimdDecl(Library.INSTANCE.OMPDeclareSimdDecl()),
    OMPReferencedVar(Library.INSTANCE.OMPReferencedVar()),
    ObjCBoxable(Library.INSTANCE.ObjCBoxable()),
    ObjCClassStub(Library.INSTANCE.ObjCClassStub()),
    ObjCDesignatedInitializer(Library.INSTANCE.ObjCDesignatedInitializer()),
    ObjCDirect(Library.INSTANCE.ObjCDirect()),
    ObjCDirectMembers(Library.INSTANCE.ObjCDirectMembers()),
    ObjCNonLazyClass(Library.INSTANCE.ObjCNonLazyClass()),
    ObjCRuntimeName(Library.INSTANCE.ObjCRuntimeName()),
    ObjCRuntimeVisible(Library.INSTANCE.ObjCRuntimeVisible()),
    OpenCLAccess(Library.INSTANCE.OpenCLAccess()),
    Overloadable(Library.INSTANCE.Overloadable()),
    RenderScriptKernel(Library.INSTANCE.RenderScriptKernel()),
    Thread(Library.INSTANCE.Thread()),
    FirstAttr(Library.INSTANCE.FirstAttr()),
    LastAttr(Library.INSTANCE.LastAttr()),
    FirstTypeAttr(Library.INSTANCE.FirstTypeAttr()),
    LastTypeAttr(Library.INSTANCE.LastTypeAttr()),
    FirstStmtAttr(Library.INSTANCE.FirstStmtAttr()),
    LastStmtAttr(Library.INSTANCE.LastStmtAttr()),
    FirstInheritableAttr(Library.INSTANCE.FirstInheritableAttr()),
    LastInheritableAttr(Library.INSTANCE.LastInheritableAttr()),
    FirstDeclOrTypeAttr(Library.INSTANCE.FirstDeclOrTypeAttr()),
    LastDeclOrTypeAttr(Library.INSTANCE.LastDeclOrTypeAttr()),
    FirstInheritableParamAttr(Library.INSTANCE.FirstInheritableParamAttr()),
    LastInheritableParamAttr(Library.INSTANCE.LastInheritableParamAttr()),
    FirstParameterABIAttr(Library.INSTANCE.FirstParameterABIAttr()),
    LastParameterABIAttr(Library.INSTANCE.LastParameterABIAttr())
    ;

    @FFIGen
    @FFILibrary(value = "clang::attr::Kind", namespace = "clang::attr::Kind")
    @CXXHead("clang/Basic/AttrKinds.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter int AddressSpace();
        @FFIGetter int ArmMveStrictPolymorphism();
        @FFIGetter int CmseNSCall();
        @FFIGetter int NoDeref();
        @FFIGetter int ObjCGC();
        @FFIGetter int ObjCInertUnsafeUnretained();
        @FFIGetter int ObjCKindOf();
        @FFIGetter int OpenCLConstantAddressSpace();
        @FFIGetter int OpenCLGenericAddressSpace();
        @FFIGetter int OpenCLGlobalAddressSpace();
        @FFIGetter int OpenCLLocalAddressSpace();
        @FFIGetter int OpenCLPrivateAddressSpace();
        @FFIGetter int Ptr32();
        @FFIGetter int Ptr64();
        @FFIGetter int SPtr();
        @FFIGetter int TypeNonNull();
        @FFIGetter int TypeNullUnspecified();
        @FFIGetter int TypeNullable();
        @FFIGetter int UPtr();
        @FFIGetter int FallThrough();
        @FFIGetter int NoMerge();
        @FFIGetter int Suppress();
        @FFIGetter int AArch64VectorPcs();
        @FFIGetter int AcquireHandle();
        @FFIGetter int AnyX86NoCfCheck();
        @FFIGetter int CDecl();
        @FFIGetter int FastCall();
        @FFIGetter int IntelOclBicc();
        @FFIGetter int LifetimeBound();
        @FFIGetter int MSABI();
        @FFIGetter int NSReturnsRetained();
        @FFIGetter int ObjCOwnership();
        @FFIGetter int Pascal();
        @FFIGetter int Pcs();
        @FFIGetter int PreserveAll();
        @FFIGetter int PreserveMost();
        @FFIGetter int RegCall();
        @FFIGetter int StdCall();
        @FFIGetter int SwiftCall();
        @FFIGetter int SysVABI();
        @FFIGetter int ThisCall();
        @FFIGetter int VectorCall();
        @FFIGetter int SwiftContext();
        @FFIGetter int SwiftErrorResult();
        @FFIGetter int SwiftIndirectResult();
        @FFIGetter int Annotate();
        @FFIGetter int CFConsumed();
        @FFIGetter int CarriesDependency();
        @FFIGetter int NSConsumed();
        @FFIGetter int NonNull();
        @FFIGetter int OSConsumed();
        @FFIGetter int PassObjectSize();
        @FFIGetter int ReleaseHandle();
        @FFIGetter int UseHandle();
        @FFIGetter int AMDGPUFlatWorkGroupSize();
        @FFIGetter int AMDGPUNumSGPR();
        @FFIGetter int AMDGPUNumVGPR();
        @FFIGetter int AMDGPUWavesPerEU();
        @FFIGetter int ARMInterrupt();
        @FFIGetter int AVRInterrupt();
        @FFIGetter int AVRSignal();
        @FFIGetter int AcquireCapability();
        @FFIGetter int AcquiredAfter();
        @FFIGetter int AcquiredBefore();
        @FFIGetter int AlignMac68k();
        @FFIGetter int Aligned();
        @FFIGetter int AllocAlign();
        @FFIGetter int AllocSize();
        @FFIGetter int AlwaysDestroy();
        @FFIGetter int AlwaysInline();
        @FFIGetter int AnalyzerNoReturn();
        @FFIGetter int AnyX86Interrupt();
        @FFIGetter int AnyX86NoCallerSavedRegisters();
        @FFIGetter int ArcWeakrefUnavailable();
        @FFIGetter int ArgumentWithTypeTag();
        @FFIGetter int ArmBuiltinAlias();
        @FFIGetter int Artificial();
        @FFIGetter int AsmLabel();
        @FFIGetter int AssertCapability();
        @FFIGetter int AssertExclusiveLock();
        @FFIGetter int AssertSharedLock();
        @FFIGetter int AssumeAligned();
        @FFIGetter int Availability();
        @FFIGetter int BPFPreserveAccessIndex();
        @FFIGetter int Blocks();
        @FFIGetter int Builtin();
        @FFIGetter int C11NoReturn();
        @FFIGetter int CFAuditedTransfer();
        @FFIGetter int CFGuard();
        @FFIGetter int CFICanonicalJumpTable();
        @FFIGetter int CFReturnsNotRetained();
        @FFIGetter int CFReturnsRetained();
        @FFIGetter int CFUnknownTransfer();
        @FFIGetter int CPUDispatch();
        @FFIGetter int CPUSpecific();
        @FFIGetter int CUDAConstant();
        @FFIGetter int CUDADevice();
        @FFIGetter int CUDADeviceBuiltinSurfaceType();
        @FFIGetter int CUDADeviceBuiltinTextureType();
        @FFIGetter int CUDAGlobal();
        @FFIGetter int CUDAHost();
        @FFIGetter int CUDAInvalidTarget();
        @FFIGetter int CUDALaunchBounds();
        @FFIGetter int CUDAShared();
        @FFIGetter int CXX11NoReturn();
        @FFIGetter int CallableWhen();
        @FFIGetter int Callback();
        @FFIGetter int Capability();
        @FFIGetter int CapturedRecord();
        @FFIGetter int Cleanup();
        @FFIGetter int CmseNSEntry();
        @FFIGetter int CodeSeg();
        @FFIGetter int Cold();
        @FFIGetter int Common();
        @FFIGetter int Const();
        @FFIGetter int ConstInit();
        @FFIGetter int Constructor();
        @FFIGetter int Consumable();
        @FFIGetter int ConsumableAutoCast();
        @FFIGetter int ConsumableSetOnRead();
        @FFIGetter int Convergent();
        @FFIGetter int DLLExport();
        @FFIGetter int DLLExportStaticLocal();
        @FFIGetter int DLLImport();
        @FFIGetter int DLLImportStaticLocal();
        @FFIGetter int Deprecated();
        @FFIGetter int Destructor();
        @FFIGetter int DiagnoseIf();
        @FFIGetter int DisableTailCalls();
        @FFIGetter int EmptyBases();
        @FFIGetter int EnableIf();
        @FFIGetter int EnumExtensibility();
        @FFIGetter int ExcludeFromExplicitInstantiation();
        @FFIGetter int ExclusiveTrylockFunction();
        @FFIGetter int ExternalSourceSymbol();
        @FFIGetter int Final();
        @FFIGetter int FlagEnum();
        @FFIGetter int Flatten();
        @FFIGetter int Format();
        @FFIGetter int FormatArg();
        @FFIGetter int GNUInline();
        @FFIGetter int GuardedBy();
        @FFIGetter int GuardedVar();
        @FFIGetter int Hot();
        @FFIGetter int IBAction();
        @FFIGetter int IBOutlet();
        @FFIGetter int IBOutletCollection();
        @FFIGetter int InitPriority();
        @FFIGetter int InternalLinkage();
        @FFIGetter int LTOVisibilityPublic();
        @FFIGetter int LayoutVersion();
        @FFIGetter int LockReturned();
        @FFIGetter int LocksExcluded();
        @FFIGetter int MIGServerRoutine();
        @FFIGetter int MSAllocator();
        @FFIGetter int MSInheritance();
        @FFIGetter int MSNoVTable();
        @FFIGetter int MSP430Interrupt();
        @FFIGetter int MSStruct();
        @FFIGetter int MSVtorDisp();
        @FFIGetter int MaxFieldAlignment();
        @FFIGetter int MayAlias();
        @FFIGetter int MicroMips();
        @FFIGetter int MinSize();
        @FFIGetter int MinVectorWidth();
        @FFIGetter int Mips16();
        @FFIGetter int MipsInterrupt();
        @FFIGetter int MipsLongCall();
        @FFIGetter int MipsShortCall();
        @FFIGetter int NSConsumesSelf();
        @FFIGetter int NSReturnsAutoreleased();
        @FFIGetter int NSReturnsNotRetained();
        @FFIGetter int Naked();
        @FFIGetter int NoAlias();
        @FFIGetter int NoCommon();
        @FFIGetter int NoDebug();
        @FFIGetter int NoDestroy();
        @FFIGetter int NoDuplicate();
        @FFIGetter int NoInline();
        @FFIGetter int NoInstrumentFunction();
        @FFIGetter int NoMicroMips();
        @FFIGetter int NoMips16();
        @FFIGetter int NoReturn();
        @FFIGetter int NoSanitize();
        @FFIGetter int NoSpeculativeLoadHardening();
        @FFIGetter int NoSplitStack();
        @FFIGetter int NoStackProtector();
        @FFIGetter int NoThreadSafetyAnalysis();
        @FFIGetter int NoThrow();
        @FFIGetter int NoUniqueAddress();
        @FFIGetter int NotTailCalled();
        @FFIGetter int OMPAllocateDecl();
        @FFIGetter int OMPCaptureNoInit();
        @FFIGetter int OMPDeclareTargetDecl();
        @FFIGetter int OMPDeclareVariant();
        @FFIGetter int OMPThreadPrivateDecl();
        @FFIGetter int OSConsumesThis();
        @FFIGetter int OSReturnsNotRetained();
        @FFIGetter int OSReturnsRetained();
        @FFIGetter int OSReturnsRetainedOnNonZero();
        @FFIGetter int OSReturnsRetainedOnZero();
        @FFIGetter int ObjCBridge();
        @FFIGetter int ObjCBridgeMutable();
        @FFIGetter int ObjCBridgeRelated();
        @FFIGetter int ObjCException();
        @FFIGetter int ObjCExplicitProtocolImpl();
        @FFIGetter int ObjCExternallyRetained();
        @FFIGetter int ObjCIndependentClass();
        @FFIGetter int ObjCMethodFamily();
        @FFIGetter int ObjCNSObject();
        @FFIGetter int ObjCPreciseLifetime();
        @FFIGetter int ObjCRequiresPropertyDefs();
        @FFIGetter int ObjCRequiresSuper();
        @FFIGetter int ObjCReturnsInnerPointer();
        @FFIGetter int ObjCRootClass();
        @FFIGetter int ObjCSubclassingRestricted();
        @FFIGetter int OpenCLIntelReqdSubGroupSize();
        @FFIGetter int OpenCLKernel();
        @FFIGetter int OpenCLUnrollHint();
        @FFIGetter int OptimizeNone();
        @FFIGetter int Override();
        @FFIGetter int Owner();
        @FFIGetter int Ownership();
        @FFIGetter int Packed();
        @FFIGetter int ParamTypestate();
        @FFIGetter int PatchableFunctionEntry();
        @FFIGetter int Pointer();
        @FFIGetter int PragmaClangBSSSection();
        @FFIGetter int PragmaClangDataSection();
        @FFIGetter int PragmaClangRelroSection();
        @FFIGetter int PragmaClangRodataSection();
        @FFIGetter int PragmaClangTextSection();
        @FFIGetter int PtGuardedBy();
        @FFIGetter int PtGuardedVar();
        @FFIGetter int Pure();
        @FFIGetter int RISCVInterrupt();
        @FFIGetter int Reinitializes();
        @FFIGetter int ReleaseCapability();
        @FFIGetter int ReqdWorkGroupSize();
        @FFIGetter int RequiresCapability();
        @FFIGetter int Restrict();
        @FFIGetter int ReturnTypestate();
        @FFIGetter int ReturnsNonNull();
        @FFIGetter int ReturnsTwice();
        @FFIGetter int SYCLKernel();
        @FFIGetter int ScopedLockable();
        @FFIGetter int Section();
        @FFIGetter int SelectAny();
        @FFIGetter int Sentinel();
        @FFIGetter int SetTypestate();
        @FFIGetter int SharedTrylockFunction();
        @FFIGetter int SpeculativeLoadHardening();
        @FFIGetter int TLSModel();
        @FFIGetter int Target();
        @FFIGetter int TestTypestate();
        @FFIGetter int TransparentUnion();
        @FFIGetter int TrivialABI();
        @FFIGetter int TryAcquireCapability();
        @FFIGetter int TypeTagForDatatype();
        @FFIGetter int TypeVisibility();
        @FFIGetter int Unavailable();
        @FFIGetter int Uninitialized();
        @FFIGetter int Unused();
        @FFIGetter int Used();
        @FFIGetter int Uuid();
        @FFIGetter int VecReturn();
        @FFIGetter int VecTypeHint();
        @FFIGetter int Visibility();
        @FFIGetter int WarnUnused();
        @FFIGetter int WarnUnusedResult();
        @FFIGetter int Weak();
        @FFIGetter int WeakImport();
        @FFIGetter int WeakRef();
        @FFIGetter int WebAssemblyExportName();
        @FFIGetter int WebAssemblyImportModule();
        @FFIGetter int WebAssemblyImportName();
        @FFIGetter int WorkGroupSizeHint();
        @FFIGetter int X86ForceAlignArgPointer();
        @FFIGetter int XRayInstrument();
        @FFIGetter int XRayLogArgs();
        @FFIGetter int AbiTag();
        @FFIGetter int Alias();
        @FFIGetter int AlignValue();
        @FFIGetter int IFunc();
        @FFIGetter int InitSeg();
        @FFIGetter int LoaderUninitialized();
        @FFIGetter int LoopHint();
        @FFIGetter int Mode();
        @FFIGetter int NoBuiltin();
        @FFIGetter int NoEscape();
        @FFIGetter int OMPCaptureKind();
        @FFIGetter int OMPDeclareSimdDecl();
        @FFIGetter int OMPReferencedVar();
        @FFIGetter int ObjCBoxable();
        @FFIGetter int ObjCClassStub();
        @FFIGetter int ObjCDesignatedInitializer();
        @FFIGetter int ObjCDirect();
        @FFIGetter int ObjCDirectMembers();
        @FFIGetter int ObjCNonLazyClass();
        @FFIGetter int ObjCRuntimeName();
        @FFIGetter int ObjCRuntimeVisible();
        @FFIGetter int OpenCLAccess();
        @FFIGetter int Overloadable();
        @FFIGetter int RenderScriptKernel();
        @FFIGetter int Thread();
        @FFIGetter int FirstAttr();
        @FFIGetter int LastAttr();
        @FFIGetter int FirstTypeAttr();
        @FFIGetter int LastTypeAttr();
        @FFIGetter int FirstStmtAttr();
        @FFIGetter int LastStmtAttr();
        @FFIGetter int FirstInheritableAttr();
        @FFIGetter int LastInheritableAttr();
        @FFIGetter int FirstDeclOrTypeAttr();
        @FFIGetter int LastDeclOrTypeAttr();
        @FFIGetter int FirstInheritableParamAttr();
        @FFIGetter int LastInheritableParamAttr();
        @FFIGetter int FirstParameterABIAttr();
        @FFIGetter int LastParameterABIAttr();
    }

    int value;
    AttrKind(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    static final CXXEnumMap<AttrKind> map = new CXXEnumMap<>(values());
    public static AttrKind get(int value) {
        return map.get(value);
    }
}
