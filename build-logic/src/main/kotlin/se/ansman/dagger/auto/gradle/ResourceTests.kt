package se.ansman.dagger.auto.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering

fun Project.setupResourceTests() {
    val updateExpectedTestFiles by tasks.registering
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        updateExpectedTestFiles.configure {
            dependsOn("cleanProcessTestResources")
            dependsOn("test")
        }
    }
    pluginManager.withPlugin("com.android.library") {
        updateExpectedTestFiles.configure {
            dependsOn("cleanProcessDebugUnitTestResources")
            dependsOn("testDebugUnitTest")
        }
    }
    afterEvaluate {
        val testTask = when {
            pluginManager.hasPlugin("org.jetbrains.kotlin.jvm") -> tasks.named<Test>("test")

            pluginManager.hasPlugin("com.android.library") -> tasks.named<Test>("testDebugUnitTest")

            else -> error("Unknown project")
        }
        val writeExpectedFiles = objects.property<Boolean>()
        testTask.configure {
            inputs.property("writeExpectedFiles", writeExpectedFiles)
            doFirst {
                if (writeExpectedFiles.get()) {
                    systemProperty("writeExpectedFilesTo", project.file("src/test/resources/tests"))
                }
            }
        }
        gradle.taskGraph.whenReady {
            writeExpectedFiles.set(hasTask("$path:${updateExpectedTestFiles.name}"))
        }
    }
}