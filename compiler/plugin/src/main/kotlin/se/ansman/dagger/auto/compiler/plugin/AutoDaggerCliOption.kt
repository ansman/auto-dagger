package se.ansman.dagger.auto.compiler.plugin

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption

enum class AutoDaggerCliOption(
    override val optionName: String,
    override val valueDescription: String,
    override val description: String,
    override val required: Boolean = false,
    override val allowMultipleOccurrences: Boolean = false
) : AbstractCliOption {
    EnableLogging(
        "enableLogging",
        "<true|false>",
        "Enables verbose logging. Defaults to `true`"
    ),
    DumpFilesTo(
        "dumpFilesTo",
        "<directory>",
        "If specified dumps the generates files as text to the specified directory"
    ),
}