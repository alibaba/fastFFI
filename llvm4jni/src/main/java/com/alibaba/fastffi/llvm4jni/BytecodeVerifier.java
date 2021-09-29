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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class BytecodeVerifier {

    static class VerifierClassLoader extends URLClassLoader {
        public VerifierClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        protected Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException
        {
            synchronized (getClassLoadingLock(name)) {
                Class<?> c = findLoadedClass(name);
                if (c == null) {
                    if (c == null) {
                        try {
                            c = findClass(name);
                        } catch (ClassNotFoundException e) {
                            c = super.loadClass(name, resolve);
                        }
                    }
                }
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }
        }
    }

    static VerifierClassLoader createClassLoader(ClassLoader parent, Path ...paths) {
        URL[] urls = Arrays.stream(paths).map(p -> {
            try {
                return p.toUri().toURL();
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Not a valid URL: " + p);
            }
        }).toArray(URL[]::new);
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        return new VerifierClassLoader(urls, parent);
    }

    public static void verify(Options options, Path root, List<String> classNames) {
        try {
            VerifierClassLoader oldClassLoader = createClassLoader(ClassLoader.getSystemClassLoader(), options.getClassPath());
            VerifierClassLoader classLoader = createClassLoader(oldClassLoader, root);
            for (String className : classNames) {
                className = className.replace('/', '.');
                Class<?> cls;
                try {
                    cls = Class.forName(className, false, classLoader);
                    cls.getDeclaredFields();
                } catch (VerifyError e) {
                    throw new IllegalStateException("Fail to verify " + className, e);
                }
                if (cls.getClassLoader() != classLoader) {
                    throw new IllegalStateException("Oops");
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
