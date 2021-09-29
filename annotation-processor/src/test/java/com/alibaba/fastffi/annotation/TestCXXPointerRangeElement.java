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

import com.alibaba.fastffi.CXXPointerRangeElement;
import org.junit.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;

public class TestCXXPointerRangeElement extends TestBase {
    @Test
    public void test1() {
        assertThat(compile(this.getClass())).succeeded();
    }

    @Test
    public void test2() {
        Element element = new Element();
        element.next();
        element.nextV();
    }

    static class Element implements CXXPointerRangeElement<Element> {

        @Override
        public Element add(long delta) {
            return null;
        }

        @Override
        public Element moveTo(long delta) {
            return null;
        }

        @Override
        public void addV(long delta) {

        }

        @Override
        public void moveToV(long delta) {

        }

        @Override
        public long elementSize() {
            return 0;
        }

        @Override
        public long getAddress() {
            return 0;
        }

        @Override
        public void setAddress(long address) {

        }
    }
}
