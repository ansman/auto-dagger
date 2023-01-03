package se.ansman.deager

import org.jetbrains.kotlin.incremental.mkdirsOrThrow
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.toPath
import kotlin.streams.asSequence
import kotlin.streams.toList


abstract class BaseTest(
    private val expectedFilesDirectoryName: String,
    private val compilation: (tempDirectory: File) -> DeagerCompilation
) {
    @TestFactory
    open fun `tests from resources`(@TempDir tempDirectory: File): Collection<DynamicTest> =
        Files.list(ClassLoader.getSystemResource("tests").toURI().toPath())
            .asSequence()
            .mapNotNull { test ->
                val name = test.fileName.toString()
                val expectedDirectory = test.resolve(expectedFilesDirectoryName)
                if (!expectedDirectory.exists()) {
                    return@mapNotNull null
                }
                DynamicTest.dynamicTest(
                    name,
                    DeagerTestCase(
                        testName = name,
                        compilation = compilation(tempDirectory.resolve(name).apply { mkdirsOrThrow() }),
                        sources = Files.list(test)
                            .filter { it.isRegularFile() }
                            .map(Path::toFile)
                            .map(TestSourceFile::File)
                            .toList(),
                        expectedFiles = Files.list(expectedDirectory)
                            .asSequence()
                            .associateBy({ it.fileName.toString() }, { it.readText() })
                    )
                )
            }
            .toList()
            .also { check(it.isNotEmpty()) { "No tests found" } }

    @Test
    fun `the annotated class must be scoped`(@TempDir tempDirectory: File) {
        compilation(tempDirectory)
            .compile(
                """
                    package se.ansman
        
                    object Outer {
                      @se.ansman.deager.Eager(priority = 4711)
                      class SomeThing @javax.inject.Inject constructor()
                    }
                """
            )
            .assertFailedWithMessage(Errors.unscopedType)
    }

    @Test
    fun `the annotated class can only be scoped with @Singleton`(@TempDir tempDirectory: File) {
        compilation(tempDirectory)
            .compile(
                """
                    package se.ansman
                    
                    @javax.inject.Scope
                    annotation class SomeScope
        
                    @se.ansman.deager.Eager
                    @SomeScope
                    class SomeThing @javax.inject.Inject constructor()
                """
            )
            .assertFailedWithMessage(Errors.wrongScope)
    }
}