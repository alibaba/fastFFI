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
package com.alibaba.fastffi.llvm4jni.body;

import com.alibaba.fastffi.llvm4jni.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ClassBody {

    public final ClassNode classNode;

    public ClassBody(ClassNode classNode) {
        this.classNode = classNode;
    }

    public String getName() {
        return classNode.name;
    }

    public String getDescriptor() {
        return "L" + classNode.name + ";";
    }

    public abstract boolean needsSaving();

    public void save(Path root) throws IOException {
        String classFileName = classNode.name + ".class";
        String[] segments = classFileName.split("/");
        Path filePath = root;
        for (String seg : segments) {
            filePath = filePath.resolve(seg);
        }
        // Path filePath = root.resolve(Paths.get());
        Path directory = filePath.getParent();
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        Logger.info("Save class " + classNode.name + " into " + filePath.toString());
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        try (OutputStream output = Files.newOutputStream(filePath)) {
            output.write(writer.toByteArray());
        }
    }
}
