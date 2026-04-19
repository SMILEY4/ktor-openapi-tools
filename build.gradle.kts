import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.dependencycheck) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.versions) apply false
    alias(libs.plugins.mkdocs)
}

subprojects {

    val projectGroupId: String by project
    val projectVersion: String by project
    group = projectGroupId
    version = projectVersion

    repositories {
        mavenCentral()
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {

        val versionJvmCompile = libs.versions.jvm.compile.get().toInt()
        val versionJvmTarget = libs.versions.jvm.target.get()

        // Kotlin Toolchain
        extensions.configure<KotlinJvmProjectExtension> {
            jvmToolchain(versionJvmCompile)
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(versionJvmTarget))
            }
        }

        // Java Toolchain
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(versionJvmCompile))
            }
        }

        // JVM Compatibility
        tasks.withType<JavaCompile>().configureEach {
            sourceCompatibility = versionJvmTarget
            targetCompatibility = versionJvmTarget
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    tasks.withType<Detekt>().configureEach {
        ignoreFailures = false
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom("$rootDir/detekt/detekt.yml")
        reports {
            html.required.set(true)
            md.required.set(true)
            xml.required.set(false)
            txt.required.set(false)
            sarif.required.set(false)
        }
    }

}

mkdocs {
    sourcesDir = "."
    buildDir = "./build/mkdocs"
    updateSiteUrl = true
    publish {
        branch = "gh-pages"
        version = "5.x"
        rootRedirect = true
        rootRedirectTo = "latest"
        setVersionAliases("latest")
        generateVersionsFile = true
    }
    python {
        minPythonVersion = "3.12"
    }
}
