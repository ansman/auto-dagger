plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("library.api")
    id("library.publishing")
}

dependencies {
    api(projects.core)
    implementation(libs.dagger)
    implementation(libs.dagger.hilt.android)
    compileOnly(libs.androidx.startup)
    kapt(libs.dagger.compiler)
    kapt(libs.dagger.hilt.compiler)
}