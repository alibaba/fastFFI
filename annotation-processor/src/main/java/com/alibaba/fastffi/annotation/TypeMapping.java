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
package com.alibaba.fastffi.annotation;

import javax.lang.model.type.TypeMirror;

/**
 * A mapping of native type and Java type
 * @see TypeEnv#getTypeDefByForeignName
 */
public class TypeMapping {
    /**
     * The CXX type name, e.g., vector&lt;int&gt;
     */
    public final String cxx;
    /**
     * The Java type name, e.g., Vector&lt;Integer&gt;
     * Use TypeRegistry to query the implementation type of the cxx type;
     */
    public final TypeMirror java;

    /**
     *
     * @param cxx
     * @param java
     */
    public TypeMapping(String cxx, TypeMirror java) {
        this.cxx = cxx;
        this.java = java;
    }

    public String toString() {
        return String.format("%s -> %s", cxx, java);
    }

}
