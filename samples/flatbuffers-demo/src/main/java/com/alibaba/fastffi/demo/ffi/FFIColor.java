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

package com.alibaba.fastffi.demo.ffi;

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeRefiner;

@FFITypeAlias("MyGame::Sample::Color")
@FFITypeRefiner("FFIColor.get")
public enum FFIColor implements CXXEnum {
    Color_Red,
    Color_Green,
    Color_Blue;

    @Override
    public int getValue() {
        return ordinal();
    }

    public static FFIColor get(int value) {
        switch (value) {
            case 0:
                return Color_Red;
            case 1:
                return Color_Green;
            case 2:
                return Color_Blue;
            default:
                throw new IllegalArgumentException("Oops: unknown value: " + value);
        }    }
}
