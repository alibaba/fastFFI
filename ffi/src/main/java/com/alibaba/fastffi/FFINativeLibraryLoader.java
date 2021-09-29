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
package com.alibaba.fastffi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class FFINativeLibraryLoader {

    /**
     * The same library can be only loaded once, regardless of which class loader loads the library.
     */
    private static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    private static final String OS;
    private static final String ARCH;

    static {
        OS = osname();
        ARCH = arch();
    }

    static String osname() {
        String os = System.getProperty("os.name");
        if (os == null) {
            throw new IllegalStateException("Cannot get property os.name");
        }
        os = os.toLowerCase();
        if (os.contains("linux")) {
            return "linux";
        }
        if (os.contains("windows")) {
            return "windows";
        }
        if (os.contains("darwin") || os.contains("os x")) {
            return "darwin";
        }
        throw new IllegalStateException("Unsupported operating system: " + System.getProperty("os.name"));
    }

    static String arch() {
        String arch = System.getProperty("os.arch");
        if (arch == null) {
            throw new IllegalStateException("Cannot get property os.arch");
        }
        // The only arch we are currently supporting is x86_64
        // We could support AArch64 in the future.
        if (arch.equals("x86_64") || arch.equals("amd64")) {
            return "x86_64";
        }
        throw new IllegalStateException("Unsupported architecture: " + System.getProperty("os.arch"));
    }

    public static void load(ClassLoader classLoader, String libraryName) {
        // First try to load from java.library.path
        try {
            System.loadLibrary(libraryName);
        } catch (UnsatisfiedLinkError e) {
            System.load(findLibrary(classLoader, libraryName));
        }
    }

    public static String findLibrary(Class<?> clazz, String libraryName) {
        return findLibrary(clazz.getClassLoader(), libraryName);
    }

    public static String findLibrary(ClassLoader classLoader, String libraryName) {
        String systemPath = map.get(libraryName);
        if (systemPath != null) {
            return systemPath;
        }

        String systemName = System.mapLibraryName(libraryName);
        try {
            Enumeration<URL> libs = classLoader.getResources(systemName);
            if (!libs.hasMoreElements()) {
                throw new IllegalStateException("Cannot find library " + systemName + " in class loader " + classLoader);
            }
            URL libURL = libs.nextElement();
            Path directory = Files.createTempDirectory("fastffi-" + systemName);
            directory.toAbsolutePath().toFile().deleteOnExit();
            Path libPath = directory.resolve(systemName).toAbsolutePath();
            libPath.toAbsolutePath().toFile().deleteOnExit();

            ReadableByteChannel rbc = Channels.newChannel(libURL.openStream());
            FileOutputStream fos = new FileOutputStream(libPath.toFile());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            // Check duplicated
            if (libs.hasMoreElements()) {
                System.err.println("Using " + libURL + " for " + systemName);
                while (libs.hasMoreElements()) {
                    System.err.println("Find another " + libs.nextElement() + " for " + systemName);
                }
            }

            return libPath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load library " + systemName);
        }
    }
}
