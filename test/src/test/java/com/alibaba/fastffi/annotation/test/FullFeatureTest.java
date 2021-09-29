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
import com.alibaba.fastffi.FFITypeFactory;
import org.junit.Test;

import java.util.Random;

public class FullFeatureTest {

    @Test
    @SuppressWarnings("unchecked")
    public void doTest() {
        FullFeature.Factory<Integer, Float> factory =
            FFITypeFactory.getFactory(FullFeature.class,
                "FullFeature<int,float>");
        FullFeature<Integer, Float> feature = factory.create();

        Random rand = new Random();
        {
            byte excepted = (byte) rand.nextInt();
            Assert.assertTrue(excepted == feature.echo(excepted));
        }
        {
            short excepted = (short) rand.nextInt();
            Assert.assertTrue(excepted == feature.echo(excepted));
        }
        {
            boolean excepted = rand.nextBoolean();
            Assert.assertTrue(excepted == feature.echo(excepted));
        }
        {
            int excepted = rand.nextInt();
            Assert.assertTrue(excepted == feature.echo(excepted));
            feature.setField(excepted);
            Assert.assertTrue(excepted == feature.getField());
        }
        {
            int excepted = rand.nextInt();
            Assert.assertTrue(excepted == feature.echo(excepted));
            feature.setFieldExpr(excepted);
            Assert.assertTrue(excepted == feature.getField());
        }
        {
            long excepted = rand.nextLong();
            Assert.assertTrue(excepted == feature.echo(excepted));
            Assert.assertTrue(excepted == feature.echoExpr(excepted));
        }
        {
            float excepted = rand.nextFloat();
            Assert.assertTrue(excepted == feature.echo(excepted));
        }
        {
            double excepted = rand.nextDouble();
            Assert.assertTrue(excepted == feature.echo(excepted));
        }


        // TODO: disable exception now
        // Assert.assertException(feature::unknownException, RuntimeException.class, "Unknown exception occurred.");

        long start = System.currentTimeMillis();

        try (CXXValueScope scope = new CXXValueScope()) {
            for (int i = 0; i < 1000; i++) {
                feature.rvTest();
            }
        }
        feature.delete();
        System.out.println("elapsed: " + (System.currentTimeMillis() - start));
    }
}
