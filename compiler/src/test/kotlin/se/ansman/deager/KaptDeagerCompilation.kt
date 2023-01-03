package se.ansman.deager

import com.tschuchort.compiletesting.KotlinCompilation
import se.ansman.deager.kapt.DeagerAnnotationProcessor
import java.io.File

class KaptDeagerCompilation(workingDir: File) : DeagerCompilation(workingDir) {

    override fun compile(sources: List<TestSourceFile>, configuration: KotlinCompilation.() -> Unit): Result =
        Result(KotlinCompilation().apply {
            configuration()
            annotationProcessors = listOf(DeagerAnnotationProcessor())
            this.sources = sources.map { it.toSourceFile() }
        }.compile())

    override val KotlinCompilation.Result.filesGeneratedByAnnotationProcessor: Sequence<File>
        get() = sourcesGeneratedByAnnotationProcessor.asSequence()
}