package se.ansman.dagger.auto

import com.tschuchort.compiletesting.KotlinCompilation
import se.ansman.dagger.auto.kapt.AutoDaggerAnnotationProcessor
import java.io.File

class KaptAutoDaggerCompilation(workingDir: File) : AutoDaggerCompilation(workingDir) {

    override fun compile(sources: List<TestSourceFile>, configuration: KotlinCompilation.() -> Unit): Result =
        Result(KotlinCompilation().apply {
            configuration()
            annotationProcessors = listOf(AutoDaggerAnnotationProcessor())
            this.sources = sources.map { it.toSourceFile() }
        }.compile())

    override val KotlinCompilation.Result.filesGeneratedByAnnotationProcessor: Sequence<File>
        get() = sourcesGeneratedByAnnotationProcessor.asSequence()
}