package se.ansman.dagger.auto.compiler

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import se.ansman.dagger.auto.compiler.ksp.AutoDaggerSymbolProcessorProvider
import java.io.File

class KspAutoDaggerCompilation(workingDir: File) : AutoDaggerCompilation(workingDir) {

    override fun compile(
        sources: List<TestSourceFile>,
        configuration: KotlinCompilation.() -> Unit
    ): Result =
        KotlinCompilation()
            .apply {
                configuration()
                this.sources = sources.map { it.toSourceFile() }
                symbolProcessorProviders = listOf(AutoDaggerSymbolProcessorProvider())
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
}