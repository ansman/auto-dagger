package se.ansman.dagger.auto.compiler.common.testutils

import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.toPath
import kotlin.streams.asSequence

abstract class ResourceBasedTest(
    private val compilationFactory: AutoDaggerCompilation.Factory
) {
    private val writeExpectedFilesTo = System.getProperty("writeExpectedFilesTo")?.let(::File)

    @TestFactory
    fun resources(@TempDir tempDirectory: File): Iterable<DynamicNode> =
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

    private fun testsFromResources(tempDirectory: File, type: String): Iterable<DynamicTest> =
        Files.list(ClassLoader.getSystemResource("tests/$type").toURI().toPath())
            .asSequence()
            .mapNotNull { test ->
                val name = test.fileName.toString()
                val expectedDirectory = test.resolve(compilationFactory.expectedFilesDirectoryName)
                if (!expectedDirectory.exists() && writeExpectedFilesTo == null) {
                    return@mapNotNull null
                }
                DynamicTest.dynamicTest(
                    name,
                    AutoDaggerTestCase(
                        testType = type,
                        testName = name,
                        compilation = compilation(tempDirectory.resolve(name).apply { mkdirs() }),
                        sources = Files.list(test)
                            .asSequence()
                            .filter { it.isRegularFile() }
                            .map(Path::toFile)
                            .map(TestSourceFile::File)
                            .toList(),
                        writeExpectedFilesTo = writeExpectedFilesTo
                            ?.resolve("$type/${test.name}/${compilationFactory.expectedFilesDirectoryName}"),
                        expectedFiles = expectedDirectory
                            .takeIf(Path::exists)
                            ?.let(Files::list)
                            ?.asSequence()
                            ?.associateBy({ it.fileName.toString().removeSuffix(".txt") }, { it.readText().trim() })
                            ?: emptyMap()
                    )
                )
            }
            .ifEmpty { error("No tests found") }
            .asIterable()

    protected fun compilation(workingDir: File): AutoDaggerCompilation =
        compilationFactory.create(workingDir)
}