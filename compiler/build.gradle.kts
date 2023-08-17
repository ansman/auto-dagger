plugins {
    id("library.compiler")
    id("library.publishing")
}

dependencies {
    implementation(projects.core)
    implementation(projects.android.testing)
}