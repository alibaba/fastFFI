package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@CXXHead("clang/AST/Type.h")
@FFITypeAlias("clang::BuiltinType")
public interface BuiltinType extends Type {
    static BuiltinType dyn_cast(Type type) {
        return TypeCasting.INSTANCE.dyn_cast(type, (BuiltinType) null);
    }
    @CXXValue StringRef getName(@CXXReference PrintingPolicy printingPolicy);

    @FFITypeAlias("clang::BuiltinType::Kind")
    @FFITypeRefiner("com.alibaba.fastffi.clang.BuiltinType.Kind.get")
    enum Kind implements CXXEnum {
        OCLImage1dRO(Library.INSTANCE.OCLImage1dRO()),
        OCLImage1dArrayRO(Library.INSTANCE.OCLImage1dArrayRO()),
        OCLImage1dBufferRO(Library.INSTANCE.OCLImage1dBufferRO()),
        OCLImage2dRO(Library.INSTANCE.OCLImage2dRO()),
        OCLImage2dArrayRO(Library.INSTANCE.OCLImage2dArrayRO()),
        OCLImage2dDepthRO(Library.INSTANCE.OCLImage2dDepthRO()),
        OCLImage2dArrayDepthRO(Library.INSTANCE.OCLImage2dArrayDepthRO()),
        OCLImage2dMSAARO(Library.INSTANCE.OCLImage2dMSAARO()),
        OCLImage2dArrayMSAARO(Library.INSTANCE.OCLImage2dArrayMSAARO()),
        OCLImage2dMSAADepthRO(Library.INSTANCE.OCLImage2dMSAADepthRO()),
        OCLImage2dArrayMSAADepthRO(Library.INSTANCE.OCLImage2dArrayMSAADepthRO()),
        OCLImage3dRO(Library.INSTANCE.OCLImage3dRO()),
        OCLImage1dWO(Library.INSTANCE.OCLImage1dWO()),
        OCLImage1dArrayWO(Library.INSTANCE.OCLImage1dArrayWO()),
        OCLImage1dBufferWO(Library.INSTANCE.OCLImage1dBufferWO()),
        OCLImage2dWO(Library.INSTANCE.OCLImage2dWO()),
        OCLImage2dArrayWO(Library.INSTANCE.OCLImage2dArrayWO()),
        OCLImage2dDepthWO(Library.INSTANCE.OCLImage2dDepthWO()),
        OCLImage2dArrayDepthWO(Library.INSTANCE.OCLImage2dArrayDepthWO()),
        OCLImage2dMSAAWO(Library.INSTANCE.OCLImage2dMSAAWO()),
        OCLImage2dArrayMSAAWO(Library.INSTANCE.OCLImage2dArrayMSAAWO()),
        OCLImage2dMSAADepthWO(Library.INSTANCE.OCLImage2dMSAADepthWO()),
        OCLImage2dArrayMSAADepthWO(Library.INSTANCE.OCLImage2dArrayMSAADepthWO()),
        OCLImage3dWO(Library.INSTANCE.OCLImage3dWO()),
        OCLImage1dRW(Library.INSTANCE.OCLImage1dRW()),
        OCLImage1dArrayRW(Library.INSTANCE.OCLImage1dArrayRW()),
        OCLImage1dBufferRW(Library.INSTANCE.OCLImage1dBufferRW()),
        OCLImage2dRW(Library.INSTANCE.OCLImage2dRW()),
        OCLImage2dArrayRW(Library.INSTANCE.OCLImage2dArrayRW()),
        OCLImage2dDepthRW(Library.INSTANCE.OCLImage2dDepthRW()),
        OCLImage2dArrayDepthRW(Library.INSTANCE.OCLImage2dArrayDepthRW()),
        OCLImage2dMSAARW(Library.INSTANCE.OCLImage2dMSAARW()),
        OCLImage2dArrayMSAARW(Library.INSTANCE.OCLImage2dArrayMSAARW()),
        OCLImage2dMSAADepthRW(Library.INSTANCE.OCLImage2dMSAADepthRW()),
        OCLImage2dArrayMSAADepthRW(Library.INSTANCE.OCLImage2dArrayMSAADepthRW()),
        OCLImage3dRW(Library.INSTANCE.OCLImage3dRW()),
        OCLIntelSubgroupAVCMcePayload(Library.INSTANCE.OCLIntelSubgroupAVCMcePayload()),
        OCLIntelSubgroupAVCImePayload(Library.INSTANCE.OCLIntelSubgroupAVCImePayload()),
        OCLIntelSubgroupAVCRefPayload(Library.INSTANCE.OCLIntelSubgroupAVCRefPayload()),
        OCLIntelSubgroupAVCSicPayload(Library.INSTANCE.OCLIntelSubgroupAVCSicPayload()),
        OCLIntelSubgroupAVCMceResult(Library.INSTANCE.OCLIntelSubgroupAVCMceResult()),
        OCLIntelSubgroupAVCImeResult(Library.INSTANCE.OCLIntelSubgroupAVCImeResult()),
        OCLIntelSubgroupAVCRefResult(Library.INSTANCE.OCLIntelSubgroupAVCRefResult()),
        OCLIntelSubgroupAVCSicResult(Library.INSTANCE.OCLIntelSubgroupAVCSicResult()),
        OCLIntelSubgroupAVCImeResultSingleRefStreamout(Library.INSTANCE.OCLIntelSubgroupAVCImeResultSingleRefStreamout()),
        OCLIntelSubgroupAVCImeResultDualRefStreamout(Library.INSTANCE.OCLIntelSubgroupAVCImeResultDualRefStreamout()),
        OCLIntelSubgroupAVCImeSingleRefStreamin(Library.INSTANCE.OCLIntelSubgroupAVCImeSingleRefStreamin()),
        OCLIntelSubgroupAVCImeDualRefStreamin(Library.INSTANCE.OCLIntelSubgroupAVCImeDualRefStreamin()),
        SveInt8(Library.INSTANCE.SveInt8()),
        SveInt16(Library.INSTANCE.SveInt16()),
        SveInt32(Library.INSTANCE.SveInt32()),
        SveInt64(Library.INSTANCE.SveInt64()),
        SveUint8(Library.INSTANCE.SveUint8()),
        SveUint16(Library.INSTANCE.SveUint16()),
        SveUint32(Library.INSTANCE.SveUint32()),
        SveUint64(Library.INSTANCE.SveUint64()),
        SveFloat16(Library.INSTANCE.SveFloat16()),
        SveFloat32(Library.INSTANCE.SveFloat32()),
        SveFloat64(Library.INSTANCE.SveFloat64()),
        SveBFloat16(Library.INSTANCE.SveBFloat16()),
        SveInt8x2(Library.INSTANCE.SveInt8x2()),
        SveInt16x2(Library.INSTANCE.SveInt16x2()),
        SveInt32x2(Library.INSTANCE.SveInt32x2()),
        SveInt64x2(Library.INSTANCE.SveInt64x2()),
        SveUint8x2(Library.INSTANCE.SveUint8x2()),
        SveUint16x2(Library.INSTANCE.SveUint16x2()),
        SveUint32x2(Library.INSTANCE.SveUint32x2()),
        SveUint64x2(Library.INSTANCE.SveUint64x2()),
        SveFloat16x2(Library.INSTANCE.SveFloat16x2()),
        SveFloat32x2(Library.INSTANCE.SveFloat32x2()),
        SveFloat64x2(Library.INSTANCE.SveFloat64x2()),
        SveBFloat16x2(Library.INSTANCE.SveBFloat16x2()),
        SveInt8x3(Library.INSTANCE.SveInt8x3()),
        SveInt16x3(Library.INSTANCE.SveInt16x3()),
        SveInt32x3(Library.INSTANCE.SveInt32x3()),
        SveInt64x3(Library.INSTANCE.SveInt64x3()),
        SveUint8x3(Library.INSTANCE.SveUint8x3()),
        SveUint16x3(Library.INSTANCE.SveUint16x3()),
        SveUint32x3(Library.INSTANCE.SveUint32x3()),
        SveUint64x3(Library.INSTANCE.SveUint64x3()),
        SveFloat16x3(Library.INSTANCE.SveFloat16x3()),
        SveFloat32x3(Library.INSTANCE.SveFloat32x3()),
        SveFloat64x3(Library.INSTANCE.SveFloat64x3()),
        SveBFloat16x3(Library.INSTANCE.SveBFloat16x3()),
        SveInt8x4(Library.INSTANCE.SveInt8x4()),
        SveInt16x4(Library.INSTANCE.SveInt16x4()),
        SveInt32x4(Library.INSTANCE.SveInt32x4()),
        SveInt64x4(Library.INSTANCE.SveInt64x4()),
        SveUint8x4(Library.INSTANCE.SveUint8x4()),
        SveUint16x4(Library.INSTANCE.SveUint16x4()),
        SveUint32x4(Library.INSTANCE.SveUint32x4()),
        SveUint64x4(Library.INSTANCE.SveUint64x4()),
        SveFloat16x4(Library.INSTANCE.SveFloat16x4()),
        SveFloat32x4(Library.INSTANCE.SveFloat32x4()),
        SveFloat64x4(Library.INSTANCE.SveFloat64x4()),
        SveBFloat16x4(Library.INSTANCE.SveBFloat16x4()),
        SveBool(Library.INSTANCE.SveBool()),
        Void(Library.INSTANCE.Void()),
        Bool(Library.INSTANCE.Bool()),
        Char_U(Library.INSTANCE.Char_U()),
        UChar(Library.INSTANCE.UChar()),
        WChar_U(Library.INSTANCE.WChar_U()),
        Char8(Library.INSTANCE.Char8()),
        Char16(Library.INSTANCE.Char16()),
        Char32(Library.INSTANCE.Char32()),
        UShort(Library.INSTANCE.UShort()),
        UInt(Library.INSTANCE.UInt()),
        ULong(Library.INSTANCE.ULong()),
        ULongLong(Library.INSTANCE.ULongLong()),
        UInt128(Library.INSTANCE.UInt128()),
        Char_S(Library.INSTANCE.Char_S()),
        SChar(Library.INSTANCE.SChar()),
        WChar_S(Library.INSTANCE.WChar_S()),
        Short(Library.INSTANCE.Short()),
        Int(Library.INSTANCE.Int()),
        Long(Library.INSTANCE.Long()),
        LongLong(Library.INSTANCE.LongLong()),
        Int128(Library.INSTANCE.Int128()),
        ShortAccum(Library.INSTANCE.ShortAccum()),
        Accum(Library.INSTANCE.Accum()),
        LongAccum(Library.INSTANCE.LongAccum()),
        UShortAccum(Library.INSTANCE.UShortAccum()),
        UAccum(Library.INSTANCE.UAccum()),
        ULongAccum(Library.INSTANCE.ULongAccum()),
        ShortFract(Library.INSTANCE.ShortFract()),
        Fract(Library.INSTANCE.Fract()),
        LongFract(Library.INSTANCE.LongFract()),
        UShortFract(Library.INSTANCE.UShortFract()),
        UFract(Library.INSTANCE.UFract()),
        ULongFract(Library.INSTANCE.ULongFract()),
        SatShortAccum(Library.INSTANCE.SatShortAccum()),
        SatAccum(Library.INSTANCE.SatAccum()),
        SatLongAccum(Library.INSTANCE.SatLongAccum()),
        SatUShortAccum(Library.INSTANCE.SatUShortAccum()),
        SatUAccum(Library.INSTANCE.SatUAccum()),
        SatULongAccum(Library.INSTANCE.SatULongAccum()),
        SatShortFract(Library.INSTANCE.SatShortFract()),
        SatFract(Library.INSTANCE.SatFract()),
        SatLongFract(Library.INSTANCE.SatLongFract()),
        SatUShortFract(Library.INSTANCE.SatUShortFract()),
        SatUFract(Library.INSTANCE.SatUFract()),
        SatULongFract(Library.INSTANCE.SatULongFract()),
        Half(Library.INSTANCE.Half()),
        Float(Library.INSTANCE.Float()),
        Double(Library.INSTANCE.Double()),
        LongDouble(Library.INSTANCE.LongDouble()),
        Float16(Library.INSTANCE.Float16()),
        BFloat16(Library.INSTANCE.BFloat16()),
        Float128(Library.INSTANCE.Float128()),
        NullPtr(Library.INSTANCE.NullPtr()),
        ObjCId(Library.INSTANCE.ObjCId()),
        ObjCClass(Library.INSTANCE.ObjCClass()),
        ObjCSel(Library.INSTANCE.ObjCSel()),
        OCLSampler(Library.INSTANCE.OCLSampler()),
        OCLEvent(Library.INSTANCE.OCLEvent()),
        OCLClkEvent(Library.INSTANCE.OCLClkEvent()),
        OCLQueue(Library.INSTANCE.OCLQueue()),
        OCLReserveID(Library.INSTANCE.OCLReserveID()),
        Dependent(Library.INSTANCE.Dependent()),
        Overload(Library.INSTANCE.Overload()),
        BoundMember(Library.INSTANCE.BoundMember()),
        PseudoObject(Library.INSTANCE.PseudoObject()),
        UnknownAny(Library.INSTANCE.UnknownAny()),
        BuiltinFn(Library.INSTANCE.BuiltinFn()),
        ARCUnbridgedCast(Library.INSTANCE.ARCUnbridgedCast()),
        IncompleteMatrixIdx(Library.INSTANCE.IncompleteMatrixIdx()),
        OMPArraySection(Library.INSTANCE.OMPArraySection()),
        OMPArrayShaping(Library.INSTANCE.OMPArrayShaping()),
        OMPIterator(Library.INSTANCE.OMPIterator())
        ;

