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

/**
 * Value, Reference, or Pointer;
 */
public enum VRP {
    Value,
    Reference,
    Pointer,
    ;

    public String inNativeFromPointer(String varName, String typeName) {
        switch (this) {
            case Value:
            case Reference:
                return String.format("*reinterpret_cast<%s*>(%s)", typeName, varName);
            case Pointer:
                return String.format("reinterpret_cast<%s*>(%s)", typeName, varName);
            default:
                throw new IllegalStateException();
        }
    }

    public String outNativeAsPointer(String varName, String typeName) {
        return outNativeAsPointer(varName, typeName, false);
    }

    public String outNativeAsPointer(String varName, String typeName, boolean opto) {
        switch (this) {
            case Value:
                if (opto) {
                    return String.format("reinterpret_cast<jlong>(new((void*)rv_base) %s(%s))", typeName, varName);
                }
                return String.format("reinterpret_cast<jlong>(new %s(%s))", typeName, varName);
            case Reference:
                return String.format("reinterpret_cast<jlong>(&(%s))", varName);
            case Pointer:
                return String.format("reinterpret_cast<jlong>(%s)", varName);
            default:
                throw new IllegalStateException();
        }
    }
}
