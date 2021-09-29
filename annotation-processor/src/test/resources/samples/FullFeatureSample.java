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
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen(library = "ffitest")
@FFITypeAlias("FullFeature")
@CXXHead(value = "FullFeature.h", system = "stdio.h")
@CXXTemplate(cxx = {"int", "float"}, java = {"java.lang.Integer", "java.lang.Float"})
@CXXTemplate(cxx = {"int", "double"}, java = {"java.lang.Integer", "java.lang.Double"}, include = @CXXHead(system = "string"))
public interface FullFeatureSample<P1, P2> extends CXXPointer {

    byte echo(byte i);

    boolean echo(boolean i);

    short echo(short i);

    int echo(int i);

    long echo(long i);

    float echo(float i);

    double echo(double i);

    void setField(int i);

    @FFINameAlias("setField")
    void setField2(int i);

    @FFIExpr("{0}.field = {1}")
    void setFieldExpr(int i);

    int getField();

    void templateCall(P1 i, P2 j);

    void unknownException();

    FullFeatureSample<P1, P2> make();

    @CXXOperator("*&")
    @CXXValue FullFeatureSample<P1, P2> copy();

    @CXXOperator("sizeof")
    long size();

    @CXXOperator("delete")
    void delete();

    @CXXTemplate(cxx = "int", java = "java.lang.Integer")
    <T> void templateMethod(T t);

    @FFIFactory
    interface Factory<P1, P2> {
        FullFeatureSample<P1, P2> create();
    }

    @FFIGen(library = "Library")
    @CXXHead(value = "FullFeature.h", system = "stdio.h")
    @FFILibrary(value = "Library", namespace = "")
    interface Library {
        int libraryApi();
    }

    @CXXValue
    FullFeatureSample<P1, P2> rvTest();
}