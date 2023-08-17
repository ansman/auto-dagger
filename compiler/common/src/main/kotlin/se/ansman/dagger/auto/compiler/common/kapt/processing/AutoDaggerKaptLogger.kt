package se.ansman.dagger.auto.compiler.common.kapt.processing

import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.tools.Diagnostic.Kind

class AutoDaggerKaptLogger(
    private val messager: Messager,
    private val enableLogging: Boolean,
    private val tag: String = "[auto-dagger]",
) : AutoDaggerLogger<Element> {
    override fun withTag(tag: String): AutoDaggerKaptLogger =
        AutoDaggerKaptLogger(messager, enableLogging, "${this.tag}[$tag]")

    override fun error(message: String, node: Element) = log(Kind.ERROR, message, node)
    override fun warning(message: String, node: Element) = log(Kind.WARNING, message, node)
    override fun info(message: String, node: Element?) {
        if (enableLogging) log(Kind.NOTE, message, node)
    }

    private fun log(kind: Kind, message: String, node: Element?) =
        messager.printMessage(kind, "$tag $message", node)
}