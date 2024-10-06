package se.ansman.dagger.auto.compiler

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.kspProcessorOptions
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import se.ansman.dagger.auto.compiler.common.Options
import se.ansman.dagger.auto.compiler.plugin.AUTO_DAGGER_PLUGIN_ID
import se.ansman.dagger.auto.compiler.plugin.AutoDaggerCliOption
import se.ansman.dagger.auto.compiler.plugin.AutoDaggerCommandLineProcessor
import se.ansman.dagger.auto.compiler.plugin.AutoDaggerCompilerPluginRegistrar
import java.io.File

class CompilerPluginCompilation(
    workingDir: File,
) : Compilation(workingDir) {

    override fun File.readFile(): String = with(Companion) { readFile() }

    private val irOutput: File = workingDir.resolve("ir")

    override fun doCompile(
        sources: List<TestSourceFile>,
        configuration: KotlinCompilation.() -> Unit
    ): Result =
        KotlinCompilation()
            .apply {
                kspProcessorOptions[Options.enableLogging] = "true"
                configuration()
                this.sources = sources.map { it.toSourceFile() }
                supportsK2 = true
                compilerPluginRegistrars = listOf(AutoDaggerCompilerPluginRegistrar())
                pluginOptions = mapOf(
                    AutoDaggerCliOption.EnableLogging to true.toString(),
                    AutoDaggerCliOption.DumpFilesTo to irOutput.absolutePath
                ).entries.map { (option, value) ->
                    PluginOption(AUTO_DAGGER_PLUGIN_ID, option.optionName, value)
                }
                commandLineProcessors = listOf(AutoDaggerCommandLineProcessor())
            }
            .compile()
            .let(::Result)

    override fun JvmCompilationResult.readFilesGeneratedByAnnotationProcessor(): Map<String, String> =
        irOutput.walkTopDown()
            .filter { it.isFile }
            .associateBy({ it.name }, { it.readText().trim() })


    companion object {
        fun File.readFile(): String =
            ProcessBuilder("javap", "-p", "-c", "-v", absolutePath)
                .start()
                .run {
                    waitFor()
                    inputStream.bufferedReader()
                        .readText()
                        .lineSequence()
                        .drop(4)
                        .joinToString("\n")
                }
    }

    object Factory : Compilation.Factory {
        override val expectedFilesDirectoryName: String
            get() = "compiler-plugin"

        override fun create(workingDir: File): CompilerPluginCompilation = CompilerPluginCompilation(workingDir)
    }

    data class PluginFactory(
        val registrar: CompilerPluginRegistrar,
        val options: List<PluginOption> = emptyList(),
        val commandLineProcessors: List<CommandLineProcessor> = emptyList()
    )
}