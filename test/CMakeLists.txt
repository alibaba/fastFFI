# Copyright 1999-2021 Alibaba Group Holding Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
project(libffitest)
cmake_minimum_required(VERSION 3.8)

include_directories("src/main/native")

SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -g -std=c++11")

set(LIBNAME "ffitest")
set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)
find_package(JNI REQUIRED)
include_directories(SYSTEM ${JAVA_INCLUDE_PATH})
include_directories(SYSTEM ${JAVA_INCLUDE_PATH2})

file(GLOB_RECURSE FFITEST_SRC_FILES "src/main/native/*.cc" "target/generated-sources/annotations/*.cc")
add_library(${LIBNAME} SHARED ${FFITEST_SRC_FILES})
target_link_libraries(${LIBNAME} ${JAVA_JVM_LIBRARY} ${CMAKE_JNI_LINKER_FLAGS})
