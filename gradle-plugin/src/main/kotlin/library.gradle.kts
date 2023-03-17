@file:Suppress("UnstableApiUsage")

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val libs = the<LibrariesForLibs>()

val isRunningInIDE = (System.getProperty("idea.active")?.toBoolean() ?: false) ||
        (System.getProperty("idea.sync.active")?.toBoolean() ?: false)

if (!isRunningInIDE) {
    pluginManager.apply("com.adarshr.test-logger")
    extensions.configure<TestLoggerExtension> {
        theme = ThemeType.STANDARD_PARALLEL
    }
}

version = properties.getValue("version") as String

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        allWarningsAsErrors = true
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xcontext-receivers"
        )
    }
}

tasks.withType<Test>().configureEach {
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
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
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
    tasks.withType<Test>().configureEach {
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

plugins.withType<JavaBasePlugin>().configureEach {
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(19))
            vendor.set(JvmVendorSpec.AZUL)
        }
    }
}
