plugins {
    kotlin("jvm")
    id("library.api")
    id("library.publishing")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.dagger.hilt.core)
    testImplementation(testFixtures(projects.compiler))
    testImplementation(libs.retrofit)
}