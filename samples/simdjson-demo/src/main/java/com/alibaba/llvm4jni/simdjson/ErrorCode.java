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
package com.alibaba.llvm4jni.simdjson;

import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;
import com.alibaba.fastffi.FFITypeRefiner;

@FFITypeAlias("simdjson::error_code")
@FFITypeRefiner("ErrorCode.getErrorCode")
public enum ErrorCode implements CXXEnum {

    SUCCESS(Library.INSTANCE.SUCCESS()),
    CAPACITY(Library.INSTANCE.CAPACITY()),
    MEMALLOC(Library.INSTANCE.MEMALLOC()),
    TAPE_ERROR(Library.INSTANCE.TAPE_ERROR()),
    DEPTH_ERROR(Library.INSTANCE.DEPTH_ERROR()),
    STRING_ERROR(Library.INSTANCE.STRING_ERROR()),
    T_ATOM_ERROR(Library.INSTANCE.T_ATOM_ERROR()),
    F_ATOM_ERROR(Library.INSTANCE.F_ATOM_ERROR()),
    N_ATOM_ERROR(Library.INSTANCE.N_ATOM_ERROR()),
    NUMBER_ERROR(Library.INSTANCE.NUMBER_ERROR()),
    UTF8_ERROR(Library.INSTANCE.UTF8_ERROR()),
    UNINITIALIZED(Library.INSTANCE.UNINITIALIZED()),
    EMPTY(Library.INSTANCE.EMPTY()),
    UNESCAPED_CHARS(Library.INSTANCE.UNESCAPED_CHARS()),
    UNCLOSED_STRING(Library.INSTANCE.UNCLOSED_STRING()),
    UNSUPPORTED_ARCHITECTURE(Library.INSTANCE.UNSUPPORTED_ARCHITECTURE()),
    INCORRECT_TYPE(Library.INSTANCE.INCORRECT_TYPE()),
    NUMBER_OUT_OF_RANGE(Library.INSTANCE.NUMBER_OUT_OF_RANGE()),
    NO_SUCH_FIELD(Library.INSTANCE.NO_SUCH_FIELD()),
    IO_ERROR(Library.INSTANCE.IO_ERROR()),
    INVALID_JSON_POINTER(Library.INSTANCE.INVALID_JSON_POINTER()),
    INVALID_URI_FRAGMENT(Library.INSTANCE.INVALID_URI_FRAGMENT()),
    UNEXPECTED_ERROR(Library.INSTANCE.UNEXPECTED_ERROR()),
    PARSER_IN_USE(Library.INSTANCE.PARSER_IN_USE()),
    OUT_OF_ORDER_ITERATION(Library.INSTANCE.OUT_OF_ORDER_ITERATION()),
    INSUFFICIENT_PADDING(Library.INSTANCE.INSUFFICIENT_PADDING()),
    NUM_ERROR_CODES(Library.INSTANCE.NUM_ERROR_CODES());

    @FFIGen
    @FFILibrary(value = "simdjson::error_code", namespace = "simdjson::error_code")
    @CXXHead("simdjson.h")
    public interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);

        @FFIGetter int SUCCESS();              ///< No error
        @FFIGetter int CAPACITY();                 ///< This parser can't support a document that big
        @FFIGetter int MEMALLOC();                 ///< Error allocating int memory(); most likely out of memory
        @FFIGetter int TAPE_ERROR();               ///< Something went wrong while writing to the tape (stage int 2)(); this is a generic error
        @FFIGetter int DEPTH_ERROR();              ///< Your document exceeds the user-specified depth limitation
        @FFIGetter int STRING_ERROR();             ///< Problem while parsing a string
        @FFIGetter int T_ATOM_ERROR();             ///< Problem while parsing an atom starting with the letter 't'
        @FFIGetter int F_ATOM_ERROR();             ///< Problem while parsing an atom starting with the letter 'f'
        @FFIGetter int N_ATOM_ERROR();             ///< Problem while parsing an atom starting with the letter 'n'
        @FFIGetter int NUMBER_ERROR();             ///< Problem while parsing a number
        @FFIGetter int UTF8_ERROR();               ///< the input is not valid UTF-8
        @FFIGetter int UNINITIALIZED();            ///< unknown int error(); or uninitialized document
        @FFIGetter int EMPTY();                    ///< no structural element found
        @FFIGetter int UNESCAPED_CHARS();          ///< found unescaped characters in a string.
        @FFIGetter int UNCLOSED_STRING();          ///< missing quote at the end
        @FFIGetter int UNSUPPORTED_ARCHITECTURE(); ///< unsupported architecture
        @FFIGetter int INCORRECT_TYPE();           ///< JSON element has a different type than user expected
        @FFIGetter int NUMBER_OUT_OF_RANGE();      ///< JSON number does not fit in 64 bits
        @FFIGetter int NO_SUCH_FIELD();            ///< JSON field not found in object
        @FFIGetter int IO_ERROR();                 ///< Error reading a file
        @FFIGetter int INVALID_JSON_POINTER();     ///< Invalid JSON pointer reference
        @FFIGetter int INVALID_URI_FRAGMENT();     ///< Invalid URI fragment
        @FFIGetter int UNEXPECTED_ERROR();         ///< indicative of a bug in simdjson
        @FFIGetter int PARSER_IN_USE();            ///< parser is already in use.
        @FFIGetter int OUT_OF_ORDER_ITERATION();   ///< tried to iterate an array or object out of order
        @FFIGetter int INSUFFICIENT_PADDING();     ///< The JSON doesn't have enough padding for simdjson to safely parse it.
        @FFIGetter int NUM_ERROR_CODES();
    }

    ErrorCode(int value) {
        this.value = value;
    }

    private int value;

    @Override
    public int getValue() {
        return value;
    }

    static CXXEnumMap<ErrorCode> map = new CXXEnumMap<>(values());

    public static ErrorCode getErrorCode(int value) {
        return map.get(value);
    }
}
