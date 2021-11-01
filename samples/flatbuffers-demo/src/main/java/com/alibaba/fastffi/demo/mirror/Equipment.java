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

package com.alibaba.fastffi.demo.mirror;

import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFIMirror;
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFINameSpace;
import com.alibaba.fastffi.FFISetter;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;

@FFIGen
@FFIMirror
@FFITypeAlias("Equipment")
@FFINameSpace("FFIMirror::MyGame::Sample")
public interface Equipment extends CXXPointer {

    Factory factory = FFITypeFactory.getFactory(Equipment.class);

    @FFIFactory
    interface Factory {
        Equipment create();
    }

    @FFISetter
    @FFINameAlias("weapon")
    void setWeapon(@CXXReference Weapon weapon);
    @FFIGetter
    @FFINameAlias("weapon")
    @CXXReference Weapon getWeapon();
}
