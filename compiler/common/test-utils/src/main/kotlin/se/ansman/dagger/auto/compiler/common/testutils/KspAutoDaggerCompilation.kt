package se.ansman.dagger.auto.compiler.common.testutils

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import java.io.File

class KspAutoDaggerCompilation(
    private val processorProviders: () -> List<SymbolProcessorProvider>,
    workingDir: File
) : AutoDaggerCompilation(workingDir) {

    override fun compile(
        sources: List<TestSourceFile>,
        configuration: KotlinCompilation.() -> Unit
    ): Result =
        KotlinCompilation()
            .apply {
                configuration()
                this.sources = sources.map { it.toSourceFile() }
                symbolProcessorProviders = processorProviders()
                kspWithCompilation = true
            }
            .compileFixed()
            .let(::Result)

    private fun KotlinCompilation.compileFixed(): KotlinCompilation.Result {
        val result = compile()
        // This works around a bug where compile-testing-kotlin returns OK even though KSP failed.
        if (
            result.exitCode == KotlinCompilation.ExitCode.OK &&
            "e: Error occurred in KSP, check log for detail" in result.messages
        ) {
            return Result(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.messages)
        }
        return result
    }

    private fun File.listSourceFiles(): Sequence<File> =
        walkTopDown().filter { it.isFile && (it.extension == "java" || it.extension == "kt") }

    override val KotlinCompilation.Result.filesGeneratedByAnnotationProcessor: Sequence<File>
        get() = workingDir.resolve("ksp/sources").listSourceFiles()

    class Factory(vararg processorProviders: () -> SymbolProcessorProvider) : AutoDaggerCompilation.Factory {
        private val processorProviders = { processorProviders.map { it() }}

        override val expectedFilesDirectoryName: String
            get() = "ksp"

        override fun create(workingDir: File): KspAutoDaggerCompilation =
            KspAutoDaggerCompilation(processorProviders, workingDir)
    }
}