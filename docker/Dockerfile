FROM reg.docker.alibaba-inc.com/ali/os:7u2

RUN yum install alios7u-2_30-gcc-9-repo.noarch -y \
 && yum install gcc-c++ libstdc++ -y \
 && yum install llvm11 llvm11-devel llvm11-libs llvm11-static clang11-libs clang11 clang11-devel clang11-tools-extra -b current -y \
 && yum install lld11 -b current -y \
 && yum install mpi -b current -y \
 && yum install -y java-1.8.0-openjdk-devel.x86_64 \
 && yum install git -y \
 && yum install maven -y \
 && yum install bash -y \
 && yum install cmake -y \
 && yum install glog -y \
 && yum install glog-devel -y \
 && yum install gflags -y \
 && yum install gflags-devel -y \
 && yum clean all

ENTRYPOINT [ "/bin/bash", "-l", "-c" ]
