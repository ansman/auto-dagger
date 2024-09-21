plugins {
    kotlin("jvm") version embeddedKotlinVersion
    `kotlin-dsl`
    `version-catalog`
    alias(libs.plugins.projectAccessors)
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.projectAccessors)
    api(libs.shadow)
    api(libs.android.gradlePlugin)
    api(libs.dokka.gradlePlugin)
    api(libs.testLogger)
    api(libs.kotlin.jvm.gradle)
    api(libs.sonatypePublishFix)
}