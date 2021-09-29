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
package com.alibaba.fastffi.llvm4jni;

import com.kenai.jffi.Library;
import org.junit.Assert;
import org.junit.Test;

public class LibraryLoadTest {

    @Test
    public void testLoad() {
        String libraryName = "llvm4jni-test";
        libraryName = System.mapLibraryName(libraryName);
        Library library = Library.getCachedInstance(libraryName, Library.LAZY | Library.GLOBAL);
        Assert.assertNotNull("Error: " + Library.getLastError(), library);

        {
            long address = library.getSymbolAddress("_ZTIN8llvm4jni16VTableTestStructE");
            Assert.assertTrue(address != 0);
        }
        {
            long address = library.getSymbolAddress("_Znwm");
            Assert.assertTrue(address != 0);
        }
    }
}
