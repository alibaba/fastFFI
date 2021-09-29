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
#ifndef FFITEST_H
#define FFITEST_H

namespace test {

template <typename M>
class TestClassFunctionTemplate {

public:
    template <typename T>
    T getValueNoSuffix(M m, T t) {
        return m + t;
    }

    template <typename T>
    T getValueSkip(M m) {
        return (T) m;
    }
};

class TestFunctionTemplate {

public:
    template <typename T>
    T getValueNoSuffix(T t) {
        return t + t;
    }

    template <typename T>
    T getValueSkip() {
        return (T) 16; // a small integer that can fit 8-bit
    }
};

template<typename T>
class TestTypeMappingGenericSuper {
private:
    T _value;
public:
    TestTypeMappingGenericSuper(T value) : _value(value) {}
    T getValue() {
        return _value;
    }
    void setValue(T value) {
        _value = value;
    }
};

class TestTypeMapping : public TestTypeMappingGenericSuper<TestTypeMappingGenericSuper<int>> {
public:
    TestTypeMapping(int value) : TestTypeMappingGenericSuper<TestTypeMappingGenericSuper<int>>(TestTypeMappingGenericSuper<int>(value)) {}
    TestTypeMappingGenericSuper<char> getCharValue() {
        return TestTypeMappingGenericSuper<char>((char)getValue().getValue());
    }
};

}

#endif