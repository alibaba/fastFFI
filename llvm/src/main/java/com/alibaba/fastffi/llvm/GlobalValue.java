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

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;

@FFIGen
@CXXHead("llvm/IR/GlobalValue.h")
@FFITypeAlias("llvm::GlobalValue")
public interface GlobalValue extends Constant {

    static boolean isa(Value value) {
        return ValueCasting.INSTANCE.isa(value, (GlobalValue) null);
    }

    static GlobalValue cast(Value value) {
        return ValueCasting.INSTANCE.cast(value, (GlobalValue) null);
    }

    static GlobalValue dyn_cast(Value value) {
        return ValueCasting.INSTANCE.dyn_cast(value, (GlobalValue) null);
    }

    boolean isDeclaration();
    boolean hasExactDefinition();

    PointerType getType();
    Type getValueType();

    LinkageTypes getLinkage();
    VisibilityTypes getVisibility();

    boolean hasExternalLinkage();
    boolean hasDefaultVisibility();

    boolean hasAtLeastLocalUnnamedAddr();

    default String toJavaString() {
        return String.format("%s@%s@%s@%s@%s", getClass().getName(), Long.toHexString(getAddress()), getName(), getLinkage(), getVisibility());
    }

    @FFITypeAlias("llvm::GlobalValue::LinkageTypes")
    @FFITypeRefiner("com.alibaba.fastffi.llvm.GlobalValue.LinkageTypes.get")
    enum LinkageTypes implements CXXEnum {
        ExternalLinkage(Library.INSTANCE.ExternalLinkage()),///< Externally visible function
        AvailableExternallyLinkage(Library.INSTANCE.AvailableExternallyLinkage()), ///< Available for inspection, not emission.
        LinkOnceAnyLinkage(Library.INSTANCE.LinkOnceAnyLinkage()), ///< Keep one copy of function when linking (inline)
        LinkOnceODRLinkage(Library.INSTANCE.LinkOnceODRLinkage()), ///< Same, but only replaced by something equivalent.
        WeakAnyLinkage(Library.INSTANCE.WeakAnyLinkage()),     ///< Keep one copy of named function when linking (weak)
        WeakODRLinkage(Library.INSTANCE.WeakODRLinkage()),     ///< Same, but only replaced by something equivalent.
        AppendingLinkage(Library.INSTANCE.AppendingLinkage()),   ///< Special purpose, only applies to global arrays
        InternalLinkage(Library.INSTANCE.InternalLinkage()),    ///< Rename collisions when linking (static functions).
        PrivateLinkage(Library.INSTANCE.PrivateLinkage()),     ///< Like Internal, but omit from symbol table.
        ExternalWeakLinkage(Library.INSTANCE.ExternalWeakLinkage()),///< ExternalWeak linkage description.
        CommonLinkage(Library.INSTANCE.CommonLinkage())       ///< Tentative definitions.
        ;

        int value;
        LinkageTypes(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }

        public static CXXEnumMap<LinkageTypes> map = new CXXEnumMap<>(values());
        public static LinkageTypes get(int value) {
            return map.get(value);
        }
    };

    /// An enumeration for the kinds of visibility of global values.
    @FFITypeAlias("llvm::GlobalValue::VisibilityTypes")
    @FFITypeRefiner("com.alibaba.fastffi.llvm.GlobalValue.VisibilityTypes.get")
    enum VisibilityTypes implements CXXEnum {
        DefaultVisibility(Library.INSTANCE.DefaultVisibility()),  ///< The GV is visible
        HiddenVisibility(Library.INSTANCE.HiddenVisibility()),       ///< The GV is hidden
        ProtectedVisibility(Library.INSTANCE.ProtectedVisibility());     ///< The GV is protected

        int value;
        VisibilityTypes(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }

        public static CXXEnumMap<VisibilityTypes> map = new CXXEnumMap<>(values());
        public static VisibilityTypes get(int value) {
            return map.get(value);
        }
    };

    @FFIGen
    @FFILibrary(value = "llvm::GlobalValue", namespace = "llvm::GlobalValue")
    @CXXHead("llvm/IR/GlobalValue.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        // LinkageTypes
        @FFIGetter
        int ExternalLinkage(); ///< Externally visible function
        @FFIGetter
        int AvailableExternallyLinkage(); ///< Available for inspection(); not emission.
        @FFIGetter
        int LinkOnceAnyLinkage(); ///< Keep one copy of function when linking (inline)
        @FFIGetter
        int LinkOnceODRLinkage(); ///< Same(); but only replaced by something equivalent.
        @FFIGetter
        int WeakAnyLinkage();     ///< Keep one copy of named function when linking (weak)
        @FFIGetter
        int WeakODRLinkage();     ///< Same(); but only replaced by something equivalent.
        @FFIGetter
        int AppendingLinkage();   ///< Special purpose(); only applies to global arrays
        @FFIGetter
        int InternalLinkage();    ///< Rename collisions when linking (static functions).
        @FFIGetter
        int PrivateLinkage();     ///< Like Internal(); but omit from symbol table.
        @FFIGetter
        int ExternalWeakLinkage();///< ExternalWeak linkage description.
        @FFIGetter
        int CommonLinkage();      ///< Tentative definitions.

        // VisibilityTypes
        @FFIGetter
        int DefaultVisibility();    ///< The GV is visible
        @FFIGetter
        int HiddenVisibility();     ///< The GV is hidden
        @FFIGetter
        int ProtectedVisibility();  ///< The GV is protected
    }


}
