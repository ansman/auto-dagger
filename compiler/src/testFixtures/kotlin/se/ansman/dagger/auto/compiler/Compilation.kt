package se.ansman.dagger.auto.compiler

import assertk.assertThat
import assertk.assertions.contains
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import org.intellij.lang.annotations.Language
import java.io.File
import java.io.OutputStream
import kotlin.test.assertEquals

abstract class Compilation(
    protected val workingDir: File
) {
    open fun File.readFile(): String = readText()

    fun compile(@Language("kotlin") vararg sources: String, configuration: KotlinCompilation.() -> Unit = {}): Result =
        compile(
            sources = sources.mapIndexed { i, contents ->
                TestSourceFile.Inline.kotlin(contents, name = if (sources.size == 1) "Test.kt" else "Test$i.kt")
            },
            configuration = configuration
        )

    fun compile(vararg sources: TestSourceFile, configuration: KotlinCompilation.() -> Unit = {}): Result =
        compile(sources.asList(), configuration)

    fun compile(sources: List<TestSourceFile>, configuration: KotlinCompilation.() -> Unit = {}): Result =
        doCompile(sources) {
            workingDir = this@Compilation.workingDir
            inheritClassPath = true
            messageOutputStream = OutputStream.nullOutputStream()
            configuration()
        }

    protected abstract fun doCompile(
        sources: List<TestSourceFile>,
        configuration: KotlinCompilation.() -> Unit
    ): Result

    protected abstract fun JvmCompilationResult.readFilesGeneratedByAnnotationProcessor(): Map<String, String>

    companion object {
        val mutex = Any()
    }

    interface Factory {
        val expectedFilesDirectoryName: String
        fun create(workingDir: File): Compilation
    }

    inner class Result(private val result: JvmCompilationResult) {
        val exitCode: KotlinCompilation.ExitCode get() = result.exitCode
        val messages: String get() = result.messages
        val filesGeneratedByAnnotationProcessor: Map<String, String> = result.readFilesGeneratedByAnnotationProcessor()

        private val errorMessage = filesGeneratedByAnnotationProcessor.entries.joinToString(
            prefix = "$messages\n\n",
            separator = "\n\n",
            transform = { formatFile(it.key, it.value.trim()) }
        )

        fun assertIsSuccessful(): Result = apply {
            assertEquals(KotlinCompilation.ExitCode.OK, exitCode, errorMessage)
        }

        fun assertFailedWithMessage(message: String): Result = apply {
            assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, exitCode, errorMessage)
            assertThat(messages).contains(message)
        }

        fun findGeneratedFile(name: String): String? = filesGeneratedByAnnotationProcessor[name]

        fun getGeneratedFile(name: String): String =
            requireNotNull(findGeneratedFile(name)) {
                "No file was generated with name $name. Generated files: ${filesGeneratedByAnnotationProcessor.keys}\n$errorMessage".trim()
            }

        fun loadClass(className: String): Class<*> = result.classLoader.loadClass(className)
    }
}