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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.Assert.assertTrue;

public class TestSuperTemplate extends TestBase {

    @Test
    public void testSuperTemplate() throws IOException {
        JavaFileObject file1 = JavaFileObjects.forSourceLines(
                "test.Vector",
                "package test;",
                "import com.alibaba.fastffi.*;",
                "@FFIGen(library = \"test\")",
                "@FFITypeAlias(\"test::vector\")",
                "public interface Vector<E> extends FFIPointer {",
                "  @CXXReference E get();",
                "}"
        );

        JavaFileObject file2 = JavaFileObjects.forSourceLines(
                "test.Config",
                "package test;",
                "import com.alibaba.fastffi.*;",
                "@FFIGenBatch({",
                "@FFIGen(type=\"test.Vector\", templates = {",
                "@CXXTemplate(cxx = \"int\", java=\"Integer\"),",
                "@CXXTemplate(cxx = \"char\", java=\"Byte\"),",
                "@CXXTemplate(cxx = \"test::vector<int>\", java=\"Vector<Integer>\"),",
                "@CXXTemplate(cxx = \"test::vector<test::vector<int>>\", java=\"Vector<Vector<Integer>>\")",
                 "})})",
                "public interface Config {",
                "}"
        );

        Compilation compilation = javac().withProcessors(new AnnotationProcessor())
                .compile(file1, file2);
        checkCompilation(compilation);
    }

    void checkCompilation(Compilation compilation) throws IOException {
        assertThat(compilation).succeeded();
        {
            String content = compilation.generatedFile(StandardLocation.CLASS_OUTPUT, "ffi.properties")
                    .get().getCharContent(true).toString();
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(content.getBytes()));
            assertTrue(properties.getProperty("test.Vector_cxx_0x1db64441").equals("test::vector<char>"));
            assertTrue(properties.getProperty("test.Vector_cxx_0x1eb2d56").equals("test::vector<test::vector<test::vector<int>>>"));
            assertTrue(properties.getProperty("test.Vector_cxx_0x2a428276").equals("test::vector<int>"));
            assertTrue(properties.getProperty("test.Vector_cxx_0xc1d8608f").equals("test::vector<test::vector<int>>"));
        }

    }
}
