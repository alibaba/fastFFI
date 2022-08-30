FROM quay.io/pypa/manylinux2014_x86_64:latest

ENV PATH $PATH:/opt/python/cp39-cp39/bin
ENV LIBRARY_PATH /usr/lib:/usr/local/lib:$LIBRARY_PATH
ENV LD_LIBRARY_PATH /usr/lib:/usr/local/lib:$LD_LIBRARY_PATH

ENV LLVM_VER=11.0.0
ENV LLVM11_HOME=/opt/llvm${LLVM_VER}

COPY ./install-gpg23.sh /tmp/install-gpg23.sh
RUN bash /tmp/install-gpg23.sh

COPY ./install-llvm11.sh /tmp/install-llvm11.sh
RUN bash /tmp/install-llvm11.sh

# fixes the /opt/rh/devtoolset-10/root/usr/bin/ld: cannot find -lgcc error
RUN ln -s /opt/rh/devtoolset-10/root/lib/gcc/x86_64-redhat-linux/10 /usr/lib/gcc/x86_64-redhat-linux/10