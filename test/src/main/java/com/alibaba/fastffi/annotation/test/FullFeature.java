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
package com.alibaba.fastffi.annotation.test;

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.stdcxx.test.StdString;

@FFIGen(library = "ffitest")
@FFITypeAlias("FullFeature")
@CXXHead(value = "FullFeature.h", system = "stdio.h")
@CXXTemplate(cxx = {"int", "float"}, java = {"Integer", "Float"})
@CXXTemplate(cxx = {"int", "double"}, java = {"Integer", "Double"})
public interface FullFeature<P1, P2> extends CXXPointer {

    byte echo(byte i);

    boolean echo(boolean i);

    short echo(short i);

    int echo(int i);

    long echo(long i);

    @FFIExpr("{0}->echo({1})")
    long echoExpr(long i);

    float echo(float i);

    double echo(double i);

    void setField(int i);

    @FFIExpr("{0}->setField({1})")
    void setFieldExpr(int i);

    int getField();

    void templateCall(P1 i, P2 j);

    void unknownException();

    FullFeature<P1, P2> make();

    @CXXOperator("*&")
    @CXXValue FullFeature<P1, P2> copy();

    @CXXOperator("sizeof")
    long size();

    @CXXOperator("delete")
    void delete();

    @CXXTemplate(cxx = "int", java = "java.lang.Integer")
    <T> void templateMethod(T t);

    @FFIFactory
    interface Factory<P1, P2> {
        FullFeature<P1, P2> create();
    }

    @FFIGen(library = "ffitest")
    @CXXHead(value = "FullFeature.h", system = "stdio.h")
    @FFILibrary(value = "Library", namespace = "")
    interface Library {
        @CXXValue StdString libraryApi();
    }

    @CXXValue
    FullFeature<P1, P2> rvTest();
}