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

@FFITypeAlias("MyGame::Sample::Equipment")
@FFITypeRefiner("FFIEquipment.get")
public enum FFIEquipment implements CXXEnum {
    Equipment_NONE,
    Equipment_Weapon;

    @Override
    public int getValue() {
        return ordinal();
    }

    public static FFIEquipment get(int value) {
        switch (value) {
            case 0:
                return Equipment_NONE;
            case 1:
                return Equipment_Weapon;
            default:
                throw new IllegalArgumentException("Oops: unknown value: " + value);
        }
    }
}
