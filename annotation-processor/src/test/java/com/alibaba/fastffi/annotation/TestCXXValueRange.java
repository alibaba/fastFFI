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

import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.CXXValueRange;
import com.alibaba.fastffi.CXXValueRangeElement;
import org.junit.Test;

public class TestCXXValueRange {

    @Test
    public void test() {
        CXXValueRange<Element> elements = new CXXValueRange<Element>() {

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

        elements = new CXXValueRange<Element>() {

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

    static class Element implements CXXValueRangeElement<Element> {

        private long addr;

        public Element(long addr) {
            this.addr = addr;
        }

        @Override
        public @CXXValue Element copy() {
            return new Element(addr);
        }

        @Override
        public @CXXReference Element inc() {
            addr += 1;
            return this;
        }

        @Override
        public boolean eq(@CXXReference Element element) {
            return this.addr == element.addr;
        }

        @Override
        public long getAddress() {
            return addr;
        }
    }
}
