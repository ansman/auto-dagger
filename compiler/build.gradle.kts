plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("library")
    id("library.publishing")
    id("com.github.johnrengelman.shadow")
}

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

val shade by configurations.named("compileShaded")
dependencies {
    shade(libs.kotlinx.metadata) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
    }
    implementation(libs.javapoet)
    implementation(libs.bundles.kotlinpoet)
    implementation(libs.dagger)
    implementation(libs.dagger.hilt.core)
    implementation(libs.ksp.api)
    implementation(projects.core)
    implementation(projects.android.testing)
    implementation(libs.auto.common)

    implementation(libs.auto.service.annotations)
    kapt(libs.auto.service)

    implementation(libs.incap)
    kapt(libs.incap.compiler)

    testImplementation(libs.bundles.compileTesting)
    testImplementation(libs.okio)
}