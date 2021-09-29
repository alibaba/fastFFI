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

import org.objectweb.asm.Type;

public enum JavaKind {
    Void,
    Boolean,
    Byte,
    Short,
    Character,
    Integer,
    Float,
    Long,
    Double,
    Object,
    Array,
    Illegal;

    static JavaKind fromAsmType(Type type) {
        switch (type.getSort()) {
            case Type.VOID:
                return JavaKind.Void;
            case Type.BOOLEAN:
                return JavaKind.Boolean;
            case Type.BYTE:
                return JavaKind.Byte;
            case Type.SHORT:
                return JavaKind.Short;
            case Type.CHAR:
                return JavaKind.Character;
            case Type.INT:
                return JavaKind.Integer;
            case Type.FLOAT:
                return JavaKind.Float;
            case Type.LONG:
                return JavaKind.Long;
            case Type.DOUBLE:
                return JavaKind.Double;
            case Type.OBJECT:
                return JavaKind.Object;
            case Type.ARRAY:
                return JavaKind.Array;
            default:
                throw new IllegalArgumentException("Unknow sort of type: " + type);
        }
    }

    /**
     * @return
     */
    public JavaKind onStackKind() {
        switch (this) {
            case Void:
            case Illegal:
                throw new IllegalStateException("No valid on-stack kind of " + this);
            case Boolean:
            case Byte:
            case Short:
            case Character:
                return Integer;
            case Array:
                return Object;
            default:
                return this;
        }
    }
}
