package se.ansman.dagger.auto.ksp.processing

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.writeTo
import se.ansman.dagger.auto.ksp.KotlinPoetRenderEngine
import se.ansman.dagger.auto.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.processing.RenderEngine

class KspEnvironment(
    val environment: SymbolProcessorEnvironment,
) : AutoDaggerEnvironment<KSNode, TypeName, ClassName, AnnotationSpec, FileSpec> {
    override val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>
        get() = KotlinPoetRenderEngine

    override fun logError(message: String, node: KSNode) =
        environment.logger.error(message.formatMessage(), node)

    override fun logWarning(message: String, node: KSNode) =
        environment.logger.warn(message.formatMessage(), node)

    override fun logInfo(message: String, node: KSNode?) =
        environment.logger.info(message.formatMessage(), node)

    private fun String.formatMessage() = "Auto Dagger: $this"

    override fun write(file: FileSpec) {
        file.writeTo(environment.codeGenerator, aggregating = false)
    }
}