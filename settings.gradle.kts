pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        google()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "auto-dagger"

include(":android")
include(":android:api")
include(":android:testing")
include(":compiler")
include(":compiler:common")
include(":core")
include(":tests")
include(":third-party:androidx:room")
include(":third-party:androidx:viewmodel")
include(":third-party:ktorfit")
include(":third-party:retrofit")