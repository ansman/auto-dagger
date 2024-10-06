import se.ansman.dagger.auto.gradle.BuildConfigTask

plugins {
    kotlin("jvm")
    id("library.api")
    id("library.publishing")
    `java-gradle-plugin`
}

val buildConfig by tasks.registering(BuildConfigTask::class) {
    property("group", project.group.toString())
    property("version", project.version.toString())
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir(buildConfig)
        }
    }
}

gradlePlugin {
    plugins {
        create("auto-dagger") {
            id = "se.ansman.auto-dagger"
            implementationClass = "se.ansman.dagger.auto.gradle.plugin.AutoDaggerGradlePlugin"
        }
    }

}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
}