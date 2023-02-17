import com.google.devtools.ksp.gradle.KspTask
import com.google.devtools.ksp.gradle.KspTaskJvm
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

android.sourceSets.configureEach {
    idea.module.sourceDirs.add(buildDir.resolve("generated/ksp/$name/kotlin"))
}

// This is needed because Dagger/Hilt doesn't support KSP yet so for our generated code to be
// seen we need to make the output from KSP an input to KAPT
afterEvaluate {
    tasks.withType<Kapt> {
        val ksp = tasks.findByName(name.replace("kapt", "ksp")) as KspTaskJvm?
            ?: return@withType
        kaptExternalClasspath.from(ksp.destination)
    }
    tasks.withType<KaptGenerateStubsTask> {
        val ksp = tasks.findByName(name.replace("kaptGenerateStubs", "ksp")) as KspTaskJvm?
            ?: return@withType
        kaptClasspath.from(ksp.destination)
    }
}

dependencies {
    implementation(projects.core)
    implementation(libs.dagger.hilt.android)
    "kaptJava"(projects.compiler)
    "kspKotlin"(projects.compiler)
    kapt(libs.dagger.compiler)
    kapt(libs.dagger.hilt.compiler)

    // Unit test
    testImplementation(libs.dagger.hilt.android.testing)
    kaptTest(libs.dagger.compiler)
    kaptTest(libs.dagger.hilt.compiler)

    // Android test
    androidTestImplementation(projects.android)
    androidTestImplementation(libs.dagger.hilt.android.testing)
    kaptAndroidTest(libs.dagger.compiler)
    kaptAndroidTest(libs.dagger.hilt.compiler)
    "kaptAndroidTestJava"(projects.compiler)
    "kspAndroidTestKotlin"(projects.compiler)
}