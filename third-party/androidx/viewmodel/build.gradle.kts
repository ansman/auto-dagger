import se.ansman.dagger.auto.gradle.setupResourceTests

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("library.api")
    id("library.publishing")
}

setupResourceTests()

dependencies {
    api(projects.core)
    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.lifecycle.viewmodel)
    kapt(libs.dagger.hilt.compiler)
}
