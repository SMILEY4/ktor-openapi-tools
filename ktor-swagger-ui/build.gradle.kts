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
    implementation(libs.ktor.server.contentnegotiation)

    testImplementation(libs.ktor.server.netty.jvm)
    testImplementation(libs.ktor.server.serialization.jackson)
    testImplementation(libs.ktor.server.test.host)

    implementation(libs.webjars.swaggerui)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.test)
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(file("$rootDir/docs/dokka/ktor-swagger-ui"))
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
    coordinates(projectGroupId, "ktor-swagger-ui", projectVersion)
    pom {
        name.set("Ktor Swagger-UI")
        description.set("Ktor plugin to provide a Swagger-UI")
        url.set(projectBaseScmUrl + "ktor-swagger-ui")
        licenses {
            license {
                name.set(projectLicenseName)
                url.set(projectLicenseUrl)
                distribution.set(projectLicenseUrl)
            }
        }
        scm {
            url.set(projectBaseScmUrl + "ktor-swagger-ui")
            connection.set(projectBaseScmConnection + "ktor-swagger-ui.git")
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
