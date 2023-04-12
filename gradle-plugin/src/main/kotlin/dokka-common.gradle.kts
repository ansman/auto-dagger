
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.dokka.gradle.AbstractDokkaTask

plugins {
    id("org.jetbrains.dokka")
}

val libs = the<LibrariesForLibs>()

dependencies {
    dokkaPlugin(libs.dokka.versioningPlugin)
}

tasks.named("dokkaHtmlPartial") {
    dependsOn("kaptKotlin")
}

tasks.withType<AbstractDokkaTask>().configureEach {
    notCompatibleWithConfigurationCache("Dokka does not support config cache yet")
}
