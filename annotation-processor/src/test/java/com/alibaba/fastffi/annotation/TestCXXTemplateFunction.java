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
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Arrays;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.Assert.assertTrue;

public class TestCXXTemplateFunction extends TestBase {
    @Test
    public void test() throws IOException {

        String name0 = "CXXTemplateFunctionSample.java";
        String name1 = "CXXTemplateSample.java";

        JavaFileObject[] sourceFiles = Arrays.asList(name0, name1).stream().map(n ->
                JavaFileObjects.forResource("samples/" + n)
        ).toArray(JavaFileObject[]::new);

        Compilation compilation = javac().withProcessors(new AnnotationProcessor()).compile(sourceFiles);

        assertThat(compilation).succeeded();
        // compilation.generatedFiles().forEach(f -> System.out.println(f));
        {
            String path = "samples/CXXTemplateFunctionSampleGen.java";
            String content = compilation.generatedFile(StandardLocation.SOURCE_OUTPUT, path).get().getCharContent(true).toString();
            assertTrue(content.contains("@CXXHead(\"test.h\")"));
            assertTrue(content.contains("CXXTemplateSample<T2, Double> api4(T1 t1, T2 t2, @FFITypeAlias(\"double\") Double t3);"));
        }
        {
            String path = "samples/CXXTemplateFunctionSampleGen_cxx_0x199904cc.java";
            String content = compilation.generatedFile(StandardLocation.SOURCE_OUTPUT, path).get().getCharContent(true).toString();
            assertTrue(content.contains("@CXXHead(\"test.h\")"));
            assertTrue(content.contains("CXXTemplateSample<Double, Integer> api4"));
        }
    }
}
