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

import static com.google.testing.compile.CompilationSubject.assertThat;
import static org.junit.Assert.assertTrue;

public class TestFFILibrary extends TestBase {
    @Test
    public void test() throws IOException {
        Compilation compilation = compile(this.getClass());
        assertThat(compilation).succeeded();
        // compilation.generatedFiles().forEach(f -> System.out.println(f));
        {
            String content = readJNICode(compilation, "jni_samples_FFILibrarySample_cxx_0x6b56353c.cc");
            String expect = "#include <jni.h>\n" +
                    "#include <new>\n" +
                    "#include <cstring>\n" +
                    "\n" +
                    "#ifdef __cplusplus\n" +
                    "extern \"C\" {\n" +
                    "#endif\n" +
                    "\n" +
                    "// Common Stubs\n" +
                    "\n" +
                    "JNIEXPORT\n" +
                    "jint JNICALL Java_samples_FFILibrarySample_1cxx_10x6b56353c_nativeStrcmp(JNIEnv*, jclass, jlong arg0 /* str10 */, jlong arg1 /* str21 */) {\n" +
                    "\treturn (jint)(std::strcmp(((const char*) arg0), ((const char*) arg1)));\n" +
                    "}\n" +
                    "\n" +
                    "#ifdef __cplusplus\n" +
                    "}\n" +
                    "#endif\n";
            assertTrue(content.equals(expect));
        }
    }
}
