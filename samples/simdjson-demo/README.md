1. Download and build fastFFI <https://github.com/alibaba/fastffi>

2. Build and run benchmark

```
cd simdjson-java/
export LLVM11_HOME=<path-to-llvm-11-home>
export JAVA_HOME=<path-to-JDK8-HOME>
bash download.sh # download simdjson from github
mvn clean package
bash run.sh
```
