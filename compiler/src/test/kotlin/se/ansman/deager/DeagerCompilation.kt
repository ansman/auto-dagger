package se.ansman.deager

import assertk.assertThat
import assertk.assertions.contains
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import java.io.File
import java.io.OutputStream
import kotlin.test.assertEquals

abstract class DeagerCompilation(protected val workingDir: File) {
    fun compile(vararg sources: SourceFile): Result = compile(sources.asList())

    fun compile(sources: List<SourceFile>): Result =
        compile(sources) {
            workingDir = this@DeagerCompilation.workingDir
            inheritClassPath = true
            messageOutputStream = OutputStream.nullOutputStream()
        }

    protected abstract fun compile(
        sources: List<SourceFile>,
        configuration: KotlinCompilation.() -> Unit
    ): Result

    protected abstract val KotlinCompilation.Result.filesGeneratedByAnnotationProcessor: Sequence<File>

    inner class Result(private val result: KotlinCompilation.Result) {
        val exitCode: KotlinCompilation.ExitCode get() = result.exitCode
        val messages: String get() = result.messages

        fun assertIsSuccessful() {
            assertEquals(KotlinCompilation.ExitCode.OK, exitCode, messages)
        }

        fun assertFailedWithMessage(message: String) {
            assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, exitCode, messages)
            assertThat(messages).contains(message)
        }

        fun findGeneratedFile(name: String): File? =
            result.filesGeneratedByAnnotationProcessor.find { it.name == name }

        fun readGeneratedFile(name: String): String =
            requireNotNull(findGeneratedFile(name)) {
                "No file was generated with name $name. Generated files: ${result.filesGeneratedByAnnotationProcessor.joinToString { it.name }}"
            }.readText().trim()

        fun loadClass(className: String): Class<*> = result.classLoader.loadClass(className)
    }

}