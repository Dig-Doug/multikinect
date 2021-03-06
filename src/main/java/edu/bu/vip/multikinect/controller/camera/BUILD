java_library(
    name = "camerabus",
    srcs = [
        "CameraBus.java",
    ],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
    ],
)

java_library(
    name = "framebus",
    srcs = [
        "FrameBus.java",
        "FrameReceivedEvent.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
    ],
)

java_library(
    name = "module",
    srcs = glob(["**/*.java"]),
    visibility = ["//visibility:public"],
    runtime_deps = [
        "@org_pubref_rules_protobuf//java:netty_runtime_deps",
    ],
    deps = [
        "@org_pubref_rules_protobuf//java:grpc_compiletime_deps",
        ":camerabus",
        ":framebus",
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:org_slf4j_slf4j_api",
        "//src/main/java/edu/bu/vip/multikinect/sync:coordinatetransform",
        "//src/main/java/edu/bu/vip/multikinect/sync:protoutils",
        "//src/main/proto:calibration",
        "//src/main/proto:camera",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
        "//src/main/proto:skeletonfeed",
    ],
)

java_library(
    name = "framereader",
    srcs = [
        "FrameReader.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:com_google_guava_guava",
        "//src/main/proto:frame",
    ],
)

java_library(
    name = "fileframepublisher",
    srcs = ["FileFramePublisher.java"],
    visibility = ["//visibility:public"],
    deps = [
        ":framebus",
        ":framereader",
        "//:com_google_guava_guava",
        "//:org_slf4j_slf4j_api",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
    ],
)
