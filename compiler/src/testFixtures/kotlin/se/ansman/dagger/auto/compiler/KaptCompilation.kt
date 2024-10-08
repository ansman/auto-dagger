package se.ansman.dagger.auto.compiler

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import se.ansman.dagger.auto.compiler.common.Options
import java.io.File
import javax.annotation.processing.Processor

@OptIn(ExperimentalCompilerApi::class)
class KaptCompilation(
    private val processors: () -> List<Processor>,
    workingDir: File,
) : Compilation(workingDir) {

    override fun compile(sources: List<TestSourceFile>, configuration: KotlinCompilation.() -> Unit): Result =
        KotlinCompilation()
            .apply {
                kaptArgs[Options.enableLogging] = "true"
                configuration()
                annotationProcessors = processors()
                this.sources = sources.map { it.toSourceFile() }
                useKapt4 = true
            }
            .run { synchronized(mutex) { compile() } }
            .let(::Result)

    override val JvmCompilationResult.filesGeneratedByAnnotationProcessor: Sequence<File>
        get() = sourcesGeneratedByAnnotationProcessor.asSequence()

    class Factory(vararg processors: () -> Processor) : Compilation.Factory {
        private val processors = { processors.map { it() }}

        override val expectedFilesDirectoryName: String
            get() = "kapt"

        override fun create(workingDir: File): KaptCompilation =
            KaptCompilation(processors, workingDir)
    }
}