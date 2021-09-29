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
import org.junit.Assert;
import org.junit.Test;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Arrays;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class TestMultiInheritance extends TestBase {
    @Test
    public void test() throws IOException {
        String name0 = "CXXSubTypeSample2.java";
        String name1 = "CXXSuperTypeSample.java";
        String name2 = "CXXSuperTypeSample2.java";

        JavaFileObject[] sourceFiles = Arrays.asList(name0, name1, name2).stream().map(n ->
                JavaFileObjects.forResource("samples/" + n)
        ).toArray(JavaFileObject[]::new);

        Compilation compilation = javac().withProcessors(new AnnotationProcessor()).compile(sourceFiles);
        assertThat(compilation).succeeded();
        {
            String content = readJavaCode(compilation, "samples/CXXSubTypeSample2Gen.java");
            Assert.assertFalse(content.contains("public static native void nativeTest0(long ptr, int t0);"));
        }
        {
            String content = readJavaCode(compilation, "samples/CXXSubTypeSample2Gen_cxx_0xb6b9c631.java");
            Assert.assertTrue(content.contains("public static native void nativeTest0(long ptr, int t0);"));
        }
    }
}
