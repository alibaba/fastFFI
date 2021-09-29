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
package com.alibaba.fastffi.annotation;

import com.google.testing.compile.Compilation;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCXXTemplate extends TestBase {
    @Test
    public void test() throws IOException {
        Compilation compilation = compile(this.getClass());
        assertThat(compilation).succeeded();
        {
            String content = readJavaCode(compilation, "jni_samples_CXXTemplateSample_cxx_0x3c79cf85.cc");
            // System.out.println(content);
            assertTrue(content.contains("#include <string>"));
            assertTrue(content.contains("#include \"test.h\""));
        }
        {
            String content = readJavaCode(compilation, "samples/CXXEmptyTemplateSample2Gen.java");
            assertTrue(content.contains("default <T> void api(T t) {\n" +
                    "    throw new RuntimeException(\"Cannot call CXXEmptyTemplateSample2Gen.api, no template instantiation for the type arguments.\");\n" +
                    "  }"));
        }
        {
            String content = readJavaCode(compilation, "samples/CXXEmptyTemplateSample4Gen.java");
            assertFalse(content.contains("void api(T t)"));
            assertTrue(content.contains("void api2(T t)"));
            assertTrue(Pattern.compile("extends.*CXXEmptyTemplateSample2Gen").matcher(content).find());
        }
    }
}
