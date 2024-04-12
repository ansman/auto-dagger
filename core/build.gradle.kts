plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("library.api")
    id("library.publishing")
}

dependencies {
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.dagger.hilt.core)
    kapt(libs.dagger.hilt.compiler)
}