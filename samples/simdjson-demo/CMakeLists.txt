cmake_minimum_required(VERSION 3.1)
project(simdjson-ffi)

SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -std=c++17 -Wall")

file(GLOB SOURCES "target/generated-sources/annotations/*.cc" "target/generated-test-sources/test-annotations/*.cc" "src/main/native/*.cc" "src/main/native/*.cpp")

set(LIBNAME "simdjson-ffi")

set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)
find_package(JNI REQUIRED)
include_directories(SYSTEM ${JAVA_INCLUDE_PATH})
include_directories(SYSTEM ${JAVA_INCLUDE_PATH2})

include_directories("src/main/native")
include_directories("src/test/native")

add_library(${LIBNAME} SHARED ${SOURCES})
#target_link_libraries(${LIBNAME})

set_target_properties(${LIBNAME} PROPERTIES LINKER_LANGUAGE CXX)
add_compile_definitions(SIMDJSON_EXCEPTIONS=0)

add_custom_command(TARGET ${LIBNAME}
                   POST_BUILD
                   COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:${LIBNAME}> "${CMAKE_CURRENT_SOURCE_DIR}/target/classes/")

add_custom_command(TARGET ${LIBNAME}
                   POST_BUILD
                   COMMAND ${CMAKE_COMMAND} -E make_directory ${CMAKE_CURRENT_SOURCE_DIR}/target/native/bitcode
                   COMMAND ${CMAKE_COMMAND} -E copy_if_different $<TARGET_OBJECTS:${LIBNAME}> ${CMAKE_CURRENT_SOURCE_DIR}/target/native/bitcode COMMAND_EXPAND_LISTS)
