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
import com.alibaba.fastffi.CXXOperator;
import com.alibaba.fastffi.CXXReference;
import com.alibaba.fastffi.CXXValue;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen(library = "CXXOperatorSample")
@FFITypeAlias("CXXOperatorSample")
public interface CXXOperatorSample extends FFIPointer {

    @CXXOperator("*")
    byte indirection();

    @CXXOperator("*&")
    @CXXValue CXXOperatorSample copy();

    @CXXOperator("++")
    @CXXReference CXXOperatorSample inc();

    @CXXOperator("++")
    @CXXReference CXXOperatorSample inc(int i);

    @CXXOperator("=")
    @CXXReference CXXOperatorSample assign(@CXXReference CXXOperatorSample sample);

    @CXXOperator(value = "==")
    boolean eq(@CXXReference CXXOperatorSample rhs);

    @CXXOperator(value = "[]")
    int index(int i);

    @CXXOperator(value = "[]")
    int index(int i, int j);
}