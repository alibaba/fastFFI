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

VERSION=2.0.0
SRC_DIR=${DIR}/flatbuffers-src


if [[ ! -d $SRC_DIR ]]; then
    git clone --depth 1 --branch v$VERSION https://github.com/google/flatbuffers.git $SRC_DIR
fi

pushd $SRC_DIR

cp -r ${SRC_DIR}/java/com ${DIR}/src/main/java

#rm -rf build
mkdir -p build
pushd build
cmake ..
make

./flatc --version

./flatc --cpp -o ${DIR}/src/main/native ${SRC_DIR}/samples/monster.fbs
./flatc --java -o ${DIR}/src/main/java ${SRC_DIR}/samples/monster.fbs
popd
popd
