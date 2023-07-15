import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin
import ru.vyarus.gradle.plugin.python.task.BasePythonTask

plugins {
    `version-catalog`
    kotlin("jvm") version libs.versions.kotlin.get() apply false
    // Needed as a workaround for https://github.com/google/dagger/issues/3965
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.dagger.hilt) apply false
    id("dokka-common")
    alias(libs.plugins.binaryCompatibilityValidator)
    alias(libs.plugins.mkdocs)
}

buildscript {
    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.dokka.gradlePlugin)
        classpath(libs.dokka.versioningPlugin)
        classpath(libs.shadow)
    }
}

group = "se.ansman.dagger.auto"
val version: String = providers.gradleProperty("version").get()
    .also(::setVersion)

val latestRelease: String = providers.gradleProperty("latestRelease").get()

val isSnapshotVersion = version.endsWith("-SNAPSHOT")
val dokkaProjects = setOf(
    projects.core.dependencyProject,
    projects.android.dependencyProject,
    projects.android.testing.dependencyProject,
)

apiValidation {
    ignoredPackages.addAll(setOf(
        "hilt_aggregated_deps",
        "dagger.hilt"
    ))
    ignoredClasses.addAll(setOf(
        "se.ansman.dagger.auto.HiltWrapper_AutoDaggerInitializeModule",
        "se.ansman.dagger.auto.android.HiltWrapper_AutoDaggerInitializerEntryPoint",
    ))
    ignoredProjects.addAll(
        subprojects.asSequence()
            .minus(dokkaProjects)
            .map { it.name }
        )
}

val dokkaDocsPath = "dokka"

mkdocs {
    updateSiteUrl = false
    strict = providers.gradleProperty("strictDocs").orNull?.toBoolean() ?: true
    publish.apply {
        repoDir = project.buildDir.resolve("gh-pages").path
        if (isSnapshotVersion) {
            // We don't want to "store" the latest so instead of using an alias we simply use "latest" as the doc path
            docPath = "latest"
        } else {
            setVersionAliases("latest")
        }
        rootRedirectTo = "latest"
    }
    extras = mapOf(
        "version" to latestRelease,
        "snapshotVersion" to version,
        "dokkaLink" to "/$dokkaDocsPath" + if (latestRelease == version) {
            "/"
        } else {
            "/older/$latestRelease/"
        }
    )
}

gitPublish {
    contents {
        from(tasks.dokkaHtmlMultiModule.map { it.outputDirectory }) {
            into(dokkaDocsPath)
        }
    }
}

python {
    pip("pip:23.0.1")
}

tasks.mkdocsBuild {
    doFirst {
        delete(mkdocs.buildDir)
    }
}

tasks.gitPublishReset {
    dependsOn(tasks.dokkaHtmlMultiModule)
}

python {
    virtualenvVersion = "20.16.7"
}

tasks.register("check") {
    dependsOn(*gradle.includedBuilds.map { it.task(":check") }.toTypedArray())
    dependsOn(tasks.mkdocsBuild)
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}

val olderVersionsFolder = file("src/doc/dokka/")
val versionCurrentDocs by tasks.registering(DokkaMultiModuleTask::class) {
    enabled = !isSnapshotVersion
    outputDirectory.set(olderVersionsFolder.resolve(version))
    pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
        version = providers.gradleProperty("version").get()
    }
    addChildTasks(dokkaProjects, "dokkaHtmlPartial")
}

dependencies {
    dokkaPlugin(libs.dokka.allModulesPagePlugin)
}

tasks.dokkaHtmlMultiModule {
    removeChildTasks(subprojects - dokkaProjects)
    pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
        version = providers.gradleProperty("version").get()
        olderVersionsDir = olderVersionsFolder
    }
}

tasks.withType<BasePythonTask>().configureEach {
    notCompatibleWithConfigurationCache("mkdocs isn't compatible with the config cache")
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}