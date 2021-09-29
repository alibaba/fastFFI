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
package samples;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen(library = "CXXTemplateSample")
@FFITypeAlias("CXXTemplateSample")
@CXXTemplate(cxx = {"uint32_t", "double"}, java = {"java.lang.Integer", "java.lang.Double"})
@CXXTemplate(cxx = {"double", "double"}, java = {"Double", "Double"})
@CXXTemplate(cxx = {"int64_t", "double"}, java = {"Long", "Double"})
@CXXTemplate(cxx = {"double", "uint32_t"}, java = {"Double", "Integer"})
@CXXTemplate(cxx = {"int64_t", "uint32_t"}, java = {"Long", "Integer"})
@CXXTemplate(cxx = {"uint32_t", "float"}, cxxFull = "<uint32_t,float,2>", java = {"java.lang.Integer", "java.lang.Float"})
@CXXTemplate(cxx= {"uint32_t", "int64_t"}, java={"java.lang.Integer", "java.lang.Long"}, include = {@CXXHead(system = "string", value = "test.h")})
public interface CXXTemplateSample<T1, T2> extends FFIPointer {

    void api1(T1 t1, T2 t2);

    void api2(T1 t1, T2 t2, int i);
}

@FFIGen(library = "CXXTemplateSample")
@FFITypeAlias("CXXEmptyTemplateSample1")
interface CXXEmptyTemplateSample1<T> extends FFIPointer {
    void api(T t);
}

@FFIGen(library = "CXXTemplateSample")
@FFITypeAlias("CXXEmptyTemplateSample2")
interface CXXEmptyTemplateSample2 extends FFIPointer {
    <T> void api(T t);
}

@FFIGen(library = "CXXTemplateSample")
@FFITypeAlias("CXXEmptyTemplateSample3")
interface CXXEmptyTemplateSample3<T1> extends FFIPointer {
    <T2> void api(T1 t1, T2 t2);
}

@FFIGen(library = "CXXTemplateSample")
@FFITypeAlias("CXXEmptyTemplateSample4")
interface CXXEmptyTemplateSample4 extends CXXEmptyTemplateSample2 {
    <T> void api2(T t);
}