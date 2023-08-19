package se.ansman.dagger.auto.compiler.common.testutils

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import se.ansman.dagger.auto.compiler.common.Options
import java.io.File

class KspCompilation(
    private val processorProviders: () -> List<SymbolProcessorProvider>,
    workingDir: File
) : Compilation(workingDir) {

    override fun compile(
        sources: List<TestSourceFile>,
        configuration: KotlinCompilation.() -> Unit
    ): Result =
        KotlinCompilation()
            .apply {
                kspArgs[Options.enableLogging] = "true"
                configuration()
                this.sources = sources.map { it.toSourceFile() }
                symbolProcessorProviders = processorProviders()
                kspWithCompilation = true
            }
            .compileFixed()
            .let(::Result)

    private fun KotlinCompilation.compileFixed(): KotlinCompilation.Result {
        val result = synchronized(mutex) { compile() }
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

    companion object {
        private val mutex = Any()
    }

    class Factory(vararg processorProviders: () -> SymbolProcessorProvider) : Compilation.Factory {
        private val processorProviders = { processorProviders.map { it() }}

        override val expectedFilesDirectoryName: String
            get() = "ksp"

        override fun create(workingDir: File): KspCompilation =
            KspCompilation(processorProviders, workingDir)
    }
}