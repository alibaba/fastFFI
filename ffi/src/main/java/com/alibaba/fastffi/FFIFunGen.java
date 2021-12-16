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

/**
 * Used to match a method in a given class.
 * The signature of a method is computed as the combination
 * of the name, the types of the parameters and the type of the return value.
 * The type of a parameter of a return value must be a type that is supported
 * by fastFFI, i.e., a Java boxed or unboxed primitive type, a type variable, and a FFIType
 * and its parameterized type
 * (e.g., CXXEnum or FFIPointer).
 * If the type is a generic type, e.g., (StdVector&lt;String&gt;), the configured
 * type name must be the raw type (apply type erasure to the type), e.g., StdVector.
 *
 * For example, the following method has included all possible types, and the canonical full name of each type.
 * <pre>{@code
 * import std.StdVector;
 * import std.StdString;
 * class Foo {
 *     StdVector<Integer> <T> get(T t, StdString str, StdVector<StdVector<Long>> in, int a, Float f);
  * }
 * }</pre>
 * And we can use the following annotation to match the method
 *
 * <pre>{@code
 *  @FFIFunGen{
 *      name = "get",
 *      parameterTypes = {"T", "std.StdString", "std.StdVector", "int", "java.lang.Float"},
 *      returnType = "std.StdVector",
 *      templates = {...}
 *  }
 * }</pre>
 * TODO: Add support of simple type names.
 */
public @interface FFIFunGen {
    String name();
    String[] parameterTypes();

    /**
     * Note that fastFFI does not support array types.
     * @return erased return type
     */
    String returnType();
    CXXTemplate[] templates() default {};
}
