@file:Suppress("UnstableApiUsage")

import com.android.build.api.variant.HasAndroidTestBuilder
import com.android.build.api.variant.HasTestFixturesBuilder
import com.android.build.api.variant.HasUnitTestBuilder
import com.android.build.gradle.internal.lint.AndroidLintAnalysisTask
import com.google.devtools.ksp.gradle.KspTask
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

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
        minSdk = 21
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
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

kotlin {
    compilerOptions {
        // Needed until Hilt supports Kotlin 2.1:
        // https://github.com/google/dagger/issues/4451
        languageVersion = KotlinVersion.KOTLIN_2_0
    }
}

androidComponents {
    beforeVariants { variant ->
        (variant as? HasAndroidTestBuilder)?.androidTest?.enable = variant.buildType == "debug"
        (variant as? HasTestFixturesBuilder)?.enableTestFixtures = false
        (variant as? HasUnitTestBuilder)?.enableUnitTest = variant.buildType == "debug"
    }
}

ksp {
    arg("room.generateKotlin", "true")
}

dependencies {
    implementation(libs.retrofit.moshi)
    implementation(libs.dagger.hilt.android)
    coreLibraryDesugaring(libs.android.desugar)
    "kaptJava"(projects.compiler)
    "kspKotlin"(projects.compiler)
    "kaptJava"(libs.dagger.compiler)
    "kspKotlin"(libs.dagger.compiler)
    "kaptJava"(libs.dagger.hilt.compiler)
    "kspKotlin"(libs.dagger.hilt.compiler)

    implementation(projects.android.testing)
    implementation(projects.android)
    implementation(projects.thirdParty.androidx.viewmodel)
    implementation(projects.thirdParty.androidx.room)
    implementation(projects.thirdParty.retrofit)
    implementation(projects.thirdParty.ktorfit)
    implementation(libs.retrofit)
    implementation(libs.ktorfit)
    ksp(libs.ktorfit.ksp)
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