import com.android.build.gradle.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.internal.tasks.userinput.UserInputHandler
import org.gradle.configurationcache.extensions.serviceOf
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import se.ansman.dagger.auto.gradle.cachedProvider
import se.ansman.dagger.auto.gradle.execWithOutput
import se.ansman.dagger.auto.gradle.getOrPut
import se.ansman.dagger.auto.gradle.mapNullable

plugins {
    id("maven-publish")
    id("signing")
    id("dokka-common")
}

val libs = the<LibrariesForLibs>()

val gitCommit = cachedProvider {
    project.execWithOutput {
        commandLine("git", "rev-parse", "HEAD")
        workingDir = project.rootDir
    }.trim()
}

fun repo(path: String = "") = "https://github.com/ansman/auto-dagger$path"

val remoteSource: Provider<String> = providers.gradleProperty("version")
    .mapNullable { version -> version.takeUnless { it.endsWith("-SNAPSHOT") } }
    .orElse(gitCommit)
    .map { repo("/blob/$it") }

tasks.withType<AbstractDokkaLeafTask>().configureEach {
    moduleName.set(project.path.removePrefix(":").replace(':', '-'))
    val projectPath = project.path.removePrefix(":").replace(':', '/')
    dokkaSourceSets.configureEach {
        reportUndocumented.set(false)
        sourceLink {
            localDirectory.set(project.file("src/main/kotlin"))
            remoteUrl.set(remoteSource.map { remoteSource ->
                uri("$remoteSource/$projectPath/src/main/kotlin").toURL()
            })
            remoteLineSuffix.set("#L")
        }
        externalDocumentationLink(
            url = "https://javadoc.io/doc/com.google.dagger/dagger/${libs.versions.dagger}/",
        )
    }
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    doLast {
        with(publication) {
            println("Published artifact $groupId:$artifactId:$version")
        }
    }
}

val publishing: PublishingExtension = extensions.getByType()

with(publishing) {
    repositories.maven {
        name = "mavenCentral"
        setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        credentials {
            username = providers.gradleProperty("sonatype.username")
                .orElse(providers.environmentVariable("SONATYPE_USERNAME"))
                .orNull
            password = providers.gradleProperty("sonatype.password")
                .orElse(providers.environmentVariable("SONATYPE_PASSWORD"))
                .orNull
        }
    }

    repositories.maven {
        name = "sonatypeSnapshots"
        setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        credentials {
            username = providers.gradleProperty("sonatype.username")
                .orElse(providers.environmentVariable("SONATYPE_USERNAME"))
                .orNull
            password = providers.gradleProperty("sonatype.password")
                .orElse(providers.environmentVariable("SONATYPE_PASSWORD"))
                .orNull
        }
    }
}

val javadocJar by tasks.registering(Jar::class) {
    from(tasks.dokkaJavadoc)
    archiveClassifier.set("javadoc")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
}

val publication = publishing.publications.register<MavenPublication>("autoDagger") {
    groupId = rootProject.group as String
    artifactId = project.path.removePrefix(":").replace(':', '-')
    version = providers.gradleProperty("version").get()
    artifact(sourcesJar)
    artifact(javadocJar)
    pom {
        val moduleName = project.path
            .removePrefix(":")
            .splitToSequence(":")
            .joinToString { it.replaceFirstChar(Char::uppercaseChar) }

        name.set("Auto Dagger $moduleName")
        description.set("Automatic Dagger setup using Hilt")
        url.set(repo())
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set(remoteSource.map { "$it/LICENSE.txt" })
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("ansman")
                name.set("Nicklas Ansman")
                email.set("nicklas@ansman.se")
            }
            scm {
                connection.set("scm:git:git://github.com/ansman/auto-dagger.git")
                developerConnection.set("scm:git:ssh://git@github.com/ansman/auto-dagger.git")
                url.set(repo())
            }
        }
    }
}


if (findProperty("signArtifacts")?.toString()?.toBoolean() == true) {
    signing {
        sign(publication.get())
        gradle.taskGraph.whenReady {
            if (hasTask(tasks.getByName("sign${publication.name.replaceFirstChar(Char::uppercaseChar)}Publication"))) {
                rootProject.ext.getOrPut("signing.gnupg.passphrase") {
                    serviceOf<UserInputHandler>()
                        .askQuestion("Signing key passphrase: ", "")
                }
                useGpgCmd()
            }
        }
    }
}

pluginManager.withPlugin("com.android.library") {
    extensions.configure<LibraryExtension> {
        publishing {
            singleVariant("release")
        }
    }

    publication {
        afterEvaluate {
            from(components["release"])
        }
    }

    sourcesJar {
        // This is a "hack" to avoid Android from including generated sources
        from(
            files("src/main").asFileTree.matching(
                PatternSet().include(
                    "**/*.java",
                    "**/*.kt",
                )
            )
        )
    }
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    publication {
        from(components["java"])
    }

    sourcesJar {
        dependsOn("classes")
        from(project.extensions.getByType<SourceSetContainer>().getByName("main").allSource)
    }
}