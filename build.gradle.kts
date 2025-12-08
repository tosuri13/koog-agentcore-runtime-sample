plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "io.github.tosuri13.kars"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(libs.awssdk.auth.java)
    implementation(libs.bedrock.runtime.jvm)
    implementation(libs.bedrock.agentcore.jvm)
    implementation(libs.dotenv.kotlin)
    implementation(libs.koog.agents)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.logback.classic)
}

tasks.register<JavaExec>("invokeAgent") {
    mainClass.set("io.github.tosuri13.kars.InvokeAgentKt")
    classpath = sourceSets["main"].runtimeClasspath
}
