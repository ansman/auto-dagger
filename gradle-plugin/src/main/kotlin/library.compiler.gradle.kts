
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("library")
}

val libs = the<LibrariesForLibs>()

tasks.withType<Test>().configureEach {
    jvmArgs(
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

dependencies {
    implementation(project(":compiler:common", configuration = "shadow"))
    implementation(libs.javapoet)
    implementation(libs.bundles.kotlinpoet)
    implementation(libs.dagger)
    implementation(libs.dagger.hilt.core)
    implementation(libs.ksp.api)
    api(libs.auto.common)

    implementation(libs.auto.service.annotations)
    kapt(libs.auto.service)

    implementation(libs.incap)
    kapt(libs.incap.compiler)

    testImplementation(project(":compiler:common:test-utils"))
}