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
#ifndef FULL_FEATURE
#define FULL_FEATURE

#include "jni.h"
#include <string>

template<typename P1, typename P2>
class FullFeature {
private:
  int field;
public:
  jbyte echo(jbyte i);

  jboolean echo(jboolean i);

  jshort echo(jshort i);

  jint echo(jint i);

  jlong echo(jlong i);

  jfloat echo(jfloat i);

  jdouble echo(jdouble i);

  std::string echo(std::string i);

  void setField(int i) { field = i; }

  int getField() { return field; }

  void templateCall(P1 i, P2 j);

  void unknownException();

  FullFeature* make();

  template<typename T>
  void templateMethod(T t);

  FullFeature rvTest();
};

std::string libraryApi();

#endif // FULL_FEATURE