package se.ansman.dagger.auto.compiler.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.io.File

object AutoDaggerConfiguration {
    val enableLogging = CompilerConfigurationKey.create<Boolean>("enableLogging")
    val dumpFilesTo = CompilerConfigurationKey.create<File>("dumpFilesTo")
}


const val AUTO_DAGGER_PLUGIN_ID = "se.ansman.dagger.auto"

@AutoService(CommandLineProcessor::class)
class AutoDaggerCommandLineProcessor : CommandLineProcessor {
    override val pluginId = AUTO_DAGGER_PLUGIN_ID

    override val pluginOptions: Collection<AbstractCliOption> = AutoDaggerCliOption.entries

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        if (option !is AutoDaggerCliOption) {
            throw CliOptionProcessingException("Unknown option: ${option.optionName}")
        }
        when (option) {
            AutoDaggerCliOption.EnableLogging -> configuration.put(AutoDaggerConfiguration.enableLogging, value.toBoolean())
            AutoDaggerCliOption.DumpFilesTo -> configuration.put(AutoDaggerConfiguration.dumpFilesTo, File(value))
        }
    }
}