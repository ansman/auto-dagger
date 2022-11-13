@file:Suppress("UnstableApiUsage")

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val libs = the<LibrariesForLibs>()

pluginManager.apply("com.adarshr.test-logger")

version = properties.getValue("version") as String

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        allWarningsAsErrors = true
    }
}

tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2

}

pluginManager.withPlugin("com.android.library") {
    configure<LibraryExtension> {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        defaultConfig {
            minSdk = libs.versions.android.minSdk.get().toInt()
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        namespace = (rootProject.group as String) + project.path.replace(':', '.')
        buildFeatures {
            buildConfig = false
            resValues = false
        }
        testOptions {
            unitTests {
                isIncludeAndroidResources = true
            }
        }
    }

    configure<LibraryAndroidComponentsExtension> {
        beforeVariants(selector().withBuildType("release")) { variant ->
            with(variant) {
                enableAndroidTest = false
                enableTestFixtures = false
                enableUnitTest = false
            }
        }
    }

    dependencies {
        "testImplementation"(libs.bundles.androidTesting)
        "testImplementation"(libs.robolectric)
        "androidTestImplementation"(libs.bundles.androidTesting)
    }
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    tasks.withType<Test> {
        useJUnitPlatform()
        systemProperties(
            "junit.jupiter.execution.parallel.enabled" to "true",
            "junit.jupiter.execution.parallel.config.strategy"  to "dynamic",
            "junit.jupiter.execution.parallel.config.dynamic.factor"  to "1",
            "junit.jupiter.execution.parallel.mode.default"  to "concurrent",
            "junit.jupiter.execution.parallel.mode.classes.default"  to "concurrent",
        )
    }
    dependencies {
        "testImplementation"(libs.bundles.jvmTesting)
    }
}