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
package samples;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.CXXPointer;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIExpr;
import com.alibaba.fastffi.FFIFactory;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGenBatch;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen(library = "FFITypeAliasSample")
@FFITypeAlias("FFITypeAliasSample")
@CXXTemplate(cxx = "bool", java = "Boolean")
public interface FFITypeAliasSample<T> extends FFIPointer {
    void api1(T t);

    @FFIGen(library = "FFITypeAliasSample.Inner")
    @FFITypeAlias("FFITypeAliasSample<%1$s>::Inner<%2$s>")
    @CXXTemplate(cxx = {"bool", "uint32_t"}, java = {"Boolean", "Integer"})
    interface Inner<T, U> extends FFIPointer {
        void api2(T t, U u);
    }
}

@FFITypeAlias("FFITypeAliasSample2")
@FFIGen(templates = {@CXXTemplate(cxx = "double", java = "java.lang.Double")})
interface FFITypeAliasSample2<T> extends FFIPointer
{
    int get0();

    ArrayType2<T> get2();

    @FFITypeAlias("FFITypeAliasSample2<%s>::ArrayType2")
    @FFIGen(templates = {@CXXTemplate(cxx="double", java = "java.lang.Double")})
    interface ArrayType2<T> extends CXXPointer
    {
    }
}

@FFITypeAlias("std::shared_ptr")
@FFIGen(templates = {
        @CXXTemplate(cxx = "int", java = "Integer")
})
@CXXHead(
        system = "__memory/shared_ptr.h"
)
@CXXHead(
        system = "memory"
)
interface shared_ptr<_Tp> extends CXXPointer {
    void swap(@CXXReference shared_ptr<_Tp> __r);

    void reset();

    element_type<_Tp> get();

    int use_count();

    boolean unique();

    boolean __owner_equivalent(@CXXReference shared_ptr<_Tp> __p);

    @FFIFactory
    @CXXHead(
            system = "__memory/shared_ptr.h"
    )
    @CXXHead(
            system = "memory"
    )
    interface Factory<_Tp> {
        shared_ptr<_Tp> create();

        shared_ptr<_Tp> create(@CXXReference shared_ptr<_Tp> __r);
    }

    @FFITypeAlias("std::shared_ptr<%s>::element_type")
    @FFIGen(templates = {
            @CXXTemplate(cxx = "int", java = "Integer")
    })
    @CXXHead(
            system = "__memory/shared_ptr.h"
    )
    interface element_type<_Tp> extends CXXPointer {
        @FFIExpr("{0}")
        _Tp get();
    }
}
