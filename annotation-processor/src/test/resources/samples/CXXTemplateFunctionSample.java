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

@FFIGen(library = "CXXTemplateFunctionSample")
@FFITypeAlias("CXXTemplateFunctionSample")
@CXXTemplate(cxx = {"uint32_t", "double"}, java = {"java.lang.Integer", "java.lang.Double"})
@CXXTemplate(cxx= {"uint32_t", "int64_t"}, java={"java.lang.Integer", "java.lang.Long"})
public interface CXXTemplateFunctionSample<T1, T2> extends FFIPointer {

    void api1(T1 t1, T2 t2);

    void api2(T1 t1, T2 t2, int i);

    @CXXTemplate(cxx = {"uint32_t"}, java = {"java.lang.Integer"})
    @CXXTemplate(cxx = {"double"}, java = {"java.lang.Double"})
    @CXXTemplate(cxx = {"uint64_t"}, java = {"java.lang.Long"}, include = {@CXXHead("test.h")})
    <T3> void api3(T1 t1, T2 t2, T3 t3);

    @CXXTemplate(cxx = {"uint32_t"}, java = {"java.lang.Integer"})
    @CXXTemplate(cxx = {"double"}, java = {"java.lang.Double"})
    <T3> CXXTemplateSample<T2, T3> api4(T1 t1, T2 t2, T3 t3);
}