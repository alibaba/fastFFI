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
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;
import com.alibaba.fastffi.llvm.LLVMPointer;
import com.alibaba.fastffi.llvm.StringOStream;
import com.alibaba.fastffi.stdcxx.StdString;

@FFIGen
@FFITypeAlias("clang::TemplateName")
@CXXHead({"clang/AST/TemplateName.h", "llvm/Support/raw_ostream.h"})
public interface TemplateName extends LLVMPointer {
    TemplateName.Factory FACTORY = FFITypeFactory.getFactory(TemplateName.class);

    static TemplateName create(TemplateDecl Template) {
        return FACTORY.create(Template);
    }

    TemplateDecl getAsTemplateDecl();
    @CXXValue TemplateName getUnderlying();

    boolean isNull();
    NameKind getKind();

    @CXXValue TemplateName getNameToSubstitute();
    boolean isDependent();
    boolean isInstantiationDependent();
    boolean containsUnexpandedParameterPack();

    void dump(@CXXReference StringOStream Out);

    default String dump() {
        StdString out = StdString.create();
        StringOStream os = StringOStream.create(out);
        dump(os);
        return out.toJavaString();
    }

    @FFIFactory
    @CXXHead("clang/Sema/Template.h")
    interface Factory {
        TemplateName create(TemplateDecl Template);
    }

    @FFITypeAlias("clang::TemplateName::NameKind")
    @FFITypeRefiner("com.alibaba.fastffi.clang.TemplateName.NameKind.get")
    enum NameKind implements CXXEnum
    {
        Template(TemplateName.NameKind.Library.INSTANCE.Template()),
        OverloadedTemplate(TemplateName.NameKind.Library.INSTANCE.OverloadedTemplate()),
        AssumedTemplate(TemplateName.NameKind.Library.INSTANCE.AssumedTemplate()),
        QualifiedTemplate(TemplateName.NameKind.Library.INSTANCE.QualifiedTemplate()),
        DependentTemplate(TemplateName.NameKind.Library.INSTANCE.DependentTemplate()),
        SubstTemplateTemplateParm(TemplateName.NameKind.Library.INSTANCE.SubstTemplateTemplateParm()),
        SubstTemplateTemplateParmPack(TemplateName.NameKind.Library.INSTANCE.SubstTemplateTemplateParmPack()),
        ;

        @FFIGen
        @CXXHead("clang/AST/TemplateName.h")
        @FFILibrary(value = "clang::TemplateName::NameKind", namespace = "clang::TemplateName::NameKind")
        interface Library {
            TemplateName.NameKind.Library INSTANCE = FFITypeFactory.getLibrary(TemplateName.NameKind.Library.class);
            @FFIGetter int Template();
            @FFIGetter int OverloadedTemplate();
            @FFIGetter int AssumedTemplate();
            @FFIGetter int QualifiedTemplate();
            @FFIGetter int DependentTemplate();
            @FFIGetter int SubstTemplateTemplateParm();
            @FFIGetter int SubstTemplateTemplateParmPack();
        }

        int value;
        NameKind(int value) {
            this.value = value;
        }

        private static CXXEnumMap<TemplateName.NameKind> map = new CXXEnumMap<>(TemplateName.NameKind.values());
        public static TemplateName.NameKind get(int value) {
            return map.get(value);
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
