package se.ansman.dagger.auto.compiler.ksp.processing

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode
import se.ansman.dagger.auto.compiler.processing.AutoDaggerLogger

class AutoDaggerKspLogger(
    private val logger: KSPLogger,
    private val tag: String = "[auto-dagger]",
) : AutoDaggerLogger<KSNode> {
    override fun withTag(tag: String): AutoDaggerKspLogger = AutoDaggerKspLogger(logger, "${this.tag}[$tag]")
    override fun error(message: String, node: KSNode) = logger.error(message.formatMessage(), node)
    override fun warning(message: String, node: KSNode) = logger.warn(message.formatMessage(), node)
    override fun info(message: String, node: KSNode?) = logger.info(message.formatMessage(), node)
    private fun String.formatMessage() = "$tag $this"
}