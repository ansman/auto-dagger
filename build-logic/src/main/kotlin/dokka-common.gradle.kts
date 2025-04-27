
import com.google.devtools.ksp.gradle.KspAATask
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask
import se.ansman.dagger.auto.gradle.execWithOutput
import se.ansman.dagger.auto.gradle.mapNullable

plugins {
    id("org.jetbrains.dokka")
    id("org.jetbrains.dokka-javadoc")
}

val libs = the<LibrariesForLibs>()

fun repo(path: String = "") = "https://github.com/ansman/auto-dagger$path"

val gitCommit = project
    .execWithOutput {
        commandLine("git", "rev-parse", "HEAD")
        workingDir = project.rootDir
    }
    .map { it.trim() }

val remoteSource: Provider<String> = providers.gradleProperty("version")
    .mapNullable { version -> version.takeUnless { it.endsWith("-SNAPSHOT") } }
    .orElse(gitCommit)
    .map { repo("/blob/$it") }

dokka {
    val projectPath = project.path.removePrefix(":").replace(':', '/')
    dokkaSourceSets.configureEach {
        suppressGeneratedFiles = true
        reportUndocumented = false
        tasks.withType<KspAATask>().configureEach {
            suppressedFiles.from(fileTree(kspConfig.outputBaseDir))
        }
        perPackageOption {
            matchingRegex = "hilt_aggregated_deps*"
            suppress = true
        }

        sourceLink {
            localDirectory.set(project.file("src/main/kotlin"))
            remoteUrl.set(remoteSource.map { remoteSource ->
                uri("$remoteSource/$projectPath/src/main/kotlin")
            })
            remoteLineSuffix.set("#L")
        }
    }
}

tasks.withType<DokkaGenerateTask>().configureEach {
    mustRunAfter(tasks.withType<KspAATask>())
}

pluginManager.withPlugin("com.android.library") {
   dependencies {
       dokkaPlugin(libs.dokka.androidDocumentationPlugin)
   }
}