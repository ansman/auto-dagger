import se.ansman.dagger.auto.gradle.setupResourceTests

plugins {
    kotlin("jvm")
    id("library.api")
    id("library.publishing")
}

setupResourceTests()

dependencies {
    implementation(libs.dagger.hilt.core)
    testImplementation(testFixtures(projects.compiler))
}