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

@Retention(value=RUNTIME)
@Target({ElementType.TYPE})
public @interface FFIGen {
    String library() default "";

    /**
     * If this FFIGen is annotating a TypeElement,
     * the type() is ignored.
     * If this FFIGen is part of a FFIBatchGen,
     * the type() must be a valid name of TypeElement.
     * @return
     */
    String type() default "";

    /**
     * Arguments for the type template.
     * @return
     */
    CXXTemplate[] templates() default {};

    /**
     * function templates
     * @return
     */
    FFIFunGen[] functionTemplates() default {};
}
