package se.ansman.dagger.auto.compiler.common.testutils

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

class ResourceBasedTestGenerator(
    private val compilationFactoryProvider: CompilationFactoryProvider,
) {
    private val writeExpectedFilesTo = System.getProperty("writeExpectedFilesTo")?.let(::File)

    fun generateTests(tempDirectory: File): Iterable<DynamicNode> =
        Files.list(ClassLoader.getSystemResource("tests").toURI().toPath())
            .asSequence()
            .map { type ->
                val typeName = type.fileName.toString()
                DynamicContainer.dynamicContainer(
                    typeName,
                    testsFromResources(tempDirectory.resolve(typeName), typeName)
                )
            }
            .ifEmpty { error("No tests found") }
            .asIterable()

    private fun testsFromResources(tempDirectory: File, type: String): Iterable<DynamicNode> =
        Files.list(ClassLoader.getSystemResource("tests/$type").toURI().toPath())
            .asSequence()
            .map { test ->
                DynamicContainer.dynamicContainer(
                    test.name,
                    compilationFactoryProvider.factories
                        .mapNotNull { factory -> createTestCase(test, factory, type, tempDirectory) }
                        .asIterable()
                )
            }
            .ifEmpty { error("No tests found") }
            .asIterable()

    private fun createTestCase(
        test: Path,
        factory: Compilation.Factory,
        type: String,
        tempDirectory: File
    ): DynamicTest? {
        val name = test.fileName.toString()
        val expectedDirectory = test.resolve(factory.expectedFilesDirectoryName)
        return DynamicTest.dynamicTest(
            factory.expectedFilesDirectoryName,
            ResourceTestCase(
                testType = type,
                testName = name,
                compilation = factory.create(tempDirectory.resolve(name).resolve(factory.expectedFilesDirectoryName).apply { mkdirs() }),
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
                    ?: emptyMap()
            )
        )
    }
}