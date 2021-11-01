#! /bin/bash
# /*
#  * Copyright 1999-2021 Alibaba Group Holding Ltd.
#  *
#  * Licensed under the Apache License, Version 2.0 (the "License");
#  * you may not use this file except in compliance with the License.
#  * You may obtain a copy of the License at
#  *
#  *      http://www.apache.org/licenses/LICENSE-2.0
#  *
#  * Unless required by applicable law or agreed to in writing, software
#  * distributed under the License is distributed on an "AS IS" BASIS,
#  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  * See the License for the specific language governing permissions and
#  * limitations under the License.
#  */

DIR=$(pushd $(dirname $BASH_SOURCE[0]) > /dev/null && pwd && popd > /dev/null)

JAVA=$(which java)

if [[ -d "$JAVA_HOME" ]]; then
    JAVA=$JAVA_HOME/bin/java
fi

if [[ ! -x "$JAVA" ]]; then
    echo "\$JAVA_HOME/bin/java points to $JAVA, which is not an executable"
    exit 1
fi

CP=${DIR}/target/benchmarks.jar

VM_OPTS="$VM_OPTS -XX:CompileCommandFile=${DIR}/compile-commands.txt"
VM_OPTS="$VM_OPTS -Xms8G -Xmx8G"
#VM_OPTS="$VM_OPTS -XX:+UseParallelGC"

MAIN_CLASS=${MAIN_CLASS:-org.openjdk.jmh.Main}

echo ">>>> Begin to run benchmarks without LLVM4JNI"
$JAVA $VM_OPTS -cp $CP $MAIN_CLASS -gc true -rff ${DIR}/jni.csv -bm avgt -tu ns $@
echo ">>>> End to run benchmarks without LLVM4JNI"

echo ">>>> Begin to run benchmarks with LLVM4JNI"
CP=${DIR}/target/llvm4jni-output:$CP
$JAVA $VM_OPTS -cp $CP $MAIN_CLASS -gc true -rff ${DIR}/ffi.csv -bm avgt -tu ns $@
echo ">>>> End to run benchmarks with LLVM4JNI"
