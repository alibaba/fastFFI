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
package com.alibaba.fastffi.llvm4jni.type;

import com.alibaba.fastffi.llvm4jni.type.impl.TypeDefFactoryImpl;

import java.nio.file.Path;

public interface TypeDefFactory {

    static TypeDefFactory create(Path[] classpath) {
        return TypeDefFactoryImpl.create(classpath);
    }

    /**
     * The same format as JNI function findClass
     * @param descriptor
     * @return
     */
    TypeDef getTypeDef(String descriptor);

    TypeDef getJavaLangObject();
    TypeDef getJavaLangString();
    TypeDef getJavaLangClass();
    TypeDef getJavaLangThrowable();
}
