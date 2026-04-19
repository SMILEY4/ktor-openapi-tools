import org.gradle.kotlin.dsl.testImplementation

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dependencycheck)
    alias(libs.plugins.detekt)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.versions)
}

dependencies {
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.resources)

    testImplementation(libs.ktor.server.netty.jvm)
    testImplementation(libs.ktor.server.contentnegotiation)
    testImplementation(libs.ktor.server.serialization.jackson)
    testImplementation(libs.ktor.server.calllogging)
    testImplementation(libs.ktor.server.test.host)

    implementation(libs.swagger.parser)

    implementation(libs.jackson.kotlin)

    implementation(libs.schemakenerator.core)
    implementation(libs.schemakenerator.reflection)
    implementation(libs.schemakenerator.serialization)
    implementation(libs.schemakenerator.swagger)

    implementation(libs.kotlin.logging)

    implementation(libs.schemakenerator.swagger)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.logback)
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(file("$rootDir/docs/dokka/ktor-openapi"))
    }
}


mavenPublishing {
    val projectGroupId: String by project
    val projectVersion: String by project
    val projectBaseScmUrl: String by project
    val projectBaseScmConnection: String by project
    val projectLicenseName: String by project
    val projectLicenseUrl: String by project
    val projectDeveloperName: String by project
    val projectDeveloperUrl: String by project

    configure(
        com.vanniktech.maven.publish.KotlinJvm(
            javadocJar = com.vanniktech.maven.publish.JavadocJar.Dokka("dokkaGenerateHtml")
        )
    )

    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    coordinates(projectGroupId, "ktor-openapi", projectVersion)
    pom {
        name.set("Ktor OpenApi")
        description.set("Ktor plugin to automatically generate and provide OpenApi")
        url.set(projectBaseScmUrl)
        licenses {
            license {
                name.set(projectLicenseName)
                url.set(projectLicenseUrl)
                distribution.set(projectLicenseUrl)
            }
        }
        scm {
            url.set(projectBaseScmUrl + "ktor-openapi")
            connection.set(projectBaseScmConnection + "ktor-openapi.git")
        }
        developers {
            developer {
                id.set(projectDeveloperName)
                name.set(projectDeveloperName)
                url.set(projectDeveloperUrl)
            }
        }
    }
}
