plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("library")
    id("library.publishing")
}

dependencies {
    implementation(libs.javapoet)
    implementation(libs.bundles.kotlinpoet)
    implementation(libs.dagger)
    implementation(libs.dagger.hilt.core)
    implementation(libs.dagger.hilt.compiler)
    implementation(libs.ksp.api)
    api(projects.core)
    api(libs.auto.common)

    implementation(libs.auto.service.annotations)
    kapt(libs.auto.service)

    implementation(libs.incap)
    kapt(libs.incap.compiler)

    testImplementation(libs.bundles.compileTesting)
    testImplementation(libs.okio)
}