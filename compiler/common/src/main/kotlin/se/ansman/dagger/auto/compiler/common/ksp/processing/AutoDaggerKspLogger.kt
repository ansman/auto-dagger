package se.ansman.dagger.auto.compiler.common.ksp.processing

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger

class AutoDaggerKspLogger(
    private val logger: KSPLogger,
    private val enableLogging: Boolean = false,
    private val tag: String = "[auto-dagger]",
) : AutoDaggerLogger<KSNode> {
    override fun withTag(tag: String): AutoDaggerKspLogger =
        AutoDaggerKspLogger(logger, enableLogging, "${this.tag}[$tag]")
    override fun error(message: String, node: KSNode) = logger.error(message.formatMessage(), node)
    override fun warning(message: String, node: KSNode) = logger.warn(message.formatMessage(), node)
    override fun info(message: String, node: KSNode?) {
        if (enableLogging) logger.info(message.formatMessage(), node)
    }
    private fun String.formatMessage() = "$tag $this"
}