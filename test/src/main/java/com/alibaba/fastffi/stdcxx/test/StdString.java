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

import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.CXXValueRange;
import com.alibaba.fastffi.CXXValueRangeElement;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.StringProvider;
import com.alibaba.fastffi.StringReceiver;

@FFIGen(library = "ffitest")
@CXXHead(system = "string")
@FFITypeAlias("std::string")
public interface StdString extends CXXPointer, CXXValueRange<StdString.Iterator>, StringReceiver, StringProvider {

    Factory factory = FFITypeFactory.getFactory(StdString.class);

    @FFIFactory
    interface Factory {
        StdString create();
        default StdString create(@CXXValue String string) {
            StdString std = create();
            std.fromJavaString(string);
            return std;
        }
        StdString create(@CXXReference StdString string);
    }

    @FFIGen(library = "ffitest")
    @CXXHead(system = "string")
    @FFITypeAlias("std::string::iterator")
    interface Iterator extends CXXValueRangeElement<Iterator>, FFIPointer {

        @CXXOperator("*")
        byte indirection();

        @CXXOperator("*&")
        @CXXValue Iterator copy();

        @CXXOperator("++")
        @CXXReference Iterator inc();

        @CXXOperator(value = "==")
        boolean eq(@CXXReference Iterator rhs);
    }

    long size();

    long data();
    long c_str();

    void resize(long size);

	void clear();

    void push_back(byte c);

    /**
     * The actual String returns a reference but we can use a value.
     * @return
     */
    byte at(long index);

    @CXXValue Iterator begin();
    @CXXValue Iterator end();

    @CXXReference StdString append(@CXXReference StdString rhs);

    long find(@CXXReference StdString str, long pos);
    default long find(@CXXReference StdString str) {
        return find(str, 0);
    }

    long find(byte c, long pos);
    default long find(byte c) {
        return find(c, 0);
    }

    @CXXValue StdString substr(long pos, long len);
    default @CXXValue StdString substr(long pos) {
        // std::string::npos
        return substr(pos, -1L);
    }

    long find_first_of(@CXXReference StdString str, long pos);
    default long find_first_of(@CXXReference StdString str) {
        return find_first_of(str, 0);
    }

    long find_last_of(@CXXReference StdString str, long pos);
    default long find_last_of(@CXXReference StdString str) {
        return find_last_of(str, -1L);
    }

    long find_first_of(byte c, long pos);
    default long find_first_of(byte c) {
        return find_first_of(c, 0);
    }

    long find_last_of(byte c, long pos);
    default long find_last_of(byte c) {
        return find_last_of(c, -1L);
    }

    int compare (@CXXReference StdString str);
}
