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

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import java.io.IOException;

import static com.google.testing.compile.Compiler.javac;

public class TestBase {

    private JavaFileObject target(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return JavaFileObjects.forResource("samples/" + name.substring(4) + "Sample.java");
    }

    protected void printGeneratedFiles(Compilation compilation) {
        compilation.generatedFiles().forEach(f -> {
            System.out.println(f);
        });
    }

    protected String readJavaCode(Compilation compilation, String fileName) throws IOException {
        return compilation.generatedFile(StandardLocation.SOURCE_OUTPUT, fileName).get().getCharContent(true).toString();
    }

    protected String readJNICode(Compilation compilation, String fileName) throws IOException {
        return compilation.generatedFile(StandardLocation.SOURCE_OUTPUT, fileName).get().getCharContent(true).toString();
    }

    protected final Compilation compile(Class<?> clazz) {
        return javac().withProcessors(new AnnotationProcessor()).compile(target(clazz));
    }

    protected final Compilation compile(Class<?> clazz, Object... options) {
        return javac().withProcessors(new AnnotationProcessor()).withOptions(options).compile(target(clazz));
    }
}
