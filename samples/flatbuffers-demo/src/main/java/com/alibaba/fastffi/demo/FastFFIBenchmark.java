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
package com.alibaba.fastffi.demo;

import com.alibaba.fastffi.FFIByteString;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFIUnsafe;
import com.alibaba.fastffi.FFIVector;
import com.alibaba.fastffi.demo.mirror.Color;
import com.alibaba.fastffi.demo.mirror.EquipmentType;
import com.alibaba.fastffi.demo.mirror.Monster;
import com.alibaba.fastffi.demo.mirror.Vec3;
import com.alibaba.fastffi.demo.mirror.Weapon;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import sun.misc.Unsafe;

import java.util.concurrent.TimeUnit;

@Fork(value = 1)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
public class FastFFIBenchmark {

    static FFIByteString.Factory byteStringFactory = FFITypeFactory.getFFIByteStringFactory();

    static FFIByteString createFrom(String from) {
        FFIByteString str = byteStringFactory.create();
        str.copyFrom(from);
        return str;
    }

    @State(Scope.Thread)
    public static class BMState {
        @Setup(Level.Trial)
        public void Setup() {
            Monster monster = Monster.factory.create();
            FFIByteString weaponOneName = createFrom("Sword");
            short weaponOneDamage = 3;
            FFIByteString weaponTwoName = createFrom("Axe");
            short weaponTwoDamage = 5;

            Weapon weaponOne = Weapon.factory.create();
            weaponOne.setName(weaponOneName);
            weaponOne.setDamage(weaponOneDamage);
            Weapon weaponTwo = Weapon.factory.create();
            weaponTwo.setName(weaponTwoName);
            weaponTwo.setDamage(weaponTwoDamage);
            FFIVector<Weapon> weapons = monster.getWeapons();
            weapons.push_back(weaponOne);
            weapons.push_back(weaponTwo);

            FFIByteString name = createFrom("Orc");
            monster.setName(name);

            FFIVector<Byte> inventory = monster.getInventory();
            inventory.resize(10);
            byte[] treasure = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
            FFIUnsafe.U.copyMemory(treasure, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, inventory.data(), Unsafe.ARRAY_BYTE_INDEX_SCALE * treasure.length);

            Vec3 pos = Vec3.factory.create();
            pos.setX(1.0f);
            pos.setY(2.0f);
            pos.setZ(3.0f);

            monster.setMana((short) 150);
            monster.setPos(pos);
            monster.setColor(Color.Red.ordinal());
            monster.setHp((short) 300);
            monster.setEquippedType(EquipmentType.Weapon.ordinal());
            monster.getEquipment().setWeapon(weaponTwo);

            this.monster = monster;
        }

        Monster monster;
    }

    static void assertTrue(boolean value) {
        if (!value) {
            throw new IllegalStateException();
        }
    }

    @Benchmark
    public void testReadFFIMirror(BMState state) {
        // Get access to the root:
        Monster monster = state.monster;

        // Note: We did not set the `mana` field explicitly, so we get back the default value.
        assertTrue(monster.getMana() == (short)150);
        assertTrue(monster.getHp() == (short)300);
        assertTrue(monster.getName().toString().equals("Orc"));
        assertTrue(monster.getColor() == Color.Red.ordinal());
        assertTrue(monster.getPos().getX() == 1.0f);
        assertTrue(monster.getPos().getY() == 2.0f);
        assertTrue(monster.getPos().getZ() == 3.0f);


        {
            // Get and test the `inventory` FlatBuffer `vector`.
            FFIVector<Byte> inventory = monster.getInventory();
            int size = Math.toIntExact(inventory.size());
            for (int i = 0; i < size; i++) {
                assertTrue(inventory.get(i) == (byte) i);
            }
        }
        {
            // Get and test the `weapons` FlatBuffer `vector` of `table`s.
            String[] expectedWeaponNames = {"Sword", "Axe"};
            int[] expectedWeaponDamages = {3, 5};
            FFIVector<Weapon> weapons = monster.getWeapons();
            int size = Math.toIntExact(weapons.size());
            for (int i = 0; i < size; i++) {
                Weapon weapon = weapons.get(i);
                assertTrue(weapon.getName().toString().equals(expectedWeaponNames[i]));
                assertTrue(weapon.getDamage() == expectedWeaponDamages[i]);
            }
        }

//        // Get and test the `equipped` FlatBuffer `union`.
        assertTrue(monster.getEquippedType() == EquipmentType.Weapon.ordinal());
        Weapon equipped = monster.getEquipment().getWeapon();
        assertTrue(equipped.getName().toString().equals("Axe"));
        assertTrue(equipped.getDamage() == 5);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FastFFIBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
