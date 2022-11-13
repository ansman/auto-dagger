package se.ansman.deager

import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.function.Executable
import kotlin.test.assertEquals

class DeagerTestCase(
    private val testName: String,
    private val compilation: DeagerCompilation,
    private val sources: List<SourceFile>,
    private val expectedFiles: Map<String, String>,
) : Executable {
    override fun execute() {
        val result = compilation.compile(sources)
        result.assertIsSuccessful()
        for ((path, contents) in expectedFiles) {
            val actual = result.readGeneratedFile(path)
            assertEquals(contents, actual)
            try {
                result.loadClass(buildString {
                    append("se.ansman.")
                    append(testName.replace('-', '.'))
                    append(".")
                    append(path.substringBeforeLast("."))
                })
            } catch (e: Exception) {
                throw AssertionError("Failed to load class $path", e)
            }
        }
    }
}