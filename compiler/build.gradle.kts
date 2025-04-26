
import se.ansman.dagger.auto.gradle.setupResourceTests

plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
    id("library")
    id("library.publishing")
    `java-test-fixtures`
}

setupResourceTests()

dependencies {
    implementation(libs.javapoet)
    implementation(libs.bundles.kotlinpoet)
    implementation(libs.dagger)
    implementation(libs.dagger.hilt.core)
    implementation(libs.ksp.api)
    api(libs.auto.common)
    implementation(libs.kotlin.metadata)

    implementation(projects.core)
    implementation(projects.android.testing)
    implementation(projects.thirdParty.androidx.room)
    implementation(projects.thirdParty.ktorfit)
    implementation(projects.thirdParty.retrofit)
    implementation(projects.compiler.common)

    implementation(libs.auto.service.annotations)
    ksp(libs.auto.service)

    testFixturesApi(libs.bundles.jvmTesting)
    testFixturesApi(libs.dagger)
    testFixturesApi(libs.dagger.hilt.core)
    testFixturesApi(libs.bundles.compileTesting)
    testFixturesApi(projects.compiler.common)
}