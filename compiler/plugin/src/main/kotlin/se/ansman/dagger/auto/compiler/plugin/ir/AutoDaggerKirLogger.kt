package se.ansman.dagger.auto.compiler.plugin.ir

import org.jetbrains.kotlin.backend.common.getCompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.util.fileOrNull
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger

class AutoDaggerKirLogger(
    private val messageCollector: MessageCollector,
    private val enableLogging: Boolean = false,
    private val tag: String = "[auto-dagger]",
) : AutoDaggerLogger<IrElement> {
    override fun withTag(tag: String): AutoDaggerKirLogger =
        AutoDaggerKirLogger(messageCollector, enableLogging, "${this.tag}[$tag]")

    override fun error(message: String, node: IrElement) = log(CompilerMessageSeverity.ERROR, message, node)
    override fun warning(message: String, node: IrElement) = log(CompilerMessageSeverity.WARNING, message, node)
    override fun info(message: String, node: IrElement?) {
        if (enableLogging) log(CompilerMessageSeverity.INFO, message, node)
    }

    private fun String.formatMessage() = "$tag $this"

    private fun log(severity: CompilerMessageSeverity, message: String, node: IrElement?) {
        messageCollector.report(severity, message.formatMessage(), node
            ?.let { it as? IrDeclaration }
            ?.run { getCompilerMessageLocation(fileOrNull ?: return@run null) })
    }
}