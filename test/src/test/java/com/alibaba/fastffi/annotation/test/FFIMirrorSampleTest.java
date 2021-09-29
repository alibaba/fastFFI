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
package com.alibaba.fastffi.annotation.test;

import com.alibaba.fastffi.CXXValueScope;
import com.alibaba.fastffi.FFIByteString;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFIVector;
import com.alibaba.fastffi.impl.CXXStdString;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class FFIMirrorSampleTest {

    @Test
    public void testFFITypeFactory() throws Exception {
        {
            Class<?> clazz = FFITypeFactory.getType(FFIByteString.class, "std::string");
            assertTrue(CXXStdString.class.isAssignableFrom(clazz));
        }
        {
            Class<?> clazz = FFITypeFactory.getType(FFIVector.class, "std::vector<jint>");
            assertTrue(FFIVector.class.isAssignableFrom(clazz));
        }
        {
            try {
                FFITypeFactory.getType(FFIVector.class, "std::vector<NotAType>");
            } catch (IllegalArgumentException e) {
                assertTrue(e.getMessage().contains("FFIPointer"));
                assertTrue(e.getMessage().contains("std::vector<NotAType>"));
            }
        }
    }

    @Test
    public void testFFIMirrorSample() {
        FFIMirrorSample sample = FFIMirrorSample.create();
        Random random = new Random();
        {
            int expected = random.nextInt();
            sample.intField(expected);
            assertTrue(expected == sample.intField());
        }

        {
            FFIByteString string = sample.stringField();
            assertTrue(string.toString().equals(""));
            String expected = "阿里巴巴alibaba";
            string.copyFrom(expected);
            assertTrue(string.toString().equals(expected));
        }

        {
            FFIVector<Integer> vector = sample.intVectorField();
            assertTrue(vector.size() == 0);
            vector.resize(10);
            assertTrue(vector.size() == 10);
            for (int i = 0; i < 10; i++) {
                vector.set(i, i);
            }
            for (int i = 0; i < 10; i++) {
                assertTrue(vector.get(i) == i);
            }
        }

        {
            FFIVector<FFIVector<Integer>> vector = sample.intVectorVectorField();
            assertTrue(vector.size() == 0);
            vector.resize(1);
            assertTrue(vector.size() == 1);
            FFIVector<Integer> intVector = vector.get(0);
            assertTrue(intVector.size() == 0);
            intVector.resize(10);
            assertTrue(intVector.size() == 10);
            for (int i = 0; i < 10; i++) {
                intVector.set(i, i);
            }
            for (int i = 0; i < 10; i++) {
                assertTrue(intVector.get(i) == i);
            }
        }

        {
            FFIByteString string = FFITypeFactory.newByteString();;
            assertTrue(string.toString().equals(""));
            String expected = "阿里巴巴alibaba";
            string.copyFrom(expected);
            assertTrue(string.toString().equals(expected));
        }

        {
            FFIVector<Integer> vector = FFITypeFactory.newFFIVector(int.class);
            assertTrue(vector.size() == 0);
            vector.resize(10);
            assertTrue(vector.size() == 10);
            for (int i = 0; i < 10; i++) {
                vector.set(i, i);
            }
            for (int i = 0; i < 10; i++) {
                assertTrue(vector.get(i) == i);
            }
        }
    }

    @Test
    public void testFFIMirrorSampleWrapper() {
        FFIMirrorSampleWrapper sampleWrapper = FFIMirrorSampleWrapper.create();
        sampleWrapper.sampleVectorField();
        sampleWrapper.sampleVectorVectorField();
    }

    long runCHeapAlloc(int total) {
        long nativeCall = - System.nanoTime();
        for (int i = 0; i < total; i++) {
            FFIMirrorSample sample = FFIMirrorSample.create();
            int expected = 0xdeadbeef;
            sample.intField(expected);
            assertTrue(expected == sample.intField());
            sample.delete();
        }
        nativeCall += System.nanoTime();
        return nativeCall;
    }

    long runResourceAlloc(int total) {
        long stackCall = - System.nanoTime();
        try (CXXValueScope scope = new CXXValueScope()) {
            for (int i = 0; i < total; i++) {
                FFIMirrorSample sample = FFIMirrorSample.createStack();
                int expected = 0xdeadbeef;
                sample.intField(expected);
                assertTrue(expected == sample.intField());
            }
        }
        stackCall += System.nanoTime();
        return stackCall;
    }

    @Test
    public void testFFIMirrorSampleStack() {
        int total = 100_000;
        for (int i = 0; i < 100; i++) {
            runCHeapAlloc(total);
            runResourceAlloc(total);
        }
        long nativeCall = runCHeapAlloc(total);
        long stackCall = runResourceAlloc(total);
        System.out.format("CHeapAlloc: %s; ResourceAlloc: %s\n", TimeUnit.NANOSECONDS.toMillis(nativeCall),
                TimeUnit.NANOSECONDS.toMillis(stackCall));
    }
}
