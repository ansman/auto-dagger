package se.ansman.dagger.auto

import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language
import java.io.File as JavaFile

sealed class TestSourceFile {
    abstract val name: String
    abstract val language: SourceLanguage
    abstract fun toSourceFile(): SourceFile
    abstract fun asString(): String

    data class File(
        val file: JavaFile,
        override val language: SourceLanguage = SourceLanguage.fromExtension(file.extension)
    ) : TestSourceFile() {
        override val name: String get() = file.name
        override fun toSourceFile(): SourceFile = SourceFile.fromPath(file)
        override fun asString(): String = file.readText()
    }

    data class Inline(
        override val name: String,
        val contents: String,
        override val language: SourceLanguage
    ) : TestSourceFile() {
        override fun toSourceFile(): SourceFile = SourceFile.new(name, contents)
        override fun asString(): String = contents

        companion object {
            fun kotlin(@Language("kotlin") contents: String, name: String = "Test.kt") =
                Inline(name, contents.trimIndent(), SourceLanguage.Kotlin)

            fun java(@Language("java") contents: String, name: String = "Test.java") =
                Inline(name, contents.trimIndent(), SourceLanguage.Java)
        }
    }

    enum class SourceLanguage {
        Kotlin,
        Java;

        companion object {
            fun fromExtension(extension: String): SourceLanguage =
                when (extension) {
                    "kt", "kts" -> Kotlin
                    "java" -> Java
                    else -> error("Unknown extension $extension")
                }
        }
    }
}
