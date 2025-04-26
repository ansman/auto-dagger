
import com.android.build.gradle.LibraryExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.accessors.dm.LibrariesForLibs
import se.ansman.dagger.auto.gradle.execWithOutput
import se.ansman.dagger.auto.gradle.mapNullable

plugins {
    id("maven-publish")
    id("signing")
    id("dokka-common")
    id("se.ansman.sonatype-publish-fix")
}

val libs = the<LibrariesForLibs>()

val gitCommit = project
    .execWithOutput {
        commandLine("git", "rev-parse", "HEAD")
        workingDir = project.rootDir
    }
    .map { it.trim() }


fun repo(path: String = "") = "https://github.com/ansman/auto-dagger$path"

val remoteSource: Provider<String> = providers.gradleProperty("version")
    .mapNullable { version -> version.takeUnless { it.endsWith("-SNAPSHOT") } }
    .orElse(gitCommit)
    .map { repo("/blob/$it") }

dokka {
    val projectPath = project.path.removePrefix(":").replace(':', '/')
    dokkaSourceSets.configureEach {
        reportUndocumented.set(false)
        sourceLink {
            localDirectory.set(project.file("src/main/kotlin"))
            remoteUrl.set(remoteSource.map { remoteSource ->
                uri("$remoteSource/$projectPath/src/main/kotlin")
            })
            remoteLineSuffix.set("#L")
        }
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
            username = providers.gradleProperty("sonatype.s01.oss.sonatype.org.username")
                .orElse(providers.environmentVariable("SONATYPE_USERNAME"))
                .orNull
            password = providers.gradleProperty("sonatype.s01.oss.sonatype.org.password")
                .orElse(providers.environmentVariable("SONATYPE_PASSWORD"))
                .orNull
        }
    }

    repositories.maven {
        name = "sonatypeSnapshots"
        setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        credentials {
            username = providers.gradleProperty("sonatype.s01.oss.sonatype.org.username")
                .orElse(providers.environmentVariable("SONATYPE_USERNAME"))
                .orNull
            password = providers.gradleProperty("sonatype.s01.oss.sonatype.org.password")
                .orElse(providers.environmentVariable("SONATYPE_PASSWORD"))
                .orNull
        }
    }
}

tasks.register("publishSnapshot") {
    if (providers.gradleProperty("version").get().endsWith("-SNAPSHOT")) {
        dependsOn("publishAllPublicationsToSonatypeSnapshotsRepository")
    }
}

val javadocJar by tasks.registering(Jar::class) {
    from(tasks.dokkaGeneratePublicationJavadoc)
    archiveClassifier.set("javadoc")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
}

val publication = publishing.publications.register<MavenPublication>("autoDagger") {
    groupId = rootProject.group as String
    artifactId = project.path
        .removePrefix(":third-party")
        .removePrefix(":")
        .replace(':', '-')
    version = providers.gradleProperty("version").get()
    artifact(sourcesJar)
    artifact(javadocJar)
    pom {
        val moduleName = project.path
            .removePrefix(":third-party")
            .removePrefix(":")
            .splitToSequence(":")
            .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

        name.set("Auto Dagger $moduleName")
        description.set("Automatic Dagger setup using Hilt")
        url.set(repo())
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("https://raw.githubusercontent.com/ansman/auto-dagger/main/LICENSE.txt")
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
        useGpgCmd()
        sign(publication.get())
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
        afterEvaluate {
            if (pluginManager.hasPlugin("com.gradleup.shadow")) {
                from(components["shadow"])
            } else {
                from(components["java"])
            }
        }
    }

    sourcesJar {
        dependsOn("classes")
        from(project.extensions.getByType<SourceSetContainer>().getByName("main").allSource)
    }
}

pluginManager.withPlugin("com.gradleup.shadow") {
    val compileShaded: Configuration by configurations.creating
    configurations.named("compileOnly") { extendsFrom(compileShaded) }
    configurations.named("testRuntimeOnly") { extendsFrom(compileShaded) }
    configurations.named("shadow") { extendsFrom(configurations["implementation"]) }

    // Since we change the classifier of the shadowJar we need to disable the default jar task or we'll get two
    // artifacts that have the same classifier
    tasks.named<Jar>("jar") {
        archiveClassifier.set("ignored")
    }

    tasks.named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        configurations = listOf(compileShaded)
        isEnableRelocation = true
        relocationPrefix = "se.ansman.dagger.auto${project.path.replace(':', '.').replace('-', '_')}"
        mergeServiceFiles()
    }

    pluginManager.withPlugin("org.gradle.java-test-fixtures") {
        configurations.named("testFixturesImplementation") { extendsFrom(compileShaded) }
        configurations.named("testFixturesImplementation") { extendsFrom(configurations["implementation"]) }
        configurations.named("testFixturesApi") { extendsFrom(configurations["api"]) }
    }
}

pluginManager.withPlugin("org.gradle.java-test-fixtures") {
    // Disables publishing test fixtures:
    // https://docs.gradle.org/current/userguide/java_testing.html#ex-disable-publishing-of-test-fixtures-variants
    val javaComponent = components["java"] as AdhocComponentWithVariants
    javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
    javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
}
