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
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXTemplates;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen(library = "CXXTemplatesSample")
@FFITypeAlias("CXXTemplatesSample")
@CXXTemplates({
                  @CXXTemplate(cxx = {"uint32_t", "double"}, java = {"java.lang.Integer", "java.lang.Double"}),
                  @CXXTemplate(cxx = {"uint32_t", "int64_t"}, java = {"java.lang.Integer", "java.lang.Long"})
              })
public interface CXXTemplatesSample<T1, T2> extends FFIPointer {

    void api(T1 t1, T2 t2);

    @CXXTemplates({
                      @CXXTemplate(cxx = {"uint32_t", "double"}, java = {"java.lang.Integer", "java.lang.Double"}),
                      @CXXTemplate(cxx = {"uint32_t", "int64_t"}, java = {"java.lang.Integer", "java.lang.Long"})
                  })
    <MT1, MT2> void api2(MT1 mt1, MT2 mt2);
}