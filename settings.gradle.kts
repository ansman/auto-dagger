pluginManagement {
    @Suppress("UnstableApiUsage")
    includeBuild("gradle-plugin")

    repositories {
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
includeBuild("gradle-plugin")

rootProject.name = "auto-dagger"

include(":core")
include(":android")
include(":android:testing")
include(":compiler")
include(":compiler:common")
include(":compiler:common:test-utils")
include(":tests")