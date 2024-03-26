package se.ansman.dagger.auto.compiler.common.testutils

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.*
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import se.ansman.dagger.auto.compiler.common.Options
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
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

    private fun KotlinCompilation.compileFixed(): JvmCompilationResult {
        val result = synchronized(mutex) { compile() }
        // This works around a bug where compile-testing-kotlin returns OK even though KSP failed.
        if (
            result.exitCode == KotlinCompilation.ExitCode.OK &&
            "e: Error occurred in KSP, check log for detail" in result.messages
        ) {
            return JvmCompilationResult(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.messages, this)
        }
        return result
    }

    private fun File.listSourceFiles(): Sequence<File> =
        walkTopDown().filter { it.isFile && (it.extension == "java" || it.extension == "kt") }

    override val JvmCompilationResult.filesGeneratedByAnnotationProcessor: Sequence<File>
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