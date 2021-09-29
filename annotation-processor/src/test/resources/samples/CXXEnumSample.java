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
import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;

@FFITypeAlias("CXXEnumSample")
public enum CXXEnumSample implements CXXEnum {

    A(Library.INSTANCE.A()), B(Library.INSTANCE.B()), C(Library.INSTANCE.C());

    public static CXXEnumMap<CXXEnumSample> map = new CXXEnumMap<>(values());

    private int value;

    private CXXEnumSample(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @FFIGen(library = "CXXEnumSample")
    @CXXHead("CXXEnumSample.h")
    @FFILibrary(value = "CXXEnumSample")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);

        @FFIGetter
        int A();

        @FFIGetter
        int B();

        @FFIGetter
        int C();

        void test(@CXXValue CXXEnumSample v, char a);
    }
}