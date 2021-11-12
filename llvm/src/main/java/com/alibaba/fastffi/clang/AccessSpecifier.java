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

@FFITypeAlias("clang::AccessSpecifier")
@FFITypeRefiner("AccessSpecifier.get")
public enum AccessSpecifier implements CXXEnum {
    AS_public(Library.INSTANCE.AS_public()),
    AS_protected(Library.INSTANCE.AS_protected()),
    AS_private(Library.INSTANCE.AS_private()),
    AS_none(Library.INSTANCE.AS_none())
    ;

    int value;

    @FFIGen
    @FFILibrary(namespace = "clang::AccessSpecifier")
    @CXXHead("clang/Basic/Specifiers.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        @FFIGetter int AS_public();
        @FFIGetter int AS_protected();
        @FFIGetter int AS_private();
        @FFIGetter int AS_none();
    }

    public final boolean isPublic() {
        return this == AS_public;
    }

    AccessSpecifier(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    static CXXEnumMap<AccessSpecifier> map = new CXXEnumMap<>(values());
    public static AccessSpecifier get(int value) {
        return map.get(value);
    }
}
