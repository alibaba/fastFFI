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
 * At least one of value and namespace must be non-empty.
 */
@Retention(value=RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FFILibrary {
    /**
     * A unique type registry ID used to loop up the generated library class
     * default value is the value of namespace
     * @return
     */
    String value() default "";

    /**
     * The namespace used to access members of the FFILibrary
     * default value is empty;
     * @return
     */
    String namespace() default "";
}
