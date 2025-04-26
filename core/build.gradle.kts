plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
    id("library.api")
    id("library.publishing")
}

dependencies {
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)
    implementation(libs.dagger.hilt.core)
    ksp(libs.dagger.hilt.compiler)
}