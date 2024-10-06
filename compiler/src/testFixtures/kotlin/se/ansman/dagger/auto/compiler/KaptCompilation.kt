package se.ansman.dagger.auto.compiler

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import se.ansman.dagger.auto.compiler.common.Options
import se.ansman.dagger.auto.compiler.kapt.AutoDaggerAnnotationProcessor
import java.io.File

class KaptCompilation(workingDir: File) : Compilation(workingDir) {

    override fun doCompile(sources: List<TestSourceFile>, configuration: KotlinCompilation.() -> Unit): Result =
        KotlinCompilation()
            .apply {
                kaptArgs[Options.enableLogging] = "true"
                configuration()
                annotationProcessors = listOf(AutoDaggerAnnotationProcessor())
                this.sources = sources.map { it.toSourceFile() }
                useKapt4 = true
            }
            .run { synchronized(mutex) { compile() } }
            .let(::Result)

    override fun JvmCompilationResult.readFilesGeneratedByAnnotationProcessor(): Map<String, String> =
        sourcesGeneratedByAnnotationProcessor.associateBy(
            { it.name },
            { it.readText().trim() }
        )

    object Factory : Compilation.Factory {
        override val expectedFilesDirectoryName: String
            get() = "kapt"

        override fun create(workingDir: File): KaptCompilation =
            KaptCompilation(workingDir)
    }
}