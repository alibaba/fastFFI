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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <ol>
 * <li>If the annotated type is a FFIPointer:
 * <ol>
 *  <li>As a parameter: The pointer will be used as a reference (p -> *p) in generated code</li>
 *  <li>As a return value: The reference will be used as a pointer (p -> &p) in generated code</li>
 * <ol/>
 * </li>
 * <li>If the annotated type is a FFIMirror:
 * <ol>
 *     <li>As a parameter: The mirror value will be used as a reference. The state will be updated accordingly. If no state is needed, use a CXXValue instead.</li>
 *     <li>As a return value: The reference will be used to copy value to a mirror.</li>
 * </ol>
 * </li>
 * <li>Primitives are used the same as the FFIMirror except their values are not updated.</li>
 * </ol>
 */
@Retention(value=RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.METHOD, ElementType.PARAMETER})
public @interface CXXReference {
}
