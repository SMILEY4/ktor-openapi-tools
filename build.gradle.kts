plugins {
    kotlin("jvm") version "2.0.21"
    id("org.jetbrains.dokka") version "1.9.20" apply false
    id("org.owasp.dependencycheck") version "8.2.1" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.0" apply false
    id("com.vanniktech.maven.publish") version "0.28.0" apply false
    id("com.github.ben-manes.versions") version "0.51.0" apply false
    id("ru.vyarus.mkdocs") version "4.0.1"
}

repositories {
    mavenCentral()
}

mkdocs {
    sourcesDir = "."
    buildDir = "./build/mkdocs"
    updateSiteUrl = true
    publish {
        version = "5.0"
        rootRedirect = true
        rootRedirectTo = "latest"
        setVersionAliases("latest")
        generateVersionsFile = true
    }
    python {
        minPythonVersion = "3.12"
    }
}
