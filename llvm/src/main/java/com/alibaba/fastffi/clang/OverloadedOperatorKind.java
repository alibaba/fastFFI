package com.alibaba.fastffi.clang;

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;
import com.alibaba.fastffi.llvm.StringRef;

@FFITypeAlias("clang::OverloadedOperatorKind")
@FFITypeRefiner("com.alibaba.fastffi.clang.OverloadedOperatorKind.get")
public enum OverloadedOperatorKind implements CXXEnum {
    OO_None(Library.INSTANCE.OO_None()),

    OO_New(Library.INSTANCE.OO_New()),

    OO_Delete(Library.INSTANCE.OO_Delete()),

    OO_Array_New(Library.INSTANCE.OO_Array_New()),

    OO_Array_Delete(Library.INSTANCE.OO_Array_Delete()),

    OO_Plus(Library.INSTANCE.OO_Plus()),

    OO_Minus(Library.INSTANCE.OO_Minus()),

    OO_Star(Library.INSTANCE.OO_Star()),

    OO_Slash(Library.INSTANCE.OO_Slash()),

    OO_Percent(Library.INSTANCE.OO_Percent()),

    OO_Caret(Library.INSTANCE.OO_Caret()),

    OO_Amp(Library.INSTANCE.OO_Amp()),

    OO_Pipe(Library.INSTANCE.OO_Pipe()),

    OO_Tilde(Library.INSTANCE.OO_Tilde()),

    OO_Exclaim(Library.INSTANCE.OO_Exclaim()),

    OO_Equal(Library.INSTANCE.OO_Equal()),

    OO_Less(Library.INSTANCE.OO_Less()),

    OO_Greater(Library.INSTANCE.OO_Greater()),

    OO_PlusEqual(Library.INSTANCE.OO_PlusEqual()),

    OO_MinusEqual(Library.INSTANCE.OO_MinusEqual()),

    OO_StarEqual(Library.INSTANCE.OO_StarEqual()),

    OO_SlashEqual(Library.INSTANCE.OO_SlashEqual()),

    OO_PercentEqual(Library.INSTANCE.OO_PercentEqual()),

    OO_CaretEqual(Library.INSTANCE.OO_CaretEqual()),

    OO_AmpEqual(Library.INSTANCE.OO_AmpEqual()),

    OO_PipeEqual(Library.INSTANCE.OO_PipeEqual()),

    OO_LessLess(Library.INSTANCE.OO_LessLess()),

    OO_GreaterGreater(Library.INSTANCE.OO_GreaterGreater()),

    OO_LessLessEqual(Library.INSTANCE.OO_LessLessEqual()),

    OO_GreaterGreaterEqual(Library.INSTANCE.OO_GreaterGreaterEqual()),

    OO_EqualEqual(Library.INSTANCE.OO_EqualEqual()),

    OO_ExclaimEqual(Library.INSTANCE.OO_ExclaimEqual()),

    OO_LessEqual(Library.INSTANCE.OO_LessEqual()),

    OO_GreaterEqual(Library.INSTANCE.OO_GreaterEqual()),

    OO_Spaceship(Library.INSTANCE.OO_Spaceship()),

    OO_AmpAmp(Library.INSTANCE.OO_AmpAmp()),

    OO_PipePipe(Library.INSTANCE.OO_PipePipe()),

    OO_PlusPlus(Library.INSTANCE.OO_PlusPlus()),

    OO_MinusMinus(Library.INSTANCE.OO_MinusMinus()),

    OO_Comma(Library.INSTANCE.OO_Comma()),

    OO_ArrowStar(Library.INSTANCE.OO_ArrowStar()),

    OO_Arrow(Library.INSTANCE.OO_Arrow()),

    OO_Call(Library.INSTANCE.OO_Call()),

    OO_Subscript(Library.INSTANCE.OO_Subscript()),

    OO_Conditional(Library.INSTANCE.OO_Conditional()),

    OO_Coawait(Library.INSTANCE.OO_Coawait()),

    NUM_OVERLOADED_OPERATORS(Library.INSTANCE.NUM_OVERLOADED_OPERATORS());

    private static final CXXEnumMap<OverloadedOperatorKind> $map = new CXXEnumMap<>(values());

    int $value;

    OverloadedOperatorKind(int value) {
        $value = value;
    }

    public int getValue() {
        return $value;
    }

    public static OverloadedOperatorKind get(int value) {
        return $map.get(value);
    }

    @FFIGen
    @FFILibrary(
            value = "clang::OverloadedOperatorKind",
            namespace = "clang::OverloadedOperatorKind"
    )
    @CXXHead("clang/Basic/OperatorKinds.h")
    @CXXHead("llvm/ADT/StringRef.h")
    public interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);

        @FFIGetter
        int OO_None();

        @FFIGetter
        int OO_New();

        @FFIGetter
        int OO_Delete();

        @FFIGetter
        int OO_Array_New();

        @FFIGetter
        int OO_Array_Delete();

        @FFIGetter
        int OO_Plus();

        @FFIGetter
        int OO_Minus();

        @FFIGetter
        int OO_Star();

        @FFIGetter
        int OO_Slash();

        @FFIGetter
        int OO_Percent();

        @FFIGetter
        int OO_Caret();

        @FFIGetter
        int OO_Amp();

        @FFIGetter
        int OO_Pipe();

        @FFIGetter
        int OO_Tilde();

        @FFIGetter
        int OO_Exclaim();

        @FFIGetter
        int OO_Equal();

        @FFIGetter
        int OO_Less();

        @FFIGetter
        int OO_Greater();

        @FFIGetter
        int OO_PlusEqual();

        @FFIGetter
        int OO_MinusEqual();

        @FFIGetter
        int OO_StarEqual();

        @FFIGetter
        int OO_SlashEqual();

        @FFIGetter
        int OO_PercentEqual();

        @FFIGetter
        int OO_CaretEqual();

        @FFIGetter
        int OO_AmpEqual();

        @FFIGetter
        int OO_PipeEqual();

        @FFIGetter
        int OO_LessLess();

        @FFIGetter
        int OO_GreaterGreater();

        @FFIGetter
        int OO_LessLessEqual();

        @FFIGetter
        int OO_GreaterGreaterEqual();

        @FFIGetter
        int OO_EqualEqual();

        @FFIGetter
        int OO_ExclaimEqual();

        @FFIGetter
        int OO_LessEqual();

        @FFIGetter
        int OO_GreaterEqual();

        @FFIGetter
        int OO_Spaceship();

        @FFIGetter
        int OO_AmpAmp();

        @FFIGetter
        int OO_PipePipe();

        @FFIGetter
        int OO_PlusPlus();

        @FFIGetter
        int OO_MinusMinus();

        @FFIGetter
        int OO_Comma();

        @FFIGetter
        int OO_ArrowStar();

        @FFIGetter
        int OO_Arrow();

        @FFIGetter
        int OO_Call();

        @FFIGetter
        int OO_Subscript();

        @FFIGetter
        int OO_Conditional();

        @FFIGetter
        int OO_Coawait();

        @FFIGetter
        int NUM_OVERLOADED_OPERATORS();

        @FFIExpr("clang::getOperatorSpelling({1})")
        @CXXValue StringRef getOperatorSpelling(@CXXValue OverloadedOperatorKind kind);
    }

    public String getOperatorSpelling() {
        return Library.INSTANCE.getOperatorSpelling(this).toJavaString();
    }
}