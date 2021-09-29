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

public class ExecutableTypeMapping {
    private final TypeMapping returnTypeMapping;
    private final TypeMapping[] parametersTypeMapping;

    public ExecutableTypeMapping(TypeMapping returnTypeMapping, TypeMapping[] parametersTypeMapping) {
        this.returnTypeMapping = returnTypeMapping;
        this.parametersTypeMapping = parametersTypeMapping;
    }

    public TypeMapping getReturnTypeMapping() {
        return returnTypeMapping;
    }

    public TypeMapping getParameterTypeMapping(int index) {
        return parametersTypeMapping[index];
    }

    public int getNumberOfParameterTypes() {
        return parametersTypeMapping.length;
    }
}
