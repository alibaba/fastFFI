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

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFISetter;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.llvm.LLVMPointer;
import com.alibaba.fastffi.llvm.StringRef;

@FFIGen
@CXXHead("clang/AST/PrettyPrinter.h")
@FFITypeAlias("clang::PrintingPolicy")
public interface PrintingPolicy extends LLVMPointer {
    void adjustForCPlusPlus();

    @FFIGetter byte Indentation();
    @FFISetter void Indentation(byte v);
    @FFIGetter boolean SuppressSpecifiers();
    @FFISetter void SuppressSpecifiers(boolean v);
    @FFIGetter boolean SuppressTagKeyword();
    @FFISetter void SuppressTagKeyword(boolean v);
    @FFIGetter boolean IncludeTagDefinition();
    @FFISetter void IncludeTagDefinition(boolean v);
    @FFIGetter boolean SuppressScope();
    @FFISetter void SuppressScope(boolean v);
    @FFIGetter boolean SuppressUnwrittenScope();
    @FFISetter void SuppressUnwrittenScope(boolean v);
    @FFIGetter boolean SuppressInitializers();
    @FFISetter void SuppressInitializers(boolean v);
    @FFIGetter boolean ConstantArraySizeAsWritten();
    @FFISetter void ConstantArraySizeAsWritten(boolean v);
    @FFIGetter boolean AnonymousTagLocations();
    @FFISetter void AnonymousTagLocations(boolean v);
    @FFIGetter boolean SuppressStrongLifetime();
    @FFISetter void SuppressStrongLifetime(boolean v);
    @FFIGetter boolean SuppressLifetimeQualifiers();
    @FFISetter void SuppressLifetimeQualifiers(boolean v);
    @FFIGetter boolean SuppressTemplateArgsInCXXConstructors();
    @FFISetter void SuppressTemplateArgsInCXXConstructors(boolean v);
    @FFIGetter boolean Bool();
    @FFISetter void Bool(boolean v);
    @FFIGetter boolean Restrict();
    @FFISetter void Restrict(boolean v);
    @FFIGetter boolean Alignof();
    @FFISetter void Alignof(boolean v);
    @FFIGetter boolean UnderscoreAlignof();
    @FFISetter void UnderscoreAlignof(boolean v);
    @FFIGetter boolean UseVoidForZeroParams();
    @FFISetter void UseVoidForZeroParams(boolean v);
    @FFIGetter boolean SplitTemplateClosers();
    @FFISetter void SplitTemplateClosers(boolean v);
    @FFIGetter boolean TerseOutput();
    @FFISetter void TerseOutput(boolean v);
    @FFIGetter boolean PolishForDeclaration();
    @FFISetter void PolishForDeclaration(boolean v);
    @FFIGetter boolean Half();
    @FFISetter void Half(boolean v);
    @FFIGetter boolean MSWChar();
    @FFISetter void MSWChar(boolean v);
    @FFIGetter boolean IncludeNewlines();
    @FFISetter void IncludeNewlines(boolean v);
    @FFIGetter boolean MSVCFormatting();
    @FFISetter void MSVCFormatting(boolean v);
    @FFIGetter boolean ConstantsAsWritten();
    @FFISetter void ConstantsAsWritten(boolean v);
    @FFIGetter boolean SuppressImplicitBase();
    @FFISetter void SuppressImplicitBase(boolean v);
    @FFIGetter boolean FullyQualifiedName();
    @FFISetter void FullyQualifiedName(boolean v);
    @FFIGetter boolean PrintCanonicalTypes();
    @FFISetter void PrintCanonicalTypes(boolean v);
    @FFIGetter boolean PrintInjectedClassNameWithArguments();
    @FFISetter void PrintInjectedClassNameWithArguments(boolean v);
}
