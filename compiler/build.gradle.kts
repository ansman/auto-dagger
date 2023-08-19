plugins {
    id("library.compiler")
    id("library.publishing")
}

dependencies {
    implementation(projects.core)
    implementation(projects.retrofit)
    implementation(projects.android.testing)
    testImplementation(libs.retrofit)
}