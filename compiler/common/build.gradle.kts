plugins {
    kotlin("jvm")
    id("library")
    id("library.publishing")
}

dependencies {
    compileOnly(libs.kotlinx.metadata)
    compileOnly(libs.javapoet)
    compileOnly(libs.bundles.kotlinpoet)
    api(libs.dagger)
    api(libs.dagger.hilt.core)
    api(projects.core)

}