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

import com.alibaba.fastffi.llvm4jni.body.InputClass;
import com.alibaba.fastffi.llvm4jni.body.NativeMethod;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaClassCollector {

    private Path[] classPathArray;
    private Map<String, InputClass> results = new HashMap<>();

    public JavaClassCollector(Path[] classPathArray) {
        this.classPathArray = classPathArray;
    }

    public Map<String, InputClass> collect() {
        Arrays.stream(classPathArray).forEach(cp -> collect(cp));
        return results;
    }

    public void collect(Path classPath) {
        FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
            // Print information about
            // each type of file.
            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attr) {
                Logger.info("Process class file: " + file);
                if (attr.isRegularFile()) {
                    if (file.getFileName().toString().endsWith(".class")) {
                        try (InputStream input = Files.newInputStream(file)) {
                            ClassReader reader = new ClassReader(input);
                            ClassNode classNode = new ClassNode();
                            reader.accept(classNode, 0);
                            if (classNode.methods.isEmpty()) {
                                return FileVisitResult.CONTINUE;
                            }
                            List<MethodNode> nativeMethodNodes = new ArrayList<>();
                            for (MethodNode methodNode : classNode.methods) {
                                if (Modifier.isNative(methodNode.access)) {
                                    nativeMethodNodes.add(methodNode);
                                }
                            }
                            if (!nativeMethodNodes.isEmpty()) {
                                InputClass javaClass = new InputClass(classNode);
                                for (MethodNode methodNode : nativeMethodNodes) {
                                    NativeMethod nativeMethod = new NativeMethod(javaClass, methodNode);
                                    javaClass.addNativeMethod(nativeMethod);
                                }
                                if (results.put(javaClass.getName(), javaClass) != null) {
                                    throw new IllegalStateException("Duplicated class: " + results);
                                }
                            }
                        } catch (IOException e) {
                            if (Logger.debug()) e.printStackTrace();
                            Logger.warn("Fail to parse Java class file: " + file);
                        }

                    }
                }
                return FileVisitResult.CONTINUE;
            }
        };

        try {
            if (classPath.toString().endsWith(".jar")) {
                Map<String, String> env = new HashMap<>();
                env.put("create", "false");
                URI root = URI.create("jar:file:" + classPath.toAbsolutePath().toString());
                try (FileSystem fs = FileSystems.newFileSystem(root, env)) {
                    Path rootPath = fs.getPath("/");
                    Files.walkFileTree(rootPath, visitor);
                }
            } else {
                Files.walkFileTree(classPath, visitor);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot walk class path: " + classPath);
        }
    }
}
