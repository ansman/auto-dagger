@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.tasks.Kapt

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.ksp)
    id("library")
    id("com.google.dagger.hilt.android")
    idea
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
    beforeVariants(selector().withBuildType("release")) { variant ->
        with(variant) {
            enableAndroidTest = false
            enableTestFixtures = false
            enableUnitTest = false
        }
    }
}

// This is needed because Dagger/Hilt doesn't support KSP yet so for our generated code to be
// seen we need to make KAPT depend on KSP
val variantUnitTestClassPaths = mutableMapOf<String, FileCollection>()
afterEvaluate {
    tasks.withType<Kapt>().configureEach {
        tasks.findByName(name.replace("kapt", "ksp"))
            ?.let { dependsOn(it) }
    }
    tasks.withType<KaptGenerateStubsTask>().configureEach {
        tasks.findByName(name.replace("kaptGenerateStubs", "ksp"))
            ?.let { dependsOn(it) }
    }
}

dependencies {
    implementation(projects.android)
    implementation(projects.android.testing)
    implementation(libs.dagger.hilt.android)
    "kaptJava"(projects.compiler)
    "kspKotlin"(projects.compiler)
    kapt(libs.dagger.compiler)
    kapt(libs.dagger.hilt.compiler)

    // Unit test
    testImplementation(libs.dagger.hilt.android.testing)
    "kaptTestJava"(projects.compiler)
    "kspTestKotlin"(projects.compiler)
    // Compile Only is used here to ensure it's included by the android module
    testCompileOnly(libs.androidx.startup)
    kaptTest(libs.dagger.compiler)
    kaptTest(libs.dagger.hilt.compiler)
}