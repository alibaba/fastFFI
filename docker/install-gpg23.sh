#!/bin/bash

workdir=/tmp/install-gpg
mkdir -p $workdir

cd $workdir
curl -L -O --insecure https://gnupg.org/ftp/gcrypt/gpgrt/libgpg-error-1.45.tar.bz2
tar xjf libgpg-error-1.45.tar.bz2
cd libgpg-error-1.45
./configure --prefix=/usr
make install -j$(nproc)

cd $workdir
curl -L -O --insecure https://www.gnupg.org/ftp/gcrypt/libassuan/libassuan-2.5.5.tar.bz2
tar xjf libassuan-2.5.5.tar.bz2
cd libassuan-2.5.5
./configure --prefix=/usr
make install -j$(nproc)

cd $workdir
curl -L -O --insecure https://www.gnupg.org/ftp/gcrypt/libgcrypt/libgcrypt-1.10.1.tar.bz2
tar xjf libgcrypt-1.10.1.tar.bz2
cd libgcrypt-1.10.1
./configure --prefix=/usr
make install -j$(nproc)

cd $workdir
curl -L -O --insecure https://www.gnupg.org/ftp/gcrypt/libksba/libksba-1.6.0.tar.bz2
tar xjf libksba-1.6.0.tar.bz2
cd libksba-1.6.0
./configure --prefix=/usr
make install -j$(nproc)

cd $workdir
curl -L -O --insecure https://www.gnupg.org/ftp/gcrypt/npth/npth-1.6.tar.bz2
tar xjf npth-1.6.tar.bz2
cd npth-1.6
./configure --prefix=/usr
make install -j$(nproc)

cd $workdir
curl -L -O --insecure https://www.gnupg.org/ftp/gcrypt/gnupg/gnupg-2.3.7.tar.bz2
tar xjf gnupg-2.3.7.tar.bz2
cd gnupg-2.3.7
./configure --prefix=/usr
make install -j$(nproc)

if [ -d ~/.gnugp ]; then rm -ri ~/.gnugp; fi
gpgconf --kill gpg-agent

# cleanup
cd /
rm -rf $workdir
