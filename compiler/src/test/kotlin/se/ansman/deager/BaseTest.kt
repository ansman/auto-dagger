package se.ansman.deager

import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.incremental.mkdirsOrThrow
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.toPath
import kotlin.streams.asSequence
import kotlin.streams.toList


abstract class BaseTest(
    private val expectedFilesDirectoryName: String,
    private val compilation: (File) -> DeagerCompilation
) {
    @TestFactory
    open fun `tests from resources`(@TempDir tempDirectory: File): Collection<DynamicTest> =
        Files.list(ClassLoader.getSystemResource("tests").toURI().toPath())
            .map { test ->
                val name = test.fileName.toString()
                DynamicTest.dynamicTest(
                    name,
                    DeagerTestCase(
                        testName = name,
                        compilation = compilation(tempDirectory.resolve(name).apply { mkdirsOrThrow() }),
                        sources = Files.list(test)
                            .filter { it.isRegularFile() }
                            .map { SourceFile.fromPath(it.toFile()) }
                            .toList(),
                        expectedFiles = Files.list(test.resolve(expectedFilesDirectoryName))
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
                kotlin(
                    """
                        package se.ansman
            
                        object Outer {
                          @se.ansman.deager.Eager(priority = 4711)
                          class SomeThing @javax.inject.Inject constructor()
                        }
                    """
                )
            )
            .assertFailedWithMessage(Errors.unscopedType)
    }

    @Test
    fun `the annotated class can only be scoped with @Singleton`(@TempDir tempDirectory: File) {
        compilation(tempDirectory)
            .compile(
                kotlin(
                        """
                        package se.ansman
                        
                        @javax.inject.Scope
                        annotation class SomeScope
            
                        @se.ansman.deager.Eager
                        @SomeScope
                        class SomeThing @javax.inject.Inject constructor()
                    """
                )
            )
            .assertFailedWithMessage(Errors.wrongScope)
    }

    private fun kotlin(
        @Language("kotlin")
        contents: String,
        name: String = "test.kt"
    ) = SourceFile.kotlin(name, contents)
}