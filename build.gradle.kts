val kotlin_version: String by project
val logback_version: String by project
val ktorm_version: String by project
val ktor_version: String by project

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("io.ktor.plugin") version "3.1.1"
}

application {
    mainClass = "io.ktor.server.jetty.jakarta.EngineMain"
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-jetty-jakarta")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.ktorm:ktorm-core:${ktorm_version}")
    implementation("io.ktor:ktor-server-websockets")
    implementation("de.mkammerer:argon2-jvm:latest.release")
    implementation("io.ktor:ktor-serialization-gson:3.1.1")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("com.h2database:h2:2.3.232")
}
