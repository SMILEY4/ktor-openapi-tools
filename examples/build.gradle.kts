val projectGroupId: String by project
val projectVersion: String by project
group = projectGroupId
version = projectVersion

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.0.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":ktor-openapi"))
    implementation(project(":ktor-swagger-ui"))
    implementation(project(":ktor-redoc"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    val versionKtor: String by project
    implementation("io.ktor:ktor-server-netty-jvm:$versionKtor")
    implementation("io.ktor:ktor-server-content-negotiation:$versionKtor")
    implementation("io.ktor:ktor-serialization-jackson:$versionKtor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$versionKtor")


    implementation("io.ktor:ktor-server-auth:$versionKtor")
    implementation("io.ktor:ktor-server-call-logging:$versionKtor")
    implementation("io.ktor:ktor-server-test-host:$versionKtor")
    implementation("io.ktor:ktor-server-resources:$versionKtor")

    val versionSchemaKenerator: String by project
    implementation("io.github.smiley4:schema-kenerator-core:$versionSchemaKenerator")
    implementation("io.github.smiley4:schema-kenerator-reflection:$versionSchemaKenerator")
    implementation("io.github.smiley4:schema-kenerator-serialization:$versionSchemaKenerator")
    implementation("io.github.smiley4:schema-kenerator-swagger:$versionSchemaKenerator")
    implementation("io.github.smiley4:schema-kenerator-jackson:$versionSchemaKenerator")

    val versionSwaggerParser: String by project
    implementation("io.swagger.parser.v3:swagger-parser:$versionSwaggerParser")

    val versionKotlinLogging: String by project
    implementation("io.github.oshai:kotlin-logging-jvm:$versionKotlinLogging")

    val versionLogback: String by project
    implementation("ch.qos.logback:logback-classic:$versionLogback")
}

kotlin {
    jvmToolchain(11)
}
