package se.ansman.deager

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import se.ansman.deager.kapt.DeagerAnnotationProcessor
import java.io.File

class KaptCompilation(workingDir: File) : DeagerCompilation(workingDir) {

    override fun compile(sources: List<SourceFile>, configuration: KotlinCompilation.() -> Unit): Result =
        Result(KotlinCompilation().apply {
            configuration()
            annotationProcessors = listOf(DeagerAnnotationProcessor())
            this.sources = sources
        }.compile())

    override val KotlinCompilation.Result.filesGeneratedByAnnotationProcessor: Sequence<File>
        get() = sourcesGeneratedByAnnotationProcessor.asSequence()
}