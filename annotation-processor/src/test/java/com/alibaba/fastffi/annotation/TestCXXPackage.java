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

public class TestCXXPackage extends TestBase {
    @Test
    public void test() throws IOException {
        String name0 = "CXXPackageSample.java";

        JavaFileObject[] sourceFiles = Arrays.asList(name0).stream().map(n ->
                JavaFileObjects.forResource("samples/pkg1/pkg2/" + n)
        ).toArray(JavaFileObject[]::new);

        Compilation compilation = javac().withProcessors(new AnnotationProcessor()).compile(sourceFiles);
        assertThat(compilation).succeeded();
        Assert.assertTrue(readJavaCode(compilation, "samples/pkg1/pkg2/CXXPackageSample_cxx_0x1ec4dfcd.java").contains("System.loadLibrary(\"testLibrary\");"));
    }
}
