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

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen
@FFITypeAlias("flatbuffers::Vector")
@CXXHead("monster_generated.h")
@CXXTemplate(cxx = "uint8_t", java = "Byte")
@CXXTemplate(cxx = "MyGame::Sample::Weapon", cxxFull = "flatbuffers::Offset<MyGame::Sample::Weapon>", java = "FFIWeapon")
@CXXTemplate(cxx = "MyGame::Sample::Vec3", cxxFull = "MyGame::Sample::Vec3*", java = "FFIVec3")
public interface FFIFBVector<T> extends FFIPointer {

    long size();

    @FFINameAlias("Get")
    T get(long idx);

    @FFINameAlias("Data")
    long data();
}
