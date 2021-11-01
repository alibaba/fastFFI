#! /bin/bash

VERSION=v0.9.7

wget https://raw.githubusercontent.com/simdjson/simdjson/${VERSION}/singleheader/simdjson.h -O src/main/native/simdjson.h
wget https://raw.githubusercontent.com/simdjson/simdjson/${VERSION}/singleheader/simdjson.cpp -O src/main/native/simdjson.cpp
wget https://raw.githubusercontent.com/simdjson/simdjson/${VERSION}/jsonexamples/twitter.json -O src/main/resources/twitter.json
