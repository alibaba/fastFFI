Require cmake 3.1, LLVM 11 and JDK 8.

1. Build and install fastFFI

    ```sh
    git clone https://github.com/alibaba/fastffi
    export LLVM11_HOME=<path-to-LLVM11-home>
    cd fastffi
    mvn clean install
    ```

2. Download and build flatbuffers 2.0.0

    ```sh
    bash build-flatbuffers.sh
    ```

3. Build source code

    ```sh
    export LLVM11_HOME=<path-to-LLVM11-home>
    mvn clean package
    ```

4. Run

    ```sh
    bash run.sh
    ```
