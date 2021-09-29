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

import com.alibaba.fastffi.CXXPointerRange;
import com.alibaba.fastffi.CXXPointerRangeElement;
import org.junit.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;

public class TestCXXPointerRange extends TestBase {
    @Test
    public void test1() {
        assertThat(compile(this.getClass())).succeeded();
    }

    @Test
    public void test2() {
        CXXPointerRange<Element> elements = new CXXPointerRange<Element>() {

            @Override
            public Element begin() {
                return new Element(1);
            }

            @Override
            public Element end() {
                return new Element(2);
            }
        };

        for (Element element : elements) {
            System.out.println(element);
        }

        elements = new CXXPointerRange<Element>() {

            @Override
            public Element begin() {
                return new Element(1);
            }

            @Override
            public Element end() {
                return new Element(2);
            }
        };

        Iterable<Element> locals = elements.locals();
        for (Element local : locals) {
            System.out.println(local);
        }
    }

    static class Element implements CXXPointerRangeElement<Element> {

        private final long size;

        public Element(long size) {
            this.size = size;
        }

        @Override
        public TestCXXPointerRange.Element add(long delta) {
            return null;
        }

        @Override
        public TestCXXPointerRange.Element moveTo(long delta) {
            return this;
        }

        @Override
        public void addV(long delta) {

        }

        @Override
        public void moveToV(long delta) {
        }

        @Override
        public long elementSize() {
            return 1;
        }

        @Override
        public long getAddress() {
            return size;
        }

        @Override
        public void setAddress(long address) {

        }
    }

}
