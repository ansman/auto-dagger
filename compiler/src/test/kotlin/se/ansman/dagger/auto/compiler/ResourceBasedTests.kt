package se.ansman.dagger.auto.compiler

import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import se.ansman.dagger.auto.compiler.common.testutils.ResourceBasedTestGenerator
import java.io.File

class ResourceBasedTests {
    @TestFactory
    fun resources(@TempDir tempDirectory: File) =
        ResourceBasedTestGenerator(AutoDaggerCompilationFactoryProvider()).generateTests(tempDirectory)
}