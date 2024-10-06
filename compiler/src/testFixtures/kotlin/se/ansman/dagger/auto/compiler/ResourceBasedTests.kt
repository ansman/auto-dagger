package se.ansman.dagger.auto.compiler

import com.tschuchort.compiletesting.KotlinCompilation
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import java.io.File

abstract class ResourceBasedTests {
    @TestFactory
    fun resources(@TempDir tempDirectory: File) =
        ResourceBasedTestGenerator(factories.toList())
            .generateTests(tempDirectory) { configure() }

    protected open val factories get() = AutoDaggerCompilationFactoryProvider.factories

    protected open fun KotlinCompilation.configure() {}
}