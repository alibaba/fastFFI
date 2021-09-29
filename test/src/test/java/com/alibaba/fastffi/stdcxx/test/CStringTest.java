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
package com.alibaba.fastffi.stdcxx.test;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CStringTest {

    @Test
    public void testCString() throws Exception {
        StdString.Factory factory = StdString.factory;

        String javaString1 = "abcd";
        StdString stdString1 = factory.create(javaString1);
        String javaString2 = javaString1;
        StdString stdString2 = factory.create(javaString2);
        String javaString3 = "abcde";
        StdString stdString3 = factory.create(javaString3);
        String javaString4 = "abc";
        StdString stdString4 = factory.create(javaString4);

        assertTrue(stdString1.compare(stdString2) == javaString1.compareTo(javaString2));
        assertTrue(stdString1.compare(stdString3) == javaString1.compareTo(javaString3));
        assertTrue(stdString1.compare(stdString4) == javaString1.compareTo(javaString4));

        assertTrue(normalize(stdString1.compare(stdString2)) == normalize(CString.LIBRARY.strcmp(stdString1.c_str(), stdString2.c_str())));
        assertTrue(normalize(stdString1.compare(stdString3)) == normalize(CString.LIBRARY.strcmp(stdString1.c_str(), stdString3.c_str())));
        assertTrue(normalize(stdString1.compare(stdString4)) == normalize(CString.LIBRARY.strcmp(stdString1.c_str(), stdString4.c_str())));

        stdString1.delete();
        stdString2.delete();
        stdString3.delete();
        stdString4.delete();
    }

    static int normalize(int v) {
        return v == 0 ? 0 : ( v > 0 ? 1 : -1);
    }
}
