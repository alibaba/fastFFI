Binding Generator

## Exclude Classes and Methods

Use option `--exclude` and `--exclude-file` to exclude classes or methods via regular expressions.
We use `::` to separate class patterns and method patterns.

Examples:
* `--exclude .*DB`: exclude all classes matched by `.*DB`.
* `--exclude .*DB::Start`: exclude all methods with name `Start` in all classes matched by `.*DB`

We can put all regular expressions in a file and use it via `--excluce-file`.

## FAQ on Mac OS

Install LLVM 11 using home brew.

### Use system library with brew-installed LLVM

1. Invoke system `clang++` to compile any file with option `-v`. You will get a list of expanded options. Here is a sample.
    ```
    ...
    clang -cc1 version 13.0.0 (clang-1300.0.29.3) default target x86_64-apple-darwin20.6.0
    ignoring nonexistent directory "/Library/Developer/CommandLineTools/SDKs/MacOSX.sdk/usr/local/include"
    ignoring nonexistent directory "/Library/Developer/CommandLineTools/SDKs/MacOSX.sdk/Library/Frameworks"
    #include "..." search starts here:
    #include <...> search starts here:
        /usr/local/include
        /Library/Developer/CommandLineTools/SDKs/MacOSX.sdk/usr/include/c++/v1
        /Library/Developer/CommandLineTools/usr/lib/clang/13.0.0/include
        /Library/Developer/CommandLineTools/SDKs/MacOSX.sdk/usr/include
        /Library/Developer/CommandLineTools/usr/include
        /Library/Developer/CommandLineTools/SDKs/MacOSX.sdk/System/Library/Frameworks (framework directory)
    End of search list.
    ...
    ```

2. Copy the system search directory to run binding generator. Here are the options used to process RocksDB.
    ```
    --output-directory
    ./rocksdb/java/ffi/src/main/java
    --search-directory
    ./rocksdb/include
    --exclude
    .*::OptimizeForSmallDb
    --exclude
    .*AssociativeMergeOperator
    --exclude
    .*DBWithTTL
    --exclude
    .*StackableDB
    --exclude
    .*DB::AddFile
    --exclude
    rocksdb.SliceFormatter
    --exclude
    .*LDBTool.*
    --exclude
    .*LDBOptions
    --
    --extra-arg-before=-resource-dir
    --extra-arg-before=/usr/local/Cellar/llvm@11/11.1.0_3/lib/clang/11.1.0
    --extra-arg-before=-isysroot
    --extra-arg-before=/Library/Developer/CommandLineTools/SDKs/MacOSX11.sdk
    --extra-arg-before=-x
    --extra-arg-before=c++
    ```

