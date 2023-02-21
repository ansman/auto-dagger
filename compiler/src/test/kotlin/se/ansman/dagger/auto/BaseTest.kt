package se.ansman.dagger.auto

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


abstract class BaseTest(
    private val expectedFilesDirectoryName: String,
    private val compilation: (tempDirectory: File) -> AutoDaggerCompilation
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
                    AutoDaggerTestCase(
                        testName = name,
                        compilation = compilation(tempDirectory.resolve(name).apply { mkdirsOrThrow() }),
                        sources = Files.list(test)
                            .asSequence()
                            .filter { it.isRegularFile() }
                            .map(Path::toFile)
                            .map(TestSourceFile::File)
                            .toList(),
                        expectedFiles = Files.list(expectedDirectory)
                            .asSequence()
                            .associateBy({ it.fileName.toString().removeSuffix(".txt") }, { it.readText() })
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
                      @se.ansman.dagger.auto.AutoInitialize(priority = 4711)
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
        
                    @se.ansman.dagger.auto.AutoInitialize
                    @SomeScope
                    class SomeThing @javax.inject.Inject constructor()
                """
            )
            .assertFailedWithMessage(Errors.wrongScope)
    }

    @Test
    fun `the annotated method must be a provider or a binding`(@TempDir tempDirectory: File) {
        compilation(tempDirectory)
            .compile(
                """
                    package se.ansman
            
                    @dagger.Module
                    object SomeModule {
                        @se.ansman.dagger.auto.AutoInitialize
                        @javax.inject.Singleton
                        fun provideString(): String = "string"
                    }
                """
            )
            .assertFailedWithMessage(Errors.invalidAnnotatedMethod)
    }

    @Test
    fun `the annotated method must not be top level`(@TempDir tempDirectory: File) {
        compilation(tempDirectory)
            .compile(
                """
                    package se.ansman
            
                    @dagger.Provides
                    @se.ansman.dagger.auto.AutoInitialize
                    @javax.inject.Singleton
                    fun provideString(): String = "string"
                """
            )
            .assertFailedWithMessage(Errors.methodInNonModule)
    }

    @Test
    fun `the annotated method must not be in a module`(@TempDir tempDirectory: File) {
        compilation(tempDirectory)
            .compile(
                """
                    package se.ansman
            
                    object SomeModule {
                        @dagger.Provides
                        @se.ansman.dagger.auto.AutoInitialize
                        @javax.inject.Singleton
                        fun provideString(): String = "string"
                    }
                """
            )
            .assertFailedWithMessage(Errors.methodInNonModule)
    }
}