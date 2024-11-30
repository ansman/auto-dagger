plugins {
    java
    kotlin("jvm")
    id("library")
    id("dokka-common")
}

dependencies {
    api(projects.compiler.common)
    api(libs.bundles.jvmTesting)
    api(libs.dagger.hilt.core)
    api(libs.kotlin.metadata)
    api(libs.ksp.api)
    api(libs.bundles.compileTesting)
    api(libs.okio)
    runtimeOnly(libs.ksp)
    runtimeOnly(libs.kotlin.compiler.embeddable)
}