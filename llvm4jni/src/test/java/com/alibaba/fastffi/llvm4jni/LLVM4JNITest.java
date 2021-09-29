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

import com.alibaba.fastffi.FFITypeFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.alibaba.fastffi.llvm.TestUtils.deleteDirectory;

public class LLVM4JNITest {

    void runLLVM4JNI(Options options) throws Exception {
        try {
            // Main.main(new String[] {"-v", "INFO", "-cp", classpath, "-lib", library, "-output", output.toAbsolutePath().toString()});
            Main.parseAndGenerate(options);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    Options getDefaultOptions() throws Exception {
        String classpath = System.getProperty("java.class.path");
        classpath = Arrays.stream(classpath.split(File.pathSeparator)).filter(s ->
                s.contains("target") && s.contains("llvm4jni") && s.contains("test") && !s.contains("llvm4jni-runtime")
        ).collect(Collectors.joining(File.pathSeparator));
        Path output = Files.createTempDirectory("fastffi-test");

        Options options = Options.loadDefault();
        options.setLoggerLevel(Logger.Level.ERROR.name());

        String library = FFITypeFactory.findNativeLibrary(OperatorFFILibrary.class, "llvm4jni-test");
        if (TestUtils.isMacOS) {
            URL url = LLVM4JNITest.class.getClassLoader().getResource("llvm4jni-test.bc");
            Assert.assertTrue("Must be in file system", url.getProtocol().startsWith("file"));
            options.setBitcodePath(Paths.get(url.toURI()));
            options.setLibraryPath(Paths.get(library));
        } else if (TestUtils.isLinux) {
            options.setLibraryPath(Paths.get(library));
        } else {
            Assert.assertTrue("Should not reach here", false);
        }
        options.setClassPath(Main.parseClassPath(classpath));
        options.setOutputPath(output.toAbsolutePath());
        return options;
    }

    GeneratedTestClassLoader generatedTestClassLoader(Options options) throws Exception {
        // load new class
        List<URL> urls = Arrays.stream(options.getClassPath()).map(f -> {
            try {
                return f.toUri().toURL();
            } catch (MalformedURLException e) {
                throw new IllegalStateException(e);
            }
        }).collect(Collectors.toList());
        urls.add(0, options.getOutputPath().toUri().toURL());
        GeneratedTestClassLoader cl = new GeneratedTestClassLoader(urls.toArray(new URL[0]), LLVM4JNITest.class.getClassLoader());
        return cl;
    }

    void runTestClasses(List<Class<?>> testClasses) throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.addListener(new TextListener(System.out));
        Result result = runner.run(testClasses.toArray(new Class[0]));
        Assert.assertTrue(result.wasSuccessful());
    }

    @Test
    public void testIndirectSimple() throws Exception {
        Options options = getDefaultOptions();
        options.supportIndirectCall("simple");
        runLLVM4JNI(options);

        GeneratedTestClassLoader cl = generatedTestClassLoader(options);
        List<Class<?>> testClasses = new ArrayList<>();
        testClasses.add(Class.forName(IndirectTest.class.getName(), false, cl));

        checkFFIPointer(IndirectTestStruct.class, cl, f -> f.getName().equals("nativeGetAndAdd"), f -> false);

        runTestClasses(testClasses);
        deleteOutput(options);
    }

    private void deleteOutput(Options options) {
        File output = options.getOutputPath().toAbsolutePath().toFile();
        deleteDirectory(output);
    }

    @Test
    public void testIndirectFalse() throws Exception {
        Options options = getDefaultOptions();
        options.supportIndirectCall("false");
        runLLVM4JNI(options);

        GeneratedTestClassLoader cl = generatedTestClassLoader(options);
        List<Class<?>> testClasses = new ArrayList<>();
        testClasses.add(Class.forName(IndirectTest.class.getName(), false, cl));

        checkFFIPointer(IndirectTestStruct.class, cl, f -> false, f -> f.getName().equals("nativeGetAndAdd"));

        runTestClasses(testClasses);
        deleteOutput(options);
    }

    @Test
    public void testIndirectTrue() throws Exception {
        Options options = getDefaultOptions();
        options.supportIndirectCall("true");
        runLLVM4JNI(options);

        GeneratedTestClassLoader cl = generatedTestClassLoader(options);
        List<Class<?>> testClasses = new ArrayList<>();
        testClasses.add(Class.forName(IndirectTest.class.getName(), false, cl));

        checkFFIPointer(IndirectTestStruct.class, cl, f -> f.getName().equals("nativeGetAndAdd"), f -> false);

        runTestClasses(testClasses);
        deleteOutput(options);
    }

    @Test
    public void testAlloca() throws Exception {
        Options options = getDefaultOptions();
        options.enableAlloca();
        runLLVM4JNI(options);

        GeneratedTestClassLoader cl = generatedTestClassLoader(options);
        List<Class<?>> testClasses = new ArrayList<>();
        testClasses.add(Class.forName(MiscTest.class.getName(), false, cl));

        checkFFILibrary(MiscFFILibrary.class, cl, f -> f.getName().equals("nativeTest_alloca"), f -> false);

        runTestClasses(testClasses);
        deleteOutput(options);
    }

    @Test
    public void testLocalConst() throws Exception {
        Options options = getDefaultOptions();
        options.enableLocalConstant();
        runLLVM4JNI(options);

        GeneratedTestClassLoader cl = generatedTestClassLoader(options);
        List<Class<?>> testClasses = new ArrayList<>();
        testClasses.add(Class.forName(MiscTest.class.getName(), false, cl));

        checkFFILibrary(MiscFFILibrary.class, cl, f ->
                f.getName().equals("nativeTest_switch_table")
                    || f.getName().equals("nativeTest_localconst")
                    || f.getName().equals("nativeTest_switch"),
                f -> false);

        runTestClasses(testClasses);
        deleteOutput(options);
    }

    @Test
    public void test() throws Exception {
        Options options = getDefaultOptions();
        options.enableLocalConstant();
        options.enableAlloca();
        options.supportIndirectCall("true");
        runLLVM4JNI(options);
        GeneratedTestClassLoader cl = generatedTestClassLoader(options);

        List<Class<?>> testClasses = new ArrayList<>();
        testClasses.add(Class.forName(LoadStoreTest.class.getName(), false, cl));
        testClasses.add(Class.forName(OperatorTest.class.getName(), false, cl));
        testClasses.add(Class.forName(VTableTest.class.getName(), false, cl));
        testClasses.add(Class.forName(MiscTest.class.getName(), false, cl));
        testClasses.add(Class.forName(MemoryOperationTest.class.getName(), false, cl));

        // Check FFILibrary
        checkFFILibrary(LoadStoreTestFFILibrary.class, cl, f -> true, f -> false);
        checkFFILibrary(OperatorFFILibrary.class, cl, f -> true, f -> false);
        checkFFILibrary(MiscFFILibrary.class, cl, f -> false,  f -> false);
        checkFFIPointer(VTableTestStruct.class, cl, f -> f.getName().equals("nativeDoCalc"),  f -> false);
        checkFFIPointer(MemoryOperationTestStruct.class, cl, f -> f.getName().startsWith("nativeTest_mem"),  f -> false);

        runTestClasses(testClasses);
        deleteOutput(options);
    }

    private void checkFFIPointer(Class<?> original, GeneratedTestClassLoader cl, Predicate<Method> nonNativeFilter, Predicate<Method> nativeFilter) throws Exception {
        String name = original.getName();
        Class<?> transformed = Class.forName(name, false, cl);
        Class<?> type = FFITypeFactory.getType(transformed);
        checkFFIClass(type, nonNativeFilter, nativeFilter);
    }

    static void checkFFILibrary(Class<?> original, GeneratedTestClassLoader cl, Predicate<Method> nonNativeFilter, Predicate<Method> nativeFilter) throws Exception {
        String name = original.getName();
        Class<?> transformed = Class.forName(name, false, cl);
        Class<?> libraryClass = FFITypeFactory.getLibrary(transformed).getClass();
        checkFFIClass(libraryClass, nonNativeFilter, nativeFilter);
    }

    static void checkFFIClass(Class<?> ffiClass, Predicate<Method> nonNativeFilter, Predicate<Method> nativeFilter) {
        for (Method method : ffiClass.getDeclaredMethods()) {
            if (nonNativeFilter.test(method)) {
                Assert.assertFalse(Modifier.isNative(method.getModifiers()));
            } else if (nativeFilter.test(method)) {
                Assert.assertTrue(Modifier.isNative(method.getModifiers()));
            }
        }
    }
}
