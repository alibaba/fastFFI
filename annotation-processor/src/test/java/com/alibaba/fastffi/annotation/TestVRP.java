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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestVRP {

    @Test
    public void test1() {
        assertEquals("*reinterpret_cast<type*>(var)", VRP.Value.inNativeFromPointer("var", "type"));
        assertEquals("*reinterpret_cast<type*>(var)", VRP.Reference.inNativeFromPointer("var", "type"));
        assertEquals("reinterpret_cast<type*>(var)", VRP.Pointer.inNativeFromPointer("var", "type"));
    }

    @Test
    public void test2() {
        assertEquals("reinterpret_cast<jlong>(new type(var))", VRP.Value.outNativeAsPointer("var", "type"));
        assertEquals("reinterpret_cast<jlong>(&(var))", VRP.Reference.outNativeAsPointer("var", "type"));
        assertEquals("reinterpret_cast<jlong>(var)", VRP.Pointer.outNativeAsPointer("var", "type"));
    }

    @Test
    public void test3() {
        assertEquals("reinterpret_cast<jlong>(new((void*)rv_base) type(var))",
                     VRP.Value.outNativeAsPointer("var", "type", true));
        assertEquals("reinterpret_cast<jlong>(new type(var))", VRP.Value.outNativeAsPointer("var", "type", false));
        assertEquals("reinterpret_cast<jlong>(&(var))", VRP.Reference.outNativeAsPointer("var", "type", false));
        assertEquals("reinterpret_cast<jlong>(var)", VRP.Pointer.outNativeAsPointer("var", "type", false));
    }

}