        @Override
        public int getValue() {
            return value;
        }

        @FFIGen
        @CXXHead("clang/AST/Type.h")
        @FFILibrary(namespace = "clang::BuiltinType::Kind")
        interface Library {
            Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
            @FFIGetter int OCLImage1dRO();
            @FFIGetter int OCLImage1dArrayRO();
            @FFIGetter int OCLImage1dBufferRO();
            @FFIGetter int OCLImage2dRO();
            @FFIGetter int OCLImage2dArrayRO();
            @FFIGetter int OCLImage2dDepthRO();
            @FFIGetter int OCLImage2dArrayDepthRO();
            @FFIGetter int OCLImage2dMSAARO();
            @FFIGetter int OCLImage2dArrayMSAARO();
            @FFIGetter int OCLImage2dMSAADepthRO();
            @FFIGetter int OCLImage2dArrayMSAADepthRO();
            @FFIGetter int OCLImage3dRO();
            @FFIGetter int OCLImage1dWO();
            @FFIGetter int OCLImage1dArrayWO();
            @FFIGetter int OCLImage1dBufferWO();
            @FFIGetter int OCLImage2dWO();
            @FFIGetter int OCLImage2dArrayWO();
            @FFIGetter int OCLImage2dDepthWO();
            @FFIGetter int OCLImage2dArrayDepthWO();
            @FFIGetter int OCLImage2dMSAAWO();
            @FFIGetter int OCLImage2dArrayMSAAWO();
            @FFIGetter int OCLImage2dMSAADepthWO();
            @FFIGetter int OCLImage2dArrayMSAADepthWO();
            @FFIGetter int OCLImage3dWO();
            @FFIGetter int OCLImage1dRW();
            @FFIGetter int OCLImage1dArrayRW();
            @FFIGetter int OCLImage1dBufferRW();
            @FFIGetter int OCLImage2dRW();
            @FFIGetter int OCLImage2dArrayRW();
            @FFIGetter int OCLImage2dDepthRW();
            @FFIGetter int OCLImage2dArrayDepthRW();
            @FFIGetter int OCLImage2dMSAARW();
            @FFIGetter int OCLImage2dArrayMSAARW();
            @FFIGetter int OCLImage2dMSAADepthRW();
            @FFIGetter int OCLImage2dArrayMSAADepthRW();
            @FFIGetter int OCLImage3dRW();
            @FFIGetter int OCLIntelSubgroupAVCMcePayload();
            @FFIGetter int OCLIntelSubgroupAVCImePayload();
            @FFIGetter int OCLIntelSubgroupAVCRefPayload();
            @FFIGetter int OCLIntelSubgroupAVCSicPayload();
            @FFIGetter int OCLIntelSubgroupAVCMceResult();
            @FFIGetter int OCLIntelSubgroupAVCImeResult();
            @FFIGetter int OCLIntelSubgroupAVCRefResult();
            @FFIGetter int OCLIntelSubgroupAVCSicResult();
            @FFIGetter int OCLIntelSubgroupAVCImeResultSingleRefStreamout();
            @FFIGetter int OCLIntelSubgroupAVCImeResultDualRefStreamout();
            @FFIGetter int OCLIntelSubgroupAVCImeSingleRefStreamin();
            @FFIGetter int OCLIntelSubgroupAVCImeDualRefStreamin();
            @FFIGetter int SveInt8();
            @FFIGetter int SveInt16();
            @FFIGetter int SveInt32();
            @FFIGetter int SveInt64();
            @FFIGetter int SveUint8();
            @FFIGetter int SveUint16();
            @FFIGetter int SveUint32();
            @FFIGetter int SveUint64();
            @FFIGetter int SveFloat16();
            @FFIGetter int SveFloat32();
            @FFIGetter int SveFloat64();
            @FFIGetter int SveBFloat16();
            @FFIGetter int SveInt8x2();
            @FFIGetter int SveInt16x2();
            @FFIGetter int SveInt32x2();
            @FFIGetter int SveInt64x2();
            @FFIGetter int SveUint8x2();
            @FFIGetter int SveUint16x2();
            @FFIGetter int SveUint32x2();
            @FFIGetter int SveUint64x2();
            @FFIGetter int SveFloat16x2();
            @FFIGetter int SveFloat32x2();
            @FFIGetter int SveFloat64x2();
            @FFIGetter int SveBFloat16x2();
            @FFIGetter int SveInt8x3();
            @FFIGetter int SveInt16x3();
            @FFIGetter int SveInt32x3();
            @FFIGetter int SveInt64x3();
            @FFIGetter int SveUint8x3();
            @FFIGetter int SveUint16x3();
            @FFIGetter int SveUint32x3();
            @FFIGetter int SveUint64x3();
            @FFIGetter int SveFloat16x3();
            @FFIGetter int SveFloat32x3();
            @FFIGetter int SveFloat64x3();
            @FFIGetter int SveBFloat16x3();
            @FFIGetter int SveInt8x4();
            @FFIGetter int SveInt16x4();
            @FFIGetter int SveInt32x4();
            @FFIGetter int SveInt64x4();
            @FFIGetter int SveUint8x4();
            @FFIGetter int SveUint16x4();
            @FFIGetter int SveUint32x4();
            @FFIGetter int SveUint64x4();
            @FFIGetter int SveFloat16x4();
            @FFIGetter int SveFloat32x4();
            @FFIGetter int SveFloat64x4();
            @FFIGetter int SveBFloat16x4();
            @FFIGetter int SveBool();
            @FFIGetter int Void();
            @FFIGetter int Bool();
            @FFIGetter int Char_U();
            @FFIGetter int UChar();
            @FFIGetter int WChar_U();
            @FFIGetter int Char8();
            @FFIGetter int Char16();
            @FFIGetter int Char32();
            @FFIGetter int UShort();
            @FFIGetter int UInt();
            @FFIGetter int ULong();
            @FFIGetter int ULongLong();
            @FFIGetter int UInt128();
            @FFIGetter int Char_S();
            @FFIGetter int SChar();
            @FFIGetter int WChar_S();
            @FFIGetter int Short();
            @FFIGetter int Int();
            @FFIGetter int Long();
            @FFIGetter int LongLong();
            @FFIGetter int Int128();
            @FFIGetter int ShortAccum();
            @FFIGetter int Accum();
            @FFIGetter int LongAccum();
            @FFIGetter int UShortAccum();
            @FFIGetter int UAccum();
            @FFIGetter int ULongAccum();
            @FFIGetter int ShortFract();
            @FFIGetter int Fract();
            @FFIGetter int LongFract();
            @FFIGetter int UShortFract();
            @FFIGetter int UFract();
            @FFIGetter int ULongFract();
            @FFIGetter int SatShortAccum();
            @FFIGetter int SatAccum();
            @FFIGetter int SatLongAccum();
            @FFIGetter int SatUShortAccum();
            @FFIGetter int SatUAccum();
            @FFIGetter int SatULongAccum();
            @FFIGetter int SatShortFract();
            @FFIGetter int SatFract();
            @FFIGetter int SatLongFract();
            @FFIGetter int SatUShortFract();
            @FFIGetter int SatUFract();
            @FFIGetter int SatULongFract();
            @FFIGetter int Half();
            @FFIGetter int Float();
            @FFIGetter int Double();
            @FFIGetter int LongDouble();
            @FFIGetter int Float16();
            @FFIGetter int BFloat16();
            @FFIGetter int Float128();
            @FFIGetter int NullPtr();
            @FFIGetter int ObjCId();
            @FFIGetter int ObjCClass();
            @FFIGetter int ObjCSel();
            @FFIGetter int OCLSampler();
            @FFIGetter int OCLEvent();
            @FFIGetter int OCLClkEvent();
            @FFIGetter int OCLQueue();
            @FFIGetter int OCLReserveID();
            @FFIGetter int Dependent();
            @FFIGetter int Overload();
            @FFIGetter int BoundMember();
            @FFIGetter int PseudoObject();
            @FFIGetter int UnknownAny();
            @FFIGetter int BuiltinFn();
            @FFIGetter int ARCUnbridgedCast();
            @FFIGetter int IncompleteMatrixIdx();
            @FFIGetter int OMPArraySection();
            @FFIGetter int OMPArrayShaping();
            @FFIGetter int OMPIterator();
        }

        int value;

        Kind(int value) {
            this.value = value;
        }

        static CXXEnumMap<Kind> map = new CXXEnumMap<>(values());
        public static Kind get(int value) {
            return map.get(value);
        }
    }

    Kind getKind();
    boolean isUnsignedInteger();
    boolean isSignedInteger();
    boolean isFloatingPoint();
}
