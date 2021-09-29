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
DIR=$(pushd $(dirname ${BASH_SOURCE[0]}) > /dev/null && pwd && popd > /dev/null)


if [[ -d "$JAVA_HOME" ]]; then
    JAVA=$JAVA_HOME/bin/java
else
    JAVA=$(which java)
fi

if [[ ! -x "$JAVA" ]]; then
    echo "\$JAVA_HOME/bin/java points to $JAVA, which is not an executable"
    exit 1
fi

VERSION=0.1
JAR=${DIR}/target/llvm4jni-${VERSION}-jar-with-dependencies.jar

if [[ ! -f $JAR ]];
then
    echo "Need to run 'mvn clean install' in the root directory"
    exit 1
fi

MAIN_CLASS=com.alibaba.fastffi.llvm4jni.Main
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${DIR}/../llvm/target/native
$JAVA $VM_OPTS -Djava.library.path=${DIR}/../llvm/target/native -cp ${DIR}/target/classes:$JAR:$EXTRA_CP $MAIN_CLASS $@
