import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.DokkaTask

val projectGroupId: String by project
val projectVersion: String by project
group = projectGroupId
version = projectVersion

plugins {
    kotlin("jvm")
    id("org.owasp.dependencycheck")
    id("com.github.ben-manes.versions")
    id("io.gitlab.arturbosch.detekt")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

dependencies {
    val versionKtor: String by project
    implementation("io.ktor:ktor-server-core-jvm:$versionKtor")
    implementation("io.ktor:ktor-server-content-negotiation:$versionKtor")
    testImplementation("io.ktor:ktor-server-netty-jvm:$versionKtor")
    testImplementation("io.ktor:ktor-server-content-negotiation:$versionKtor")
    testImplementation("io.ktor:ktor-serialization-jackson:$versionKtor")
    testImplementation("io.ktor:ktor-server-test-host:$versionKtor")

    val versionRedoc: String by project
    implementation("org.webjars:redoc:$versionRedoc")

    val versionKotest: String by project
    testImplementation("io.kotest:kotest-runner-junit5:$versionKotest")
    testImplementation("io.kotest:kotest-assertions-core:$versionKotest")

    val versionKotlinTest: String by project
    testImplementation("org.jetbrains.kotlin:kotlin-test:$versionKotlinTest")
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

detekt {
    ignoreFailures = false
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/../detekt/detekt.yml")
}
tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        md.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
    }
}

mavenPublishing {
    val projectGroupId: String by project
    val projectVersion: String by project
    val projectScmUrl: String by project
    val projectScmConnection: String by project
    val projectLicenseName: String by project
    val projectLicenseUrl: String by project
    val projectDeveloperName: String by project
    val projectDeveloperUrl: String by project

    configure(KotlinJvm(JavadocJar.Dokka("dokkaHtml"), true))
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    coordinates(projectGroupId, "ktor-redoc", projectVersion)
    pom {
        name.set("Ktor Redoc")
        description.set("Ktor plugin to provide Redoc")
        url.set(projectScmUrl)
        licenses {
            license {
                name.set(projectLicenseName)
                url.set(projectLicenseUrl)
                distribution.set(projectLicenseUrl)
            }
        }
        scm {
            url.set(projectScmUrl)
            connection.set(projectScmConnection)
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

tasks.withType<DokkaTask>().configureEach {
    outputDirectory.set(file("$rootDir/docs/dokka/ktor-redoc"))
}
