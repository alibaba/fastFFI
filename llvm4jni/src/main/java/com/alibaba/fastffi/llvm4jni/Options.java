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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Options {

    public static Options loadDefault() {
        Properties properties = new Properties(System.getProperties());
        return new Options(properties);
    }

    public static Options fromProperties(Properties properties) {
        return new Options(properties);
    }

    private Properties properties;

    private Options(Properties properties) {
        this.properties = properties;
        load();
    }

    private Path[] classPath;
    private Path libraryPath;
    private Path bitcodePath;
    private Path outputPath = Paths.get("output");
    private String moduleName;

    private boolean debug;
    private String loggerLevel = Logger.Level.WARN.name();
    private boolean supportInvokeUnwind;
    private boolean supportAlloca;
    private boolean supportLocalConstant;
    private String supportIndirectCall;
    private boolean supportDevirtualization;
    private boolean printInsn;

    private boolean verifyBytecode;

    private boolean useJavaLibC;

    private int maximumBytecodeSize;

    private void load() {
        debug = getBoolean("llvm4jni.debug");
        supportInvokeUnwind = getBoolean("llvm4jni.supportInvokeUnwind");
        supportAlloca = getBoolean("llvm4jni.supportAlloca");
        supportLocalConstant = getBoolean("llvm4jni.supportLocalConstant");
        supportIndirectCall = get("llvm4jni.supportIndirectCall", "false");
        supportDevirtualization = getBoolean("llvm4jni.supportDevirtualization", true);
        printInsn = getBoolean("llvm4jni.printInsn");
        maximumBytecodeSize = getInteger("llvm4jni.maximumBytecodeSize", 128);
        verifyBytecode = getBoolean("llvm4jni.verifyBytecode", true);
        useJavaLibC = getBoolean("llvm4jni.useJavaLibC", false);
    }

    public String getLoggerLevel() {
        return loggerLevel;
    }

    public void setLoggerLevel(String loggerLevel) {
        this.loggerLevel = loggerLevel;
    }

    public Path[] getClassPath() {
        return classPath;
    }

    public void setClassPath(Path[] classPath) {
        this.classPath = classPath;
    }

    public Path getLibraryPath() {
        return libraryPath;
    }

    public void setLibraryPath(Path libraryPath) {
        this.libraryPath = libraryPath;
    }

    public Path getBitcodePath() {
        return bitcodePath;
    }

    public void setBitcodePath(Path bitcodePath) {
        this.bitcodePath = bitcodePath;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(Path outputPath) {
        this.outputPath = outputPath;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean supportInvokeUnwind() {
        return supportInvokeUnwind;
    }

    public Options enableInvokeUnwind() {
        this.supportInvokeUnwind = true;
        return this;
    }

    public boolean supportAlloca() {
        return supportAlloca;
    }

    public Options enableAlloca() {
        this.supportAlloca = true;
        return this;
    }

    public boolean supportLocalConstant() {
        return supportLocalConstant;
    }

    public Options enableLocalConstant() {
        this.supportLocalConstant = true;
        return this;
    }

    public String supportIndirectCall() {
        return supportIndirectCall;
    }

    public Options supportIndirectCall(String option) {
        this.supportIndirectCall = option;
        return this;
    }

    public boolean supportDevirtualization() {
        return supportDevirtualization;
    }

    public Options enableDevirtualization() {
        this.supportDevirtualization = true;
        return this;
    }

    public boolean printInsn() {
        return printInsn;
    }

    public Options enablePrintInsn() {
        this.printInsn = true;
        return this;
    }

    public boolean verifyBytecode() {
        return verifyBytecode;
    }

    public Options disableVerifyBytecode() {
        this.verifyBytecode = false;
        return this;
    }

    public int getInteger(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            if (value == null) {
                return defaultValue;
            }
            return Integer.parseInt(value);
        } catch (IllegalArgumentException | NullPointerException e ) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            String value = properties.getProperty(key);
            if (value == null) {
                return defaultValue;
            }
            return Boolean.parseBoolean(value);
        } catch (IllegalArgumentException | NullPointerException e ) {
            return defaultValue;
        }
    }

    public boolean useJavaLibC() {
        return this.useJavaLibC;
    }

    public int maximumBytecodeSize() {
        return maximumBytecodeSize;
    }
}
