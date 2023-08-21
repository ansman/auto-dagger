plugins {
    kotlin("jvm")
    id("library.api")
    id("library.publishing")
}

dependencies {
    implementation(libs.dagger.hilt.core)
}