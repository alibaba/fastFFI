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
#include "FullFeature.h"

template<typename P1, typename P2>
jbyte FullFeature<P1, P2>::echo(jbyte i) {
  return i;
}

template<typename P1, typename P2>
jboolean FullFeature<P1, P2>::echo(jboolean i) {
  return i;
}

template<typename P1, typename P2>
jshort FullFeature<P1, P2>::echo(jshort i) {
  return i;
}

template<typename P1, typename P2>
jint FullFeature<P1, P2>::echo(jint i) {
  return i;
}

template<typename P1, typename P2>
jlong FullFeature<P1, P2>::echo(jlong i) {
  return i;
}

template<typename P1, typename P2>
jfloat FullFeature<P1, P2>::echo(jfloat i) {
  return i;
}

template<typename P1, typename P2>
jdouble FullFeature<P1, P2>::echo(jdouble i) {
  return i;
}

template<typename P1, typename P2>
void FullFeature<P1, P2>::templateCall(P1 i, P2 j) {
}

template<typename P1, typename P2>
void FullFeature<P1, P2>::unknownException() {
  throw 0;
}

template<typename P1, typename P2>
FullFeature<P1, P2>* FullFeature<P1, P2>::make() {
  return new FullFeature<P1, P2>;
}

template<typename P1, typename P2>
template<typename T>
void FullFeature<P1, P2>::templateMethod(T t) {
}

template<typename P1, typename P2>
FullFeature<P1, P2> FullFeature<P1, P2>::rvTest() {
  return FullFeature<P1, P2>();
}

std::string libraryApi() {
  return "library api";
}

template class FullFeature<int, float>;
template class FullFeature<int, double>;
template void FullFeature<int, float>::templateMethod<int>(int);
template void FullFeature<int, double>::templateMethod<int>(int);
