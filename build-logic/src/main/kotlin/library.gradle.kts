@file:Suppress("UnstableApiUsage")

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.Lint
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.HasAndroidTestBuilder
import com.android.build.api.variant.HasTestFixturesBuilder
import com.android.build.api.variant.HasUnitTestBuilder
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
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
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
        allWarningsAsErrors = true
        freeCompilerArgs.addAll(
            "-Xsuppress-version-warnings",
        )
    }
}

tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    maxHeapSize = "1g"
    jvmArgs(
        "-Djava.awt.headless=true",
        "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
    )
    if (providers.gradleProperty("updateExpectedTestFiles").orNull?.toBoolean() == true) {
        systemProperty("writeExpectedFilesTo", file("src/test/resources/tests"))
    }
}

fun Lint.configure() {
    abortOnError = true
    warningsAsErrors = true
    checkReleaseBuilds = false
    disable.addAll(
        setOf(
            "GradleDependency",
            "AndroidGradlePluginVersion",
            "MissingApplicationIcon",
        )
    )
}

pluginManager.withPlugin("com.android.base") {
    extensions.getByType(CommonExtension::class).apply {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        defaultConfig {
            minSdk = libs.versions.android.minSdk.get().toInt()
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        namespace = (rootProject.group as String) + project.path
            .removePrefix(":third-party")
            .replace(':', '.')
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
        lint.configure()
    }

    extensions.getByType(AndroidComponentsExtension::class).apply {
        beforeVariants(selector().withBuildType("release")) { variant ->
            (variant as? HasAndroidTestBuilder)?.androidTest?.enable = false
            (variant as? HasTestFixturesBuilder)?.enableTestFixtures = false
            (variant as? HasUnitTestBuilder)?.enableUnitTest = false
        }
    }

    dependencies {
        "testImplementation"(libs.bundles.androidTesting)
        "testImplementation"(libs.robolectric)
        "androidTestImplementation"(libs.bundles.androidTesting)
    }
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    pluginManager.apply("com.android.lint")
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
    configure<KotlinJvmProjectExtension> {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
            vendor.set(JvmVendorSpec.AZUL)
        }
    }
    dependencies {
        "testImplementation"(libs.bundles.jvmTesting)
    }
    the<Lint>().configure()
}

plugins.withType<JavaBasePlugin>().configureEach {
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
            vendor.set(JvmVendorSpec.AZUL)
        }
    }
}

configurations.configureEach {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion(libs.versions.kotlin.get())
            }
        }
    }
}
