@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.lint.AndroidLintAnalysisTask
import com.google.devtools.ksp.gradle.KspTask

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.ksp)
    id("library")
    id("com.google.dagger.hilt.android")
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    flavorDimensions += "type"
    productFlavors {
        create("java") {
            dimension = "type"
        }

        create("kotlin") {
            dimension = "type"
        }
    }
    sourceSets {
        configureEach {
            java.srcDir(buildDir.resolve("generated/ksp/$name/kotlin"))
            if (name.startsWith("test")) {
                java.srcDir(buildDir.resolve("generated/ksp/${name.removePrefix("test").replaceFirstChar(Char::lowercaseChar)}UnitTest/kotlin"))
            }
        }
    }
}

androidComponents {
    beforeVariants { variant ->
        with(variant) {
            enableAndroidTest = variant.buildType == "debug"
            enableTestFixtures = false
            enableUnitTest = variant.buildType == "debug"
        }
    }
}

dependencies {
    implementation(projects.android)
    implementation(projects.androidx.viewmodel)
    implementation(projects.androidx.room)
    implementation(projects.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(projects.android.testing)
    implementation(libs.dagger.hilt.android)
    "kaptJava"(projects.compiler)
    "kspKotlin"(projects.compiler)
    "kaptJava"(libs.dagger.compiler)
    "kspKotlin"(libs.dagger.compiler)
    "kaptJava"(libs.dagger.hilt.compiler)
    "kspKotlin"(libs.dagger.hilt.compiler)

    // Third party
    implementation(libs.retrofit)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.room)
    ksp(libs.room.compiler)

    // Unit test
    testImplementation(libs.dagger.hilt.android.testing)
    "kaptTestJava"(projects.compiler)
    "kspTestKotlin"(projects.compiler)
    // Compile Only is used here to ensure it's included by the android module
    testCompileOnly(libs.androidx.startup)
    "kaptTestJava"(libs.dagger.compiler)
    "kspTestKotlin"(libs.dagger.compiler)
    "kaptTestJava"(libs.dagger.hilt.compiler)
    "kspTestKotlin"(libs.dagger.hilt.compiler)
}

tasks.withType<AndroidLintAnalysisTask>().configureEach {
    dependsOn(tasks.withType<KspTask>())
}