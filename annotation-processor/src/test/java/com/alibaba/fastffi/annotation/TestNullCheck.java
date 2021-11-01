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
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Assert;
import org.junit.Test;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Arrays;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class TestNullCheck extends TestBase {

    Compilation doCompile(boolean nullCheck) {
        String name0 = "NullCheck.java";

        JavaFileObject[] sourceFiles = Arrays.asList(name0).stream().map(n ->
                JavaFileObjects.forResource("samples/" + n)
        ).toArray(JavaFileObject[]::new);
        Compiler compiler = javac().withProcessors(new AnnotationProcessor());
        if (nullCheck) {
            compiler = compiler.withOptions("-Afastffi.nullReturnValueCheck=true");
        } else {
            compiler = compiler.withOptions("-Afastffi.nullReturnValueCheck=false");
        }
        Compilation compilation = compiler.compile(sourceFiles);
        assertThat(compilation).succeeded();
        return compilation;
    }

    @Test
    public void test() throws IOException {
        {
            Compilation compilation = doCompile(false);
            {
                String content = readJavaCode(compilation, "samples/NullCheck_cxx_0xb410aba1.java");
                Assert.assertTrue(content.contains("long ret$ = nativeDefaultMode(address); return (new samples.NullCheck_cxx_0xb410aba1(ret$));"));
                Assert.assertTrue(content.contains("long ret$ = nativeNonnull(address); return (new samples.NullCheck_cxx_0xb410aba1(ret$));"));
                Assert.assertTrue(content.contains("long ret$ = nativeNullable(address); return (ret$ == 0L ? null : new samples.NullCheck_cxx_0xb410aba1(ret$));"));
            }
        }
        {
            Compilation compilation = doCompile(true);
            {
                String content = readJavaCode(compilation, "samples/NullCheck_cxx_0xb410aba1.java");
                Assert.assertTrue(content.contains("long ret$ = nativeDefaultMode(address); return (ret$ == 0L ? null : new samples.NullCheck_cxx_0xb410aba1(ret$));"));
                Assert.assertTrue(content.contains("long ret$ = nativeNonnull(address); return (new samples.NullCheck_cxx_0xb410aba1(ret$));"));
                Assert.assertTrue(content.contains("long ret$ = nativeNullable(address); return (ret$ == 0L ? null : new samples.NullCheck_cxx_0xb410aba1(ret$));"));
            }
        }
    }
}
