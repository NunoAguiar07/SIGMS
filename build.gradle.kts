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
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-jetty-jakarta")
    implementation("io.ktor:ktor-server-websockets:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.apache.poi:poi-ooxml:5.2.3") // just for excel readability
    implementation("org.apache.commons:commons-email:1.5")
    implementation("org.ktorm:ktorm-core:${ktorm_version}")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.ktorm:ktorm-support-postgresql:${ktorm_version}")
    implementation("io.ktor:ktor-server-websockets")
    implementation("de.mkammerer:argon2-jvm:latest.release")
    implementation("io.ktor:ktor-serialization-gson:3.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("com.h2database:h2:2.3.232")
}
