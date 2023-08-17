package se.ansman.dagger.auto.compiler.common.testutils

import com.tschuchort.compiletesting.KotlinCompilation
import java.io.File
import javax.annotation.processing.Processor

class KaptAutoDaggerCompilation(
    private val processors: () -> List<Processor>,
    workingDir: File,
) : AutoDaggerCompilation(workingDir) {

    override fun compile(sources: List<TestSourceFile>, configuration: KotlinCompilation.() -> Unit): Result =
        KotlinCompilation()
            .apply {
                configuration()
                annotationProcessors = processors()
                this.sources = sources.map { it.toSourceFile() }
            }
            .compile()
            .let(::Result)

    override val KotlinCompilation.Result.filesGeneratedByAnnotationProcessor: Sequence<File>
        get() = sourcesGeneratedByAnnotationProcessor.asSequence()

    class Factory(vararg processors: () -> Processor) : AutoDaggerCompilation.Factory {
        private val processors = { processors.map { it() }}

        override val expectedFilesDirectoryName: String
            get() = "kapt"

        override fun create(workingDir: File): KaptAutoDaggerCompilation =
            KaptAutoDaggerCompilation(processors, workingDir)
    }
}