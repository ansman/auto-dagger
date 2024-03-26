
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("library")
}

val libs = the<LibrariesForLibs>()

tasks.withType<Test>().configureEach {
    if (providers.gradleProperty("updateExpectedTestFiles").orNull?.toBoolean() == true) {
        systemProperty("writeExpectedFilesTo", file("src/test/resources/tests"))
    }
}

dependencies {
    implementation(project(":compiler:common", configuration = "shadow"))
    implementation(libs.javapoet)
    implementation(libs.bundles.kotlinpoet)
    implementation(libs.dagger)
    implementation(libs.dagger.hilt.core)
    implementation(libs.ksp.api)
    api(libs.auto.common)

    implementation(libs.auto.service.annotations)
    kapt(libs.auto.service)

    implementation(libs.incap)
    kapt(libs.incap.compiler)

    testImplementation(project(":compiler:common:test-utils"))
}