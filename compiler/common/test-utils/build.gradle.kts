plugins {
    java
    kotlin("jvm")
    id("library")
    id("dokka-common")
}

dependencies {
    api(projects.compiler.common.apply { targetConfiguration = "shadow" })
    api(libs.bundles.jvmTesting)
    api(libs.dagger.hilt.core)
    api(libs.kotlinx.metadata)
    api(libs.ksp.api)
    api(libs.bundles.compileTesting)
    api(libs.okio)
}