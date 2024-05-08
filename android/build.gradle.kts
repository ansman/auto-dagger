plugins {
    id("com.android.library")
    kotlin("android")
    id("library.api")
    id("library.publishing")
}

dependencies {
    implementation(libs.androidx.startup)
    api(projects.android.api)
}