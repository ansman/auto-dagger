package se.ansman.dagger.auto.compiler

import com.tschuchort.compiletesting.KotlinCompilation
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.toPath
import kotlin.streams.asSequence

@OptIn(ExperimentalCompilerApi::class)
class ResourceBasedTestGenerator(
    private val factories: List<Compilation.Factory>,
) {
    private val writeExpectedFilesTo = System.getProperty("writeExpectedFilesTo")
        ?.let(::File)

    fun generateTests(tempDirectory: File, configure: KotlinCompilation.() -> Unit = {}): Iterable<DynamicNode> =
        Files.list(ClassLoader.getSystemResource("tests").toURI().toPath())
            .asSequence()
            .map { type ->
                val typeName = type.fileName.toString()
                DynamicContainer.dynamicContainer(
                    typeName,
                    testsFromResources(tempDirectory.resolve(typeName), typeName, configure)
                )
            }
            .ifEmpty { error("No tests found") }
            .asIterable()

    private fun testsFromResources(tempDirectory: File, type: String, configure: KotlinCompilation.() -> Unit): Iterable<DynamicNode> =
        Files.list(ClassLoader.getSystemResource("tests/$type").toURI().toPath())
            .asSequence()
            .map { test ->
                DynamicContainer.dynamicContainer(
                    test.name.replace('.', '_'),
                    factories
                        .map { factory -> createTestCase(test, factory, type, tempDirectory, configure) }
                )
            }
            .ifEmpty { error("No tests found") }
            .asIterable()

    private fun createTestCase(
        test: Path,
        factory: Compilation.Factory,
        type: String,
        tempDirectory: File,
        configure: KotlinCompilation.() -> Unit
    ): DynamicTest {
        val name = test.fileName.toString()
        val expectedDirectory = test.resolve(factory.expectedFilesDirectoryName)
        return DynamicTest.dynamicTest(
            factory.expectedFilesDirectoryName,
            ResourceTestCase(
                testType = type,
                testName = name,
                compilation = factory.create(
                    tempDirectory.resolve(name).resolve(factory.expectedFilesDirectoryName).apply { mkdirs() }),
                sources = Files.list(test)
                    .asSequence()
                    .filter { it.isRegularFile() }
                    .map(Path::toFile)
                    .map(TestSourceFile::File)
                    .toList(),
                writeExpectedFilesTo = writeExpectedFilesTo
                    ?.resolve("$type/${test.name}/${factory.expectedFilesDirectoryName}"),
                expectedFiles = expectedDirectory
                    .takeIf(Path::exists)
                    ?.let(Files::list)
                    ?.asSequence()
                    ?.filter { it.name != "empty.txt" }
                    ?.associateBy(
                        { it.fileName.toString().removeSuffix(".txt") },
                        { it.readText().trim() })
                    ?: emptyMap(),
                configure = configure
            )
        )
    }
}