@file:Suppress("UnstableApiUsage")

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
    .map { it.isolated }
    .filter { ":compiler" !in it.path && ":tests" !in it.path }
    .toSet()

apiValidation {
    ignoredPackages.addAll(
        setOf(
            "hilt_aggregated_deps",
            "dagger.hilt"
        )
    )
    nonPublicMarkers.addAll(
        setOf(
            "javax.annotation.processing.Generated",
            "dagger.hilt.codegen.OriginatingElement",
            "dagger.internal.DaggerGenerated",
        )
    )
    ignoredProjects.addAll(
        subprojects
            .map { it.isolated }
            .asSequence()
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
        from(tasks.dokkaGeneratePublicationHtml.map { it.outputDirectory }) {
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
    dependsOn(tasks.dokkaGeneratePublicationHtml)
}

python {
    virtualenvVersion = "20.16.7"
}

tasks.check {
    dependsOn(*gradle.includedBuilds.map { it.task(":check") }.toTypedArray())
    dependsOn(tasks.mkdocsBuild)
}

val olderVersionsFolder = file("src/doc/dokka/")
tasks.register<Copy>("versionCurrentDocs") {
    enabled = !isSnapshotVersion
    from(tasks.dokkaGeneratePublicationHtml)
    into(olderVersionsFolder.resolve(version))
    exclude("older")
}

dependencies {
    dokkaPlugin(libs.dokka.allModulesPagePlugin)
    dokkaPlugin(libs.dokka.versioningPlugin)
    dokka(projects.android)
    dokka(projects.android.api)
    dokka(projects.android.testing)
    dokka(projects.core)
    dokka(projects.thirdParty.androidx.room)
    dokka(projects.thirdParty.androidx.viewmodel)
    dokka(projects.thirdParty.ktorfit)
    dokka(projects.thirdParty.retrofit)
}

dokka {
    pluginsConfiguration {
        versioning {
            version = providers.gradleProperty("version").get()
            olderVersionsDir = olderVersionsFolder
        }
    }
}

tasks.withType<BasePythonTask>().configureEach {
    notCompatibleWithConfigurationCache("mkdocs isn't compatible with the config cache")
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}
