maven_jar(
    name = "com_google_guava_guava",
    artifact = "com.google.guava:guava:23.5-jre",
)
maven_jar(
    name = "com_graphql_java",
    artifact = "com.graphql-java:graphql-java:6.0",
)


# proto_library rules implicitly depend on @com_google_protobuf//:protoc,
# which is the proto-compiler.
# This statement defines the @com_google_protobuf repo.
http_archive(
    name = "com_google_protobuf",
    sha256 = "cef7f1b5a7c5fba672bec2a319246e8feba471f04dcebfe362d55930ee7c1c30",
    strip_prefix = "protobuf-3.5.0",
    urls = ["https://github.com/google/protobuf/archive/v3.5.0.zip"],
)

maven_jar(
    name = "com_google_protobuf_java",
    artifact = "com.google.protobuf:protobuf-java:jar:3.5.0",
)
maven_jar(
    name = "com_google_guice",
    artifact = "com.google.inject:guice:jar:4.1.0",
)
maven_jar(
    name = "com_google_guice_multibindings",
    artifact = "com.google.inject.extensions:guice-multibindings:jar:4.1.0",
)
maven_jar(
    name = "javax_inject",
    artifact = "javax.inject:javax.inject:jar:1",
)
maven_jar(
    name = "com_google_autovalue",
    artifact = "com.google.auto.value:auto-value:jar:1.5.2",
)
maven_jar(
    name = "javax_annotations",
    artifact = "com.google.code.findbugs:jsr305:3.0.0",
)
maven_jar(
    name = "truth",
    artifact = "com.google.truth:truth:0.36",
)
maven_jar(
    name = "truth_extension_lite",
    artifact = "com.google.truth.extensions:truth-liteproto-extension:jar:0.36",
)
maven_jar(
    name = "truth_extension",
    artifact = "com.google.truth.extensions:truth-proto-extension:jar:0.36",
)
maven_jar(
    name = "junit",
    artifact = "junit:junit:jar:4.12",
)

maven_jar(
    name = "aop",
    artifact = "aopalliance:aopalliance:jar:1.0",
)
