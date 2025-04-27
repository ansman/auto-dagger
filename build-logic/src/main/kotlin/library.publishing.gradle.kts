
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("dokka-common")
    id("com.vanniktech.maven.publish")
    id("signing")
}

fun repo(path: String = "") = "https://github.com/ansman/auto-dagger$path"

tasks.withType<AbstractPublishToMaven>().configureEach {
    doLast {
        with(publication) {
            println("Published artifact $groupId:$artifactId:$version")
        }
    }
}

val signArtifacts = providers.gradleProperty("signArtifacts").orNull?.toBooleanStrict() ?: false
mavenPublishing {
    val version = providers.gradleProperty("version").get()
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    if (signArtifacts) {
        signAllPublications()
    }

    coordinates(
        groupId = rootProject.group as String,
        artifactId = project.path
            .removePrefix(":third-party")
            .removePrefix(":")
            .replace(':', '-'),
        version = version,
    )
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

if (signArtifacts) {
    signing {
        useGpgCmd()
    }
}

tasks.register("publishSnapshot") {
    if (providers.gradleProperty("version").get().endsWith("-SNAPSHOT")) {
        dependsOn("publishAllPublicationsToMavenCentralRepository")
    }
}

pluginManager.withPlugin("com.android.library") {
    mavenPublishing {
        configure(AndroidSingleVariantLibrary(sourcesJar = true, publishJavadocJar = false))
    }

    val javadocJar by tasks.registering(Jar::class) {
        from(tasks.dokkaGeneratePublicationJavadoc)
        archiveClassifier.set("javadoc")
    }

    publishing {
        publications.withType<MavenPublication>().configureEach {
            artifact(javadocJar)
        }
    }
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    mavenPublishing {
        configure(
            KotlinJvm(
                javadocJar = JavadocJar.Dokka(tasks.dokkaGeneratePublicationJavadoc.name),
                sourcesJar = true
            )
        )
    }
}

pluginManager.withPlugin("org.gradle.java-test-fixtures") {
    // Disables publishing test fixtures:
    // https://docs.gradle.org/current/userguide/java_testing.html#ex-disable-publishing-of-test-fixtures-variants
    val javaComponent = components["java"] as AdhocComponentWithVariants
    javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
    javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
}
