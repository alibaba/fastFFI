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
import com.alibaba.fastffi.CXXPointerRange;
import com.alibaba.fastffi.CXXPointerRangeElement;
import com.alibaba.fastffi.CXXTemplate;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIPointer;
import com.alibaba.fastffi.FFITypeAlias;

@FFIGen(library = "CXXPointerRangeSample")
@FFITypeAlias("CXXPointerRangeSample")
public interface CXXPointerRangeSample extends FFIPointer, CXXPointerRange<Element> {

}

@FFIGen(library = "CXXPointerRangeSample")
@FFITypeAlias("CXXPointerRangeSampleElement")
interface Element extends FFIPointer, CXXPointerRangeElement<Element> {}
