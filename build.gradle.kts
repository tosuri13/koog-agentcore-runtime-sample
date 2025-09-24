plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

group = "io.github.tosuri13.koog-agentcore-runtime-sample"
version = "0.0.1"

application {
    mainClass.set("io.github.tosuri13.koog_agentcore_runtime_sample.MainKt")
}

dependencies {
    implementation(libs.bedrock.runtime.jvm)
    implementation(libs.koog.agents)
}
