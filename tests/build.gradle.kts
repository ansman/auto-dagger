plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("library")
    id("com.google.dagger.hilt.android")
}

dependencies {
    implementation(projects.core)
    implementation(libs.dagger.hilt.android)
    kapt(projects.compiler)
    testImplementation(libs.dagger.hilt.android.testing)
    kaptTest(libs.dagger.hilt.compiler)
}