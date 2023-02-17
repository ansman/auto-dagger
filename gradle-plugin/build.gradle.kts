plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    `kotlin-dsl`
    `version-catalog`
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    api(libs.android.gradlePlugin)
    api(libs.dokka.gradlePlugin)
    api(libs.testLogger)
    api(libs.kotlin.jvm.gradle)
}