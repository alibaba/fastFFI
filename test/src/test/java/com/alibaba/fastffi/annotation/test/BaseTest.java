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
package com.alibaba.fastffi.annotation.test;

import org.junit.Assert;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

public class BaseTest {

    @Test
    public void testFoo() throws URISyntaxException, IOException {
        File[] foo = new File[]{new File(
            Objects.requireNonNull(BaseTest.class.getClassLoader().getResource("Foo.java")).toURI())};

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, Locale.getDefault(),
                                                                              Charset.defaultCharset());
        Iterable<? extends JavaFileObject> javaFileObjectsFromFiles = fileManager.getJavaFileObjectsFromFiles(
            Arrays.asList(foo));


        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        Assert.assertTrue(tmpDir.exists() && tmpDir.isDirectory());
        File fooDir = new File(tmpDir, "foo");
        fooDir.mkdir();
        Assert.assertTrue(fooDir.exists() && fooDir.isDirectory());
        List<String> optionsList = new ArrayList<>();
        optionsList.add("-d");
        optionsList.add(fooDir.getPath());

        JavaCompiler.CompilationTask task =
            compiler.getTask(null, fileManager, null, optionsList, null, javaFileObjectsFromFiles);
        task.call();
        fileManager.close();
    }
}
