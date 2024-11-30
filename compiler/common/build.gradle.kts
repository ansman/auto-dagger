plugins {
    kotlin("jvm")
    id("library")
    id("library.publishing")
}

dependencies {
    compileOnly(libs.kotlin.metadata)
    compileOnly(libs.javapoet)
    compileOnly(libs.bundles.kotlinpoet)
    implementation(libs.dagger)
    implementation(libs.dagger.hilt.core)
}