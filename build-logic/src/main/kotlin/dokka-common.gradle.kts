import com.google.devtools.ksp.gradle.KspAATask

plugins {
    id("org.jetbrains.dokka")
    id("org.jetbrains.dokka-javadoc")
}

dokka {
    dokkaSourceSets.configureEach {
        suppressGeneratedFiles = true
        tasks.withType<KspAATask>().configureEach {
            suppressedFiles.from(fileTree(kspConfig.outputBaseDir))
        }
        perPackageOption {
            matchingRegex = "hilt_aggregated_deps*"
            suppress = true
        }
    }
}