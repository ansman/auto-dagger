@file:Suppress("UnstableApiUsage")

import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    id("library")
    id("dokka-common")
}

val libs = the<LibrariesForLibs>()

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    configure<KotlinJvmProjectExtension> {
        explicitApi()
    }
}