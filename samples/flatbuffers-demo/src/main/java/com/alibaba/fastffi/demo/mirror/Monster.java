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
import com.alibaba.fastffi.FFIByteString;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFIMirror;
import com.alibaba.fastffi.FFINameAlias;
import com.alibaba.fastffi.FFINameSpace;
import com.alibaba.fastffi.FFISetter;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFIVector;

@FFIGen
@FFIMirror
@FFITypeAlias("Monster")
@FFINameSpace("FFIMirror::MyGame::Sample")
public interface Monster extends CXXPointer {
    /**
     * struct Monster {
     *   Vec3 pos;
     *   short mana = 150;
     *   short hp = 100;
     *   std::string name;
     *   bool friendly = false;
     *   std::vector<char> inventory;
     *   Color color = Color::Blue;
     *   std::vector<Weapon> weapons;
     *   Equipment equipped;
     *   std::vector<Vec3> path;
     * };
     */

    Factory factory = FFITypeFactory.getFactory(Monster.class);

    @FFIFactory
    interface Factory {
        Monster create();
    }

    @FFISetter
    @FFINameAlias("pos")
    void setPos(@CXXReference Vec3 pos);
    @FFIGetter
    @FFINameAlias("pos")
    @CXXReference Vec3 getPos();

    @FFISetter
    @FFINameAlias("mana")
    void setMana(short mana);
    @FFIGetter
    @FFINameAlias("mana")
    short getMana();

    @FFISetter
    @FFINameAlias("hp")
    void setHp(short hp);
    @FFIGetter
    @FFINameAlias("hp")
    short getHp();

    @FFISetter
    @FFINameAlias("name")
    void setName(@CXXReference FFIByteString name);
    @FFIGetter
    @FFINameAlias("name")
    @CXXReference FFIByteString getName();

    @FFISetter
    @FFINameAlias("friendly")
    void setFriendly(boolean friendly);
    @FFIGetter
    @FFINameAlias("friendly")
    boolean getFriendly();

    @FFISetter
    @FFINameAlias("color")
    void setColor(int color);
    @FFIGetter
    @FFINameAlias("color")
    int getColor();

    @FFIGetter
    @FFINameAlias("inventory")
    @CXXReference FFIVector<Byte> getInventory();

    @FFIGetter
    @FFINameAlias("weapons")
    @CXXReference FFIVector<Weapon> getWeapons();

    @FFIGetter
    @FFINameAlias("path")
    @CXXReference FFIVector<Vec3> getPath();

    @FFISetter
    @FFINameAlias("equipped_type")
    void setEquippedType(int type);
    @FFIGetter
    @FFINameAlias("equipped_type")
    int getEquippedType();

    @FFIGetter
    @FFINameAlias("equipped")
    @CXXReference Equipment getEquipment();
}
