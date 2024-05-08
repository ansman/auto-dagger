
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.dokka.gradle.AbstractDokkaTask

plugins {
    id("org.jetbrains.dokka")
}

val libs = the<LibrariesForLibs>()

dependencies {
    dokkaPlugin(libs.dokka.versioningPlugin)
}

tasks.withType<AbstractDokkaTask>().configureEach {
    notCompatibleWithConfigurationCache("Dokka does not support config cache yet")
}

pluginManager.withPlugin("org.jetbrains.kotlin.kapt") {
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        tasks.withType<AbstractDokkaTask>().configureEach {
            dependsOn("kaptKotlin")
        }
    }
    pluginManager.withPlugin("com.android.library") {
        tasks.withType<AbstractDokkaTask>().configureEach {
            dependsOn("kaptReleaseKotlin", "kaptDebugKotlin")
        }
    }
}
