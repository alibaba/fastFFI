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

import com.alibaba.fastffi.CXXStackObject;
import com.alibaba.fastffi.llvm.Function;
import com.alibaba.fastffi.llvm.LLVMContext;
import com.alibaba.fastffi.llvm.Module;
import com.alibaba.fastffi.llvm.bitcode.BitcodeParser;
import com.alibaba.fastffi.llvm4jni.body.InputClass;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    static Map<String, InputClass> collectNativeMethods(Path[] classPath) {
        return new JavaClassCollector(classPath).collect();
    }

    static String readArg(int pos, String[] args, String errorMsg) {
        if (pos >= args.length) {
            throw new IllegalStateException(errorMsg);
        }
        return args[pos];
    }

    static Path parsePath(String path) {
        return Paths.get(path).toAbsolutePath();
    }

    static Path[] parseClassPath(String classpath) {
        return Arrays.stream(classpath.split(File.pathSeparator)).map(c -> Paths.get(c)).toArray(Path[]::new);
    }

    static Options processCommandline(String[] args) {
        Options options = Options.loadDefault();
        int pos = 0;
        while (pos < args.length) {
            String option = args[pos++];
            switch (option) {
                case "-cp":
                    options.setClassPath(parseClassPath(readArg(pos++, args, "No argument for option " + option)));
                    break;
                case "-lib":
                    options.setLibraryPath(parsePath(readArg(pos++, args, "No argument for option " + option)));
                    break;
                case "-bc":
                    options.setBitcodePath(parsePath(readArg(pos++, args, "No argument for option " + option)));
                    break;
                case "-output":
                    options.setOutputPath(parsePath(readArg(pos++, args, "No argument for option " + option)));
                    break;
                case "-mn":
                    options.setModuleName(readArg(pos++, args, "No argument for option " + option));
                    break;
                case "-v":
                    String level = readArg(pos++, args, "No argument for option " + option);
                    options.setLoggerLevel(level);
                    break;
                case "-no-verify":
                    options.disableVerifyBytecode();
                    break;
                default:
                    exitAndPrintUsage("Unknown option or argument: " + option);
            }
        }
        if (options.getLibraryPath() != null) {
            ensureFileExists(options.getLibraryPath());
        } else {
            if (options.getBitcodePath() == null) {
                exitAndPrintUsage("At least one of library and bitcode path must be specified.");
            }
        }
        if (options.getClassPath() == null) {
            exitAndPrintUsage("Missing class path, please use -cp to specify a valid library path");
        }
        return options;
    }

    public static void main(String[] args) {
        Options options = processCommandline(args);
        try {
            parseAndGenerate(options);
        } catch (IOException e) {
            Logger.error(e.getMessage());
            System.exit(2);
        }
    }

    static void ensureFileExists(Path file) {
        if (!Files.isRegularFile(file)) {
            Logger.error("File " + file + " does not exist.");
            exitAndPrintUsage();
        }
    }

    public static void parseAndGenerate(Options options) throws IOException {
        Logger.initialize(options);
        Map<String, InputClass> classes = collectNativeMethods(options.getClassPath());

        try (CXXStackObject<LLVMContext> contextObject = new CXXStackObject<>(LLVMContext.newContext())) {
            Path[] classPath = options.getClassPath();
            Path bitcodePath = options.getBitcodePath();
            Path libraryPath = options.getLibraryPath();

            LLVMContext context = contextObject.get();
            Module module;
            if (bitcodePath == null) {
                module = BitcodeParser.parseEmbeddedBitcode(context, libraryPath.toString());
            } else {
                module = BitcodeParser.parseBitcodeFile(context, bitcodePath.toString());
            }
            if (module == null) {
                exitAndPrintUsage("cannot parse bitcode module");
            }
            String moduleName = options.getModuleName();;
            if (moduleName == null) {
                if (libraryPath != null) {
                    moduleName = libraryPath.getFileName().toString();
                } else {
                    moduleName = module.getName().toJavaString();
                }
            }
            Universe universe = new Universe(options, module, moduleName, classes, libraryPath, classPath);
            Map<String, Function> namesToFD = new HashMap<>();
            module.forEachFunction(function -> {
                if (function.hasExactDefinition()) {
                    String name = function.getName();
                    if (namesToFD.put(name, function) != null) {
                        Logger.warn("Duplicated function: " + name);
                    } else {
                        Logger.info("Find function: " + name);
                    }
                }
            });

            Path outputPath = options.getOutputPath();
            universe.lookupNativeMethods(namesToFD);
            universe.save(outputPath.toAbsolutePath());
            universe.dump();
        }
    }

    private static void exitAndPrintUsage() {
        usage(System.err);
        System.exit(1);
    }

    private static void exitAndPrintUsage(String errorMsg) {
        if (errorMsg != null) System.err.println(errorMsg);
        usage(System.err);
        System.exit(1);
    }

    private static void usage(PrintStream s) {
        s.println();
        s.println("Usage: llvm4jni  [-bc <bitcode-file>] -cp <class-path> -lib <library-path> [-output <output-path>]");
        s.println("                 [-mn <module-name>] [-v <logger-level>] [-no-verify]");
        s.println();
        s.println("Arguments:");
        s.println("  bitcode-file    Path for the input LLVM Bitcode file *.bc.");
        s.println("                  Use embedded bitcode in library if no option `-bc', ");
        s.println("  class-path      Class path for searching class files.");
        s.println("  output-path     Path for the output generated files, default is ./output");
        s.println("  library-path    Path for the input shared library with embedded bitcode.");
        s.println("  module-name     Name used to generate Library class and debug. Default value is the file name of ");
        s.println("                  the shared library otherwise the module name of the bitcode module.");
        s.println("  logger-level    DEBUG, INFO, WARN, ERROR");
        s.println("    -no-verify    Disable bytecode verification (e.g., when classpath is incomplete)");
    }
}
