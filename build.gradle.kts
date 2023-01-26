import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin

plugins {
    `version-catalog`
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.binaryCompatibilityValidator)
    alias(libs.plugins.mkdocs)
}

buildscript {
    dependencies {
        classpath(libs.kotlin.jvm.gradle)
        classpath(libs.android.gradlePlugin)
        classpath(libs.dagger.hilt.gradlePlugin)
        classpath(libs.dokka.versioningPlugin)
    }
}

group = "se.ansman.deager"
val version: String = providers.gradleProperty("version").get()
    .also(::setVersion)

val latestRelease: String = providers.gradleProperty("latestRelease").get()

val isSnapshotVersion = version.endsWith("-SNAPSHOT")
val dokkaProjects = setOf(projects.core.dependencyProject, projects.android.dependencyProject)

apiValidation {
    ignoredPackages.addAll(setOf(
        "hilt_aggregated_deps",
        "dagger.hilt"
    ))
    ignoredClasses.addAll(setOf(
        "se.ansman.deager.HiltWrapper_DeagerModule",
        "se.ansman.deager.android.HiltWrapper_DeagerInitializerEntryPoint",
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
        "dokkaLink" to "/$dokkaDocsPath/older/$latestRelease"
    )
}

gitPublish {
    contents {
        from(tasks.dokkaHtmlMultiModule.map { it.outputDirectory }) {
            into(dokkaDocsPath)
        }
    }
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
    dependsOn(gradle.includedBuilds.map { it.task(":check") })
    dependsOn(tasks.mkdocsBuild)
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}

dependencies {
    dokkaPlugin(libs.dokka.versioningPlugin)
    dokkaPlugin(libs.dokka.allModulesPagePlugin)
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

tasks.dokkaHtmlMultiModule {
    removeChildTasks(subprojects - dokkaProjects)
    pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
        version = providers.gradleProperty("version").get()
        olderVersionsDir = olderVersionsFolder
    }
}