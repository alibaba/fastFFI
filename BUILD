java_library(
    name = "ffi",
    srcs = glob(["ffi/src/main/java/**/*.java"]),
    resources = glob(["ffi/src/main/resources/**"]),
    visibility = ["//visibility:public"],
)

java_library(
    name = "annotation-processor",
    srcs = glob(["annotation-processor/src/main/java/**/*.java"]),
    resources = glob(["annotation-processor/src/main/resources/**"]),
    deps = [":ffi", "@maven//:com_squareup_javapoet"],
    visibility = ["//visibility:public"],
)

java_plugin(
    name = "plugin",
    deps = [":annotation-processor"],
    processor_class = "AnnotationProcessor",
    generates_api = True,
    visibility = ["//visibility:public"],
)

java_library(
    name = "llvm",
    srcs = glob(["llvm/src/main/java/**/*.java"]),
    resources = glob(["llvm/src/main/resources/**"]),
    plugins = [":plugin"],
    deps = [":ffi"],
    visibility = ["//visibility:public"],
)

java_binary(
    name = "llvm4jni",
    srcs = glob(["llvm/src/main/java/**/*.java",
                 "llvm4jni-runtime/src/main/java/**/*.java",
                 "llvm4jni/src/main/java/**/*.java"]),
    resources = glob(["llvm/src/main/resources/**",
                      "llvm4jni-runtime/src/main/resources/**",
                      "llvm4jni/src/main/resources/**"]),
    plugins = [":plugin"],
    main_class = "Main",
    deps = [":ffi",
            "@maven//:com_github_jnr_jnr_ffi",
            "@maven//:com_github_jnr_jffi",
            "@maven//:org_ow2_asm_asm",
            "@maven//:org_ow2_asm_asm_util",
            "@maven//:org_ow2_asm_asm_tree",
           ],
    visibility = ["//visibility:public"],
)

genrule(
    name = "llvm-jni-srcs",
    srcs = [":llvm"],
    outs = ["llvm-jni-srcs.zip"],
    cmd =
    """
        mkdir -p $(@D)/llvm-jni-srcs
        $(JAVABASE)/bin/jar xf $(@D)/libllvm.jar
        for x in `find . -type f -name "*.cc"`; do cp $$x $(@D)/llvm-jni-srcs/; done
        cd $(@D)
        zip -rq llvm-jni-srcs.zip llvm-jni-srcs
    """,
    toolchains = [
        "@bazel_tools//tools/jdk:current_java_runtime",
    ],
    visibility = ["//visibility:public"],
)

genrule(
    name = "llvm-jni-so",
    srcs = [":llvm-jni-srcs"],
    outs = ["libllvm4jni.so"],
    cmd =
    """
        unzip $(@D)/llvm-jni-srcs.zip
        $$LLVM_HOME/bin/clang++ -I $$LLVM_HOME/include -I $$JAVA_HOME/include/ -I $$JAVA_HOME/include/linux/ -fPIC -shared -o $@ llvm-jni-srcs/*.cc
    """,
    visibility = ["//visibility:public"],
)
