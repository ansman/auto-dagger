package se.ansman.dagger.auto.compiler

import com.tschuchort.compiletesting.KotlinCompilation
import org.junit.jupiter.api.function.Executable
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.fail

class ResourceTestCase(
    val testType: String,
    val testName: String,
    val compilation: Compilation,
    val sources: List<TestSourceFile>,
    val writeExpectedFilesTo: File?,
    private val expectedFiles: Map<String, String>,
    private val configure: KotlinCompilation.() -> Unit
) : Executable {
    override fun execute() {
        val result = compilation.compile(sources, configure)
        result.assertIsSuccessful()
        if (writeExpectedFilesTo != null) {
            result.writeExpectedFiles(writeExpectedFilesTo)
            return
        }
        val generated = result.filesGeneratedByAnnotationProcessor
        for ((fileName, contents) in expectedFiles) {
            val actual = result.getGeneratedFile(fileName)
            assertEquals(contents, actual, "$fileName did not match expected contents.")
            try {
                result.loadClass(buildString {
                    append("tests")
                    append('.')
                    append(testType)
                    append('.')
                    append(testName)
                    append('.')
                    append(fileName.substringBeforeLast("."))
                })
            } catch (e: Exception) {
                throw AssertionError("Failed to load class $fileName", e)
            }
        }
        val unexpected = generated.keys - expectedFiles.keys
        if (unexpected.isNotEmpty()) {
            fail(unexpected.joinToString(
                prefix = "Unexpected files were generated: \n\n",
                separator = "\n\n",
                transform = { formatFile(it, generated.getValue(it), includeLineNumbers = false) }
            ))
        }
    }

    private fun Compilation.Result.writeExpectedFiles(writeExpectedFilesTo: File) {
        writeExpectedFilesTo.deleteRecursively()
        writeExpectedFilesTo.mkdirs()
        filesGeneratedByAnnotationProcessor.forEach { (name, contents) ->
            writeExpectedFilesTo.resolve("$name.txt").writeText(contents.trim())
        }
    }

    override fun toString(): String = "AutoDaggerTestCase($testName)"
}