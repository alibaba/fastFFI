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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

public class FFICodeGenerationException extends RuntimeException {

    protected TypeDef typeDef;
    protected ExecutableElement executableElement;
    protected ExecutableType executableType;

    public FFICodeGenerationException(String message) {
        super(message);
    }

    public FFICodeGenerationException(String message, Throwable cause, TypeDef typeDef) {
        super(message, cause);
        this.typeDef = typeDef;
    }
}
