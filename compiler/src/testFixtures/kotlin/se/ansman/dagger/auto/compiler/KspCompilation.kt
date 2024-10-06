package se.ansman.dagger.auto.compiler

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.kspProcessorOptions
import se.ansman.dagger.auto.compiler.common.Options
import se.ansman.dagger.auto.compiler.ksp.AutoDaggerSymbolProcessorProvider
import java.io.File

class KspCompilation(workingDir: File) : Compilation(workingDir) {

    override fun doCompile(
        sources: List<TestSourceFile>,
        configuration: KotlinCompilation.() -> Unit
    ): Result =
        KotlinCompilation()
            .apply {
                kspProcessorOptions[Options.enableLogging] = "true"
                configuration()
                this.sources = sources.map { it.toSourceFile() }
                configureKsp(useKsp2 = true) {
                    symbolProcessorProviders.add(AutoDaggerSymbolProcessorProvider())
                }
            }
            .compile()
            .let(::Result)

    private fun File.listSourceFiles(): Sequence<File> =
        walkTopDown().filter { it.isFile && (it.extension == "java" || it.extension == "kt") }

    override fun JvmCompilationResult.readFilesGeneratedByAnnotationProcessor(): Map<String, String> =
        workingDir.resolve("ksp/sources")
            .listSourceFiles()
            .associateBy(
                { it.name },
                { it.readText().trim() }
            )

    object Factory : Compilation.Factory {
        override val expectedFilesDirectoryName: String
            get() = "ksp"

        override fun create(workingDir: File): KspCompilation =
            KspCompilation(workingDir)
    }
}