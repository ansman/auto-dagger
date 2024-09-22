
import se.ansman.dagger.auto.gradle.setupResourceTests

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("library")
    id("library.publishing")
    id("com.gradleup.shadow")
    `java-test-fixtures`
}

setupResourceTests()

dependencies {
    implementation(libs.javapoet)
    implementation(libs.bundles.kotlinpoet)
    implementation(libs.dagger)
    implementation(libs.dagger.hilt.core)
    implementation(libs.ksp.api)
    implementation(libs.auto.common)

    implementation(projects.core)
    implementation(projects.android.testing)
    implementation(projects.thirdParty.androidx.room)
    implementation(projects.thirdParty.ktorfit)
    implementation(projects.thirdParty.retrofit)

    implementation(projects.compiler.common)
    compileShaded(libs.kotlinx.metadata) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
    }

    implementation(libs.auto.service.annotations)
    kapt(libs.auto.service)

    implementation(libs.incap)
    kapt(libs.incap.compiler)

    testFixturesApi(libs.bundles.jvmTesting)
    testFixturesApi(libs.dagger)
    testFixturesApi(libs.dagger.hilt.core)
    testFixturesApi(libs.bundles.compileTesting)
}