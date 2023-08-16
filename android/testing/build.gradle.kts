plugins {
    kotlin("jvm")
    id("library")
    id("library.publishing")
}

dependencies {
    implementation(libs.dagger.hilt.core)
    api(projects.core)
}