package se.ansman.deager

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import se.ansman.deager.ksp.DeagerSymbolProcessorProvider
import java.io.File

class KspCompilation(workingDir: File) : DeagerCompilation(workingDir) {

    override fun compile(
        sources: List<SourceFile>,
        configuration: KotlinCompilation.() -> Unit
    ): Result =
        KotlinCompilation()
            .apply {
                configuration()
                this.sources = sources
                symbolProcessorProviders = listOf(DeagerSymbolProcessorProvider())
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