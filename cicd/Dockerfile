FROM ubuntu:20.10

RUN apt update && apt upgrade \
    && apt-get install -y cmake openjdk-8-jdk maven ant clang-11 llvm-11 llvm-11-dev lld-11 libz-dev \
    && ln -s /usr/bin/ld.lld-11 /usr/bin/ld.lld \
    && ln -s /usr/bin/clang-11 /usr/bin/clang \
    && ln -s /usr/bin/clang++-11 /usr/bin/clang++ \
    && ln -s /usr/lib/jvm/`ls /usr/lib/jvm/ |grep 1.8` /usr/lib/jvm/8u

ENV LLVM11_HOME=/usr/lib/llvm-11
ENV JAVA_HOME=/usr/lib/jvm/8u

ENTRYPOINT [ "/bin/bash", "-l", "-c" ]
