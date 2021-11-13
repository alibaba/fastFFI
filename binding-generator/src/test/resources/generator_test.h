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
#ifndef GENERATOR_TEST_HPP
#define GENERATOR_TEST_HPP

#include <string>

using namespace std;
namespace SimpleNamespace {

typedef int Integer;
typedef int int2;
typedef int2 int3;

struct SimpleStruct {
    int i;
    int3 int3;
    std::string s;
    char* charPointer;
    char** charPointerPointer;
    string* stringPointer;
    string** stringPointerPointer;
};

enum SimpleEnum {
    _none,
    _succ,
    _fail
};

class SimpleClass {
private:
    void private_simple_method();
    void private_simple_method_int(int i);
    void private_simple_method_int(int &i);
    void private_simple_method_integer(Integer i);
public:
    enum SimpleInnerEnum {
        _e1,
        _e2,
        _e3
    };
    SimpleClass() {}
    void public_simple_method();
    void public_simple_method_int(int i);
    void public_simple_method_int(int &i);
    void public_simple_method_integer(Integer i);
};

typedef SimpleClass SimpleTypeDef;
typedef SimpleClass* SimpleTypeDefPointer;

template<typename T>
class TemplateClass {
    using iterator = typename T::iterator;
    using V = T;
    using VV = V;
private:
    T _value;
public:
    TemplateClass(T& t) : _value(t) {}
    T getValue() { return _value; }
    T& getValueReference() { return _value; }
    void setValue(T& value) {
        _value = value;
    }
    T* getValuePointer() {
        return &_value;
    }
    T** getValuePointerPointer();
    template<typename K>
    K& getKey();
    //iterator begin();
    //iterator end();

    V getVValue();
    V& getVRef();
    V* getVPointer();

    VV getVVValue();
    VV& getVVRef();
    VV* getVVPointer();

    SimpleTypeDef typeDefValue();
    SimpleTypeDefPointer typeDefPointer();
};

}

#endif

