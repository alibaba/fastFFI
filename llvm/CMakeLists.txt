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
cmake_minimum_required(VERSION 3.8)
project(LLVM4JNI)

set(CMAKE_C_STANDARD 11)
set(CMAKE_CXX_STANDARD 14)

option(USE_LIB_LLVM "Link against the libLLVM.so rather than component libraries" OFF)

SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++14 -Wall")
SET(CMAKE_CXX_FLAGS_DEBUG  "${CMAKE_CXX_FLAGS_DEBUG} -O0 -g -fprofile-arcs -ftest-coverage")
SET(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -O3 -g")

find_program(ccache_EXECUTABLE ccache)
if(ccache_EXECUTABLE)
    set(CMAKE_C_COMPILER_LAUNCHER: ${ccache_EXECUTABLE})
    set(CMAKE_CXX_COMPILER_LAUNCHER ${ccache_EXECUTABLE})
endif(ccache_EXECUTABLE)

file(GLOB SOURCES "target/generated-sources/annotations/*.cc")

set(LIBNAME "llvm4jni")

set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_JVM_LIBRARY NotNeeded)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)
find_package(JNI REQUIRED)
include_directories(SYSTEM ${JAVA_INCLUDE_PATH})
include_directories(SYSTEM ${JAVA_INCLUDE_PATH2})

find_package(LLVM REQUIRED CONFIG)
find_package(Clang REQUIRED CONFIG)
message(STATUS "Found LLVM ${LLVM_PACKAGE_VERSION}")
message(STATUS "Using LLVMConfig.cmake in: ${LLVM_DIR}")

include_directories(${LLVM_INCLUDE_DIRS})
add_definitions(${LLVM_DEFINITIONS})

add_library(${LIBNAME} SHARED ${SOURCES})

# Fixes the "LLVM ERROR: inconsistency in registered CommandLine options" error
find_library(LIB_LLVM LLVM HINTS ${LLVM_LIBRARY_DIRS})
if(USE_LIB_LLVM AND LIB_LLVM)
    message(STATUS "Use the LLVM library: ${LIB_LLVM}")
    set(llvm_libs ${LIB_LLVM})
else()
    message(STATUS "Use the splited LLVM libraries")
    llvm_map_components_to_libnames(_llvm_libs support core option bitreader object)
    llvm_expand_dependencies(llvm_libs ${_llvm_libs})
endif()

target_link_libraries(${LIBNAME} ${llvm_libs} ${CMAKE_JNI_LINKER_FLAGS}
    clangAnalysis
    clangARCMigrate
    clangAST
    clangASTMatchers
    clangBasic
    clangCodeGen
    clangCrossTU
    clangDependencyScanning
    clangDirectoryWatcher
    clangDriver
    clangDynamicASTMatchers
    clangEdit
    clangFormat
    clangFrontend
    clangFrontendTool
    clangHandleCXX
    clangHandleLLVM
    clangIndex
    clangLex
    clangParse
    clangRewrite
    clangRewriteFrontend
    clangSema
    clangSerialization
    clangStaticAnalyzerCheckers
    clangStaticAnalyzerCore
    clangStaticAnalyzerFrontend
    clangTesting
    clangTooling
    clangToolingASTDiff
    clangToolingCore
    clangToolingInclusions
    clangToolingRefactoring
    clangToolingSyntax
    clangTransformer
    )

set_target_properties(${LIBNAME} PROPERTIES COMPILE_FLAGS "-fPIC")
set_target_properties(${LIBNAME} PROPERTIES LINKER_LANGUAGE CXX)

add_custom_command(TARGET ${LIBNAME}
                   POST_BUILD
                   COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:${LIBNAME}> "${CMAKE_CURRENT_SOURCE_DIR}/target/classes/")

if (APPLE)
    add_custom_command(TARGET ${LIBNAME}
                   POST_BUILD
                   COMMAND ${CMAKE_COMMAND} -E make_directory ${CMAKE_CURRENT_SOURCE_DIR}/target/native/bitcode
                   COMMAND ${CMAKE_COMMAND} -E copy_if_different $<TARGET_OBJECTS:${LIBNAME}> ${CMAKE_CURRENT_SOURCE_DIR}/target/native/bitcode COMMAND_EXPAND_LISTS)
endif()
