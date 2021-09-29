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
package com.alibaba.fastffi.llvm4jni;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import static org.junit.Assert.fail;

public class TestUtils {

    static final String RESOURCE_PATH;

    static final String JNI_INCLUDE;

    static final String JNI_MD_INCLUDE;

    static final String LIBRARY_SUFFIX;

    static final String CLANGXX_PATH;

    static final boolean isWindows;
    static final boolean isMacOS;
    static final boolean isLinux;


    static {
        URL url = TestUtils.class.getClassLoader().getResource("dummy");
        Assert.assertNotNull(url);
        RESOURCE_PATH = new File(url.getFile()).getParent();

        String home = System.getProperty("java.home");
        if (home.endsWith("/jre")) {
            home = home.substring(0, home.length() - 4);
        }
        JNI_INCLUDE = home + File.separator + "include";
        String uname;
        try {

            Process p = Runtime.getRuntime().exec("uname -s");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                uname = br.readLine().toLowerCase();
            }

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        JNI_MD_INCLUDE = JNI_INCLUDE + File.separator + uname;

        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("win")) {
            LIBRARY_SUFFIX = ".dll";
            isWindows = true;
            isLinux = false;
            isMacOS = false;
        } else if (os.startsWith("mac")) {
            LIBRARY_SUFFIX = ".dylib";
            isWindows = false;
            isLinux = false;
            isMacOS = true;
        } else {
            LIBRARY_SUFFIX = ".so";
            isWindows = false;
            isLinux = true;
            isMacOS = false;
        }

        String LLVM11_HOME = System.getenv("LLVM11_HOME");
        if (LLVM11_HOME == null) {
            throw new RuntimeException("No LLVM11_HOME env is set");
        }
        CLANGXX_PATH = String.join(File.separator, LLVM11_HOME, "bin", "clang++");
    }

    static Products prepareBitcodeAndLibrary(String target) throws Exception {
        Products result = new Products();
        URL url = TestUtils.class.getClassLoader().getResource(target + ".cpp");
        Assert.assertNotNull(url);
        File file = new File(url.getFile());
        Assert.assertTrue(file.exists());
        String bc = file.getParent() + File.separator + target + ".bc";
        String input = file.getParent() + File.separator + target + ".cpp";

        exec(String.format("%s -O2 -fPIC -I %s -I %s -emit-llvm -c %s -o %s", CLANGXX_PATH, JNI_INCLUDE, JNI_MD_INCLUDE, input, bc));

        String object = file.getParent() + File.separator + target + ".o";
        exec(String.format("%s -O2 -fPIC -I %s -I %s -c %s -o %s", CLANGXX_PATH, JNI_INCLUDE, JNI_MD_INCLUDE, input, object));
        String library = file.getParent() + File.separator + target + LIBRARY_SUFFIX;
        exec(String.format("%s %s -shared -fPIC -o %s", CLANGXX_PATH, object, library));

        Runtime.getRuntime().load(library);
        result.bitcode = bc;
        result.library = library;
        return result;
    }

    private static void exec(String cmd) throws Exception {
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        if (process.exitValue() != 0) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.err.println(line);
                }
            }
            fail();
        }
    }
}
