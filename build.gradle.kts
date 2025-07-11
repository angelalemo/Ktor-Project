val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.2.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23" // Must match Kotlin version
}

group = "com.naruedon672110147"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    // --- Server core ---
    implementation("io.ktor:ktor-server-core-jvm:3.2.0")
    implementation("io.ktor:ktor-server-netty-jvm:3.2.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.2.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.2.0")
    implementation("io.ktor:ktor-server-config-yaml:3.2.0")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // --- Unit Test ---
    testImplementation("io.ktor:ktor-server-test-host-jvm:3.2.0")
    testImplementation("io.ktor:ktor-client-cio-jvm:3.2.0")
    testImplementation("io.ktor:ktor-client-content-negotiation-jvm:3.2.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
