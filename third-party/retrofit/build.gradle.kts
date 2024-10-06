import se.ansman.dagger.auto.gradle.setupResourceTests

plugins {
    kotlin("jvm")
    id("library.api")
    id("library.publishing")
}

tasks.compileTestKotlin {
    compilerOptions.freeCompilerArgs.addAll(
        "-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi",
    )
}

setupResourceTests()

dependencies {
    implementation(libs.dagger.hilt.core)
    testImplementation(testFixtures(projects.compiler))
    testImplementation(libs.retrofit)
}