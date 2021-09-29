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


import org.objectweb.asm.Type;

public interface TypeDef {
    /**
     * @see Class#getName()
     * @return
     */
    String getName();

    /**
     * @see Type#getDescriptor()
     * @return
     */
    String getDescriptor();
    /**
     * @see Type#getInternalName()
     * @return
     */
    String getInternalName();
    TypeDef getSuperType();
    boolean isAssignableFrom(TypeDef def);

    MethodTypeDef getMethodTypeDef(String name, String descriptor);
    FieldTypeDef getFieldTypeDef(String name, String descriptor);

    TypeDef getArrayType();

    TypeDef getComponentTypeDef();

    boolean isAbstract();
    boolean isInterface();

    boolean isPrimitive();
    boolean isArray();

    boolean isExact();

    /**
     * Ignore isExact
     * @param typeDef
     * @return
     */
    boolean isSameType(TypeDef typeDef);
    /**
     * return itself if this is an exact type
     * @return
     */
    TypeDef getExact();
}
