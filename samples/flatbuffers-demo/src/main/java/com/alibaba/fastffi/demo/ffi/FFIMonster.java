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
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;

@FFIGen
@FFITypeAlias("MyGame::Sample::Monster")
@CXXHead("monster_generated.h")
public interface FFIMonster extends FFIPointer {

    static FFIMonster getMonster(long buf) {
        return Library.INSTANCE.GetMonster(buf);
    }

    @FFIGen
    @FFILibrary(value = "MyGame::Sample::Monster", namespace = "MyGame::Sample")
    @CXXHead("monster_generated.h")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);
        FFIMonster GetMonster(@FFITypeAlias("const void*") long buf);
    }

    FFIVec3 pos();
    short mana();
    short hp();

    FFIFBString name();

    @FFITypeAlias("flatbuffers::Vector<uint8_t>")
    FFIFBVector<Byte> inventory();

    FFIColor color();

    @FFITypeAlias("flatbuffers::Vector<flatbuffers::Offset<MyGame::Sample::Weapon>>")
    FFIFBVector<FFIWeapon> weapons();

    FFIEquipment equipped_type();

    FFIWeapon equipped_as_Weapon();

    @FFITypeAlias("flatbuffers::Vector<MyGame::Sample::Vec3*>")
    FFIFBVector<FFIVec3> path();
}
