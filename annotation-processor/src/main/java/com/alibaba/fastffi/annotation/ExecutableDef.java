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
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

public class ExecutableDef {

    private final TypeElement enclosingElement; // i.e., the element that declares
    private final ExecutableElement executableElement;
    private final TypeMirror declaringType;
    private final ExecutableType executableType;

    public ExecutableDef(TypeElement enclosingElement, ExecutableElement executableElement, TypeMirror declaringType, TypeMirror executableType) {
        this.enclosingElement = enclosingElement;
        this.executableElement = executableElement;
        this.declaringType = declaringType;
        this.executableType = (ExecutableType) executableType;
    }

    public TypeElement getEnclosingElement() {
        return enclosingElement;
    }

    public ExecutableElement getExecutableElement() {
        return executableElement;
    }

    public ExecutableType getExecutableType() {
        return executableType;
    }

    public String toString() {
        return "" + declaringType + "." + executableElement.getSimpleName() + executableType;
    }
}
