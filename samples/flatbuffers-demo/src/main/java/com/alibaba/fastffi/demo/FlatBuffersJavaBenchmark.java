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

import MyGame.Sample.Color;
import MyGame.Sample.Equipment;
import MyGame.Sample.Monster;
import MyGame.Sample.Vec3;
import MyGame.Sample.Weapon;
import com.alibaba.fastffi.demo.ffi.FFIColor;
import com.alibaba.fastffi.demo.ffi.FFIEquipment;
import com.alibaba.fastffi.demo.ffi.FFIFBVector;
import com.alibaba.fastffi.demo.ffi.FFIMonster;
import com.alibaba.fastffi.demo.ffi.FFIWeapon;
import com.google.flatbuffers.FlatBufferBuilder;
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
import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;


@Fork(value = 1)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
public class FlatBuffersJavaBenchmark {

    @State(Scope.Thread)
    public static class BMState {
        @Setup(Level.Trial)
        public void Setup() {
            FlatBufferBuilder builder = FlatBuffersMonsterBuilder.newBuilder();
            buf = builder.dataBuffer();
            directBuffer = ByteBuffer.allocateDirect(buf.capacity()).put(buf.duplicate());
            rawBuf = ((DirectBuffer) directBuffer).address();
        }

        ByteBuffer buf;
        ByteBuffer directBuffer;
        long rawBuf;
    }

    static void assertTrue(boolean value) {
        if (!value) {
            throw new IllegalStateException();
        }
    }

    @Benchmark
    public void testReadFFI(BMState state) {
        // Get access to the root:
        FFIMonster monster = FFIMonster.getMonster(state.rawBuf);

        // Note: We did not set the `mana` field explicitly, so we get back the default value.
        assertTrue(monster.mana() == (short) 150);
        assertTrue(monster.hp() == (short) 300);
        assertTrue(monster.name().toJavaString().equals("Orc"));
        assertTrue(monster.color() == FFIColor.Color_Red);
        assertTrue(monster.pos().x() == 1.0f);
        assertTrue(monster.pos().y() == 2.0f);
        assertTrue(monster.pos().z() == 3.0f);

        {
            FFIFBVector<Byte> inventory = monster.inventory();
            long size = inventory.size();
            // Get and test the `inventory` FlatBuffer `vector`.
            for (long i = 0; i < size; i++) {
                assertTrue(inventory.get(i) == (byte) i);
            }
        }

        {
            // Get and test the `weapons` FlatBuffer `vector` of `table`s.
            String[] expectedWeaponNames = {"Sword", "Axe"};
            int[] expectedWeaponDamages = {3, 5};
            FFIFBVector<FFIWeapon> weapons = monster.weapons();
            long size = weapons.size();
            for (long i = 0; i < size; i++) {
                assertTrue(weapons.get(i).name().toJavaString().equals(expectedWeaponNames[(int) i]));
                assertTrue(weapons.get(i).damage() == expectedWeaponDamages[(int) i]);
            }
        }
//        Weapon.Vector weaponsVector = monster.weaponsVector();
//        for (int i = 0; i < weaponsVector.length(); i++) {
//            assertTrue(weaponsVector.get(i).name().equals(expectedWeaponNames[i]));
//            assertTrue(weaponsVector.get(i).damage() == expectedWeaponDamages[i]);
//        }

        // Get and test the `equipped` FlatBuffer `union`.
        assertTrue(monster.equipped_type() == FFIEquipment.Equipment_Weapon);
        FFIWeapon equipped = monster.equipped_as_Weapon();
        assertTrue(equipped.name().toJavaString().equals("Axe"));
        assertTrue(equipped.damage() == 5);
    }

    @Benchmark
    public void testReadJava(BMState state) {
        // Get access to the root:
        Monster monster = Monster.getRootAsMonster(state.buf);

        // Note: We did not set the `mana` field explicitly, so we get back the default value.
        assertTrue(monster.mana() == (short)150);
        assertTrue(monster.hp() == (short)300);
        assertTrue(monster.name().equals("Orc"));
        assertTrue(monster.color() == Color.Red);
        assertTrue(monster.pos().x() == 1.0f);
        assertTrue(monster.pos().y() == 2.0f);
        assertTrue(monster.pos().z() == 3.0f);

        // Get and test the `inventory` FlatBuffer `vector`.
        for (int i = 0; i < monster.inventoryLength(); i++) {
            assertTrue(monster.inventory(i) == (byte)i);
        }

        // Get and test the `weapons` FlatBuffer `vector` of `table`s.
        String[] expectedWeaponNames = {"Sword", "Axe"};
        int[] expectedWeaponDamages = {3, 5};
        for (int i = 0; i < monster.weaponsLength(); i++) {
            assertTrue(monster.weapons(i).name().equals(expectedWeaponNames[i]));
            assertTrue(monster.weapons(i).damage() == expectedWeaponDamages[i]);
        }

//        Weapon.Vector weaponsVector = monster.weaponsVector();
//        for (int i = 0; i < weaponsVector.length(); i++) {
//            assertTrue(weaponsVector.get(i).name().equals(expectedWeaponNames[i]));
//            assertTrue(weaponsVector.get(i).damage() == expectedWeaponDamages[i]);
//        }

        // Get and test the `equipped` FlatBuffer `union`.
        assertTrue(monster.equippedType() == Equipment.Weapon);
        Weapon equipped = (Weapon)monster.equipped(new Weapon());
        assertTrue(equipped.name().equals("Axe"));
        assertTrue(equipped.damage() == 5);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FlatBuffersJavaBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
