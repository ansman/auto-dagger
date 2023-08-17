
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

plugins {
    kotlin("jvm")
    id("library")
    id("library.publishing")
    id("com.github.johnrengelman.shadow")
}

val compileShaded: Configuration by configurations.creating
configurations.named("compileOnly") { extendsFrom(compileShaded) }
configurations.named("testRuntimeOnly") { extendsFrom(compileShaded) }

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    configurations = listOf(compileShaded)
    isEnableRelocation = true
    relocationPrefix = "se.ansman.dagger.auto${project.path.replace(':', '.').replace('-', '_')}"
    transformers.add(ServiceFileTransformer())
}

artifacts {
    add("runtimeOnly", shadowJar)
}

dependencies {
    @Suppress("UnstableApiUsage")
    compileShaded(libs.kotlinx.metadata) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
    }

    implementation(libs.javapoet)
    implementation(libs.bundles.kotlinpoet)
    implementation(libs.dagger)
    implementation(libs.dagger.hilt.core)
    implementation(libs.ksp.api)
    implementation(libs.auto.common)
}