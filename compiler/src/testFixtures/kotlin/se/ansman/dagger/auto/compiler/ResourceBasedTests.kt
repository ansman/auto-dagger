package se.ansman.dagger.auto.compiler

import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import java.io.File

abstract class ResourceBasedTests {
    @TestFactory
    fun resources(@TempDir tempDirectory: File) =
        ResourceBasedTestGenerator(AutoDaggerCompilationFactoryProvider())
            .generateTests(tempDirectory)
}