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
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
includeBuild("gradle-plugin")

rootProject.name = "deager"

include(":core")
include(":android")
include(":compiler")
include(":tests")