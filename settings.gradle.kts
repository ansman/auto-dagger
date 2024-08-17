pluginManagement {
    includeBuild("gradle-plugin")

    repositories {
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

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
include(":compiler")
include(":compiler:common")
include(":compiler:common:test-utils")
include(":android")
include(":android:api")
include(":android:testing")
include(":third-party:retrofit")
include(":third-party:ktorfit")
include(":third-party:androidx:viewmodel")
include(":third-party:androidx:room")
include(":tests")