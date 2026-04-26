plugins {
    id("com.android.library")
    id("library.api")
    id("library.publishing")
}

dependencies {
    implementation(libs.androidx.startup)
    api(projects.android.api)
}