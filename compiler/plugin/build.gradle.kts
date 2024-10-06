plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("library")
    id("library.publishing")
}

kotlin {
    compilerOptions.freeCompilerArgs.addAll(
        "-opt-in=org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI",
        "-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi",
    )
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    implementation(projects.core)
    implementation(projects.compiler.common)

    implementation(libs.auto.service.annotations)
    kapt(libs.auto.service)

    testImplementation(libs.bundles.jvmTesting)
    testImplementation(platform(libs.asm.bom))
    testImplementation(libs.asm.core)
    testImplementation(libs.compileTesting)
    testImplementation(kotlin("reflect"))
    testImplementation(libs.okio)
}