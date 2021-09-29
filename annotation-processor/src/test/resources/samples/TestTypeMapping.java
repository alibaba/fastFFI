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
import com.alibaba.fastffi.CXXSuperTemplate;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;

@FFIGen(library = "ffitest")
@CXXHead("ffitest.h")
@FFITypeAlias("test::TestTypeMapping")
@CXXSuperTemplate(type = "samples.TestTypeMappingGenericSuper",
        template = @CXXTemplate(cxx = "test::TestTypeMappingGenericSuper<int>", java = "TestTypeMappingGenericSuper<java.lang.Integer>"))
public interface TestTypeMapping extends TestTypeMappingGenericSuper<TestTypeMappingGenericSuper<Integer>> {

    Factory factory = FFITypeFactory.getFactory(TestTypeMapping.class);

    @FFIFactory
    interface Factory {
        TestTypeMapping create(int value);
    }

    @FFITypeAlias("test::TestTypeMappingGenericSuper<char>")
    @CXXValue TestTypeMappingGenericSuper<Byte> getCharValue();
}
