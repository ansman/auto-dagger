package se.ansman.dagger.auto.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering

fun Project.setupResourceTests() {
    val updateExpectedTestFiles by tasks.registering {
        dependsOn("cleanProcessTestResources")
        dependsOn("test")
    }
    gradle.taskGraph.whenReady {
        if (hasTask("$path:${updateExpectedTestFiles.name}")) {
            tasks.named<Test>("test") {
                systemProperty("writeExpectedFilesTo", file("src/test/resources/tests"))
            }
        }
    }
}