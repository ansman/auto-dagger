package se.ansman.dagger.auto.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property

abstract class BuildConfigTask : DefaultTask() {
    @get:Input
    val packageName: Property<String> = project.objects.property<String>().apply {
        convention(project.group.toString().replace('-', '.'))
        finalizeValueOnRead()
    }

    @get:Input
    val className: Property<String> = project.objects.property<String>().apply {
        convention(project.path
            .removePrefix(":third-party")
            .removePrefix(":")
            .splitToSequence(':', '-')
            .joinToString("", prefix = "AutoDagger", postfix = "BuildConfig") { it.replaceFirstChar(Char::uppercaseChar) })
        finalizeValueOnRead()
    }

    @get:Input
    internal val properties: MapProperty<String, String> = project.objects.mapProperty<String, String>().apply {
        finalizeValueOnRead()
    }

    @get:OutputDirectory
    val outputDirectory: DirectoryProperty = project.objects.directoryProperty().apply {
        convention(project.layout.buildDirectory.dir("generated/$name"))
        finalizeValueOnRead()
    }

    @TaskAction
    fun generate() {
        outputDirectory.get().asFile.deleteRecursively()
        val packageName = packageName.get()
        val className = className.get()
        with(outputDirectory.get().asFile.resolve("${packageName.replace('.', '/')}/$className.kt")) {
            parentFile!!.mkdirs()
            writer().use { writer ->
                writer.write(
                    """
                    package $packageName
                    
                    internal object $className {
                    """.trimIndent()
                )
                for ((k, v) in properties.get()) {
                    writer.write("\n    const val $k = $v")
                }
                writer.write("\n}")
            }
        }
    }

    fun property(name: String, value: String) {
        properties.put(name, "\"${value.replace("\"", "\\\"")}\"")
    }
}