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
package com.alibaba.fastffi;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestCXXValueScope {

    private void doAllocate() {
        for (int i = 0; i < 1024; i++) {
            long address = CXXValueScope.allocate(new Random().nextInt(2048) + 1);
            Assert.assertTrue(address > 0);
        }
    }

    @Test
    public void test1() {
        for (int i = 0; i < 1024; i++) {
            try (CXXValueScope scope1 = new CXXValueScope()) {
                doAllocate();
                try (CXXValueScope scope2 = new CXXValueScope()) {
                    doAllocate();
                }
            }

            try (CXXValueScope scope1 = new CXXValueScope()) {
                doAllocate();
                try (CXXValueScope scope2 = new CXXValueScope()) {
                    doAllocate();
                    try (CXXValueScope scope3 = new CXXValueScope()) {
                        try (CXXValueScope scope4 = new CXXValueScope()) {
                            doAllocate();
                        }
                    }
                }
            }
        }

        try (CXXValueScope scope = new CXXValueScope()) {
        }
        doAllocate();
    }

    @Test(expected = IllegalStateException.class)
    public void test2() {
        try (CXXValueScope scope1 = new CXXValueScope()) {
            doAllocate();
            try (CXXValueScope scope2 = new CXXValueScope()) {
                doAllocate();
                scope1.close();
            }
        }
    }

}
