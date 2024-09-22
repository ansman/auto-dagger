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
    alias(libs.plugins.shadow) apply false
}

buildscript {
    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.dokka.gradlePlugin)
        classpath(libs.dokka.versioningPlugin)
    }
}

group = "se.ansman.dagger.auto"
val version: String = providers.gradleProperty("version").get()
    .also(::setVersion)

val latestRelease: String = providers.gradleProperty("latestRelease").get()

val isSnapshotVersion = version.endsWith("-SNAPSHOT")
val dokkaProjects = subprojects
    .filter { ":compiler" !in it.path && ":tests" !in it.path }
    .toSet()

apiValidation {
    ignoredPackages.addAll(
        setOf(
            "hilt_aggregated_deps",
            "dagger.hilt"
        )
    )
    ignoredClasses.addAll(
        setOf(
            "se.ansman.dagger.auto.HiltWrapper_AutoDaggerInitializeModule",
            "se.ansman.dagger.auto.android.HiltWrapper_AutoDaggerInitializerEntryPoint",
        )
    )
    ignoredProjects.addAll(
        subprojects.asSequence()
            .minus(dokkaProjects)
            .map { it.name }
    )
    apiDumpDirectory = "src/main/api"
}

val dokkaDocsPath = "dokka"

mkdocs {
    updateSiteUrl = false
    strict = providers.gradleProperty("strictDocs").orNull?.toBoolean() ?: true
    publish.apply {
        repoDir = layout.buildDirectory.dir("gh-pages").get().asFile.path
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
        "daggerVersion" to libs.versions.dagger.get(),
        "snapshotVersion" to version.replace(Regex("(\\d+)\\.(\\d+)\\.(\\d+)(?:-(.+))?")) {
            val (major, minor, _, qualifier) = it.destructured
            if (qualifier == "SNAPSHOT") {
                it.groupValues[0]
            } else {
                "$major.${minor.toInt() + 1}.0-SNAPSHOT"
            }
        },
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
    pip(
        "pip:23.2.1",
        "mkdocs:1.5.3",
        "mkdocs-material:9.1.1",
        "mkdocs-minify-plugin:0.7.1",
        "Pygments:2.16.1",
        "pymdown-extensions:10.3",
        "mkdocs-markdownextradata-plugin:0.2.5"
    )
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
