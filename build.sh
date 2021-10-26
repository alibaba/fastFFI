#! /bin/bash
#
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

DIR=$(pushd $(dirname $BASH_SOURCE[0]) > /dev/null && pwd && popd > /dev/null)

if [[ -d $LLVM11_HOME ]];
then
    CLANG=$LLVM11_HOME/bin/clang++
else
    CLANG=$(which clang++)
fi

echo "Using clang at $CLANG"

$CLANG --version 2>/dev/null | grep "clang version 11" #> /dev/null 2>&1
RET=$?
if [[ $RET != 0 ]];
then
    echo "Invalid clang++ 11"
    exit $RET
fi


if [[ ! -d $LLVM11_HOME ]];
then
    LLVM11_HOME=$(dirname $(dirname $CLANG))
fi

export LLVM11_HOME=$LLVM11_HOME

echo "Using LLVM11 at $LLVM11_HOME"

mvn clean install
