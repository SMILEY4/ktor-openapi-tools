plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(project(":ktor-openapi"))
    implementation(project(":ktor-swagger-ui"))
    implementation(project(":ktor-redoc"))

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.contentnegotiation)
    implementation(libs.ktor.server.serialization.jackson)
    implementation(libs.ktor.server.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.calllogging)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.test.host)

    implementation(libs.schemakenerator.core)
    implementation(libs.schemakenerator.reflection)
    implementation(libs.schemakenerator.serialization)
    implementation(libs.schemakenerator.swagger)
    implementation(libs.schemakenerator.jackson)

    implementation(libs.swagger.parser)

    implementation(libs.kotlin.logging)
    implementation(libs.logback)
}
