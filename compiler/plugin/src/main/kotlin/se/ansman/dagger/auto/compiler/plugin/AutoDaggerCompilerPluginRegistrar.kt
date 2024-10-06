package se.ansman.dagger.auto.compiler.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import se.ansman.dagger.auto.compiler.plugin.ir.AutoDaggerIrGenerationExtension

@AutoService(CompilerPluginRegistrar::class)
class AutoDaggerCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean
        get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        IrGenerationExtension.registerExtension(
            AutoDaggerIrGenerationExtension(
                messageCollector = configuration.get(
                    CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY,
                    MessageCollector.NONE
                ),
                enableLogging = configuration.get(AutoDaggerConfiguration.enableLogging, false),
                dumpFilesTo = configuration.get(AutoDaggerConfiguration.dumpFilesTo)
            )
        )
    }
}