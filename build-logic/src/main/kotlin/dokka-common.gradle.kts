
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("org.jetbrains.dokka")
}

val libs = the<LibrariesForLibs>()

dependencies {
    dokkaPlugin(libs.dokka.versioningPlugin)
}