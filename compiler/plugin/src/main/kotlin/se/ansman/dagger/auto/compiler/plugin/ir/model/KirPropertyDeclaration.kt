package se.ansman.dagger.auto.compiler.plugin.ir.model

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrProperty
import se.ansman.dagger.auto.compiler.common.processing.model.AnnotationNode
import se.ansman.dagger.auto.compiler.common.processing.model.PropertyDeclaration
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment

data class KirPropertyDeclaration(
    override val node: IrProperty,
    override val environment: KirEnvironment,
) : KirExecutableDeclaration(), PropertyDeclaration<IrElement, KirTypeName, KirClassName> {
    override val name: String get() = node.name.asString()
    override val isAbstract: Boolean
        // TODO: Check if this is correct
        get() = node.backingField == null && node.getter == null && !node.isDelegated

    override val annotations: List<AnnotationNode<IrElement, KirTypeName, KirClassName>> by lazy(LazyThreadSafetyMode.NONE) {
        super.annotations + (node.getter?.annotations?.map { KirAnnotationNode(it, environment) } ?: emptyList())
    }

    override val receiver: KirType? by lazy(LazyThreadSafetyMode.NONE) {
        node.getter?.extensionReceiverParameter?.type?.toKirType()
    }

    override val returnType: KirType by lazy(LazyThreadSafetyMode.NONE) {
        node.getter?.returnType?.toKirType()
            ?: node.backingField?.type?.toKirType()
            ?: error("Could not determine return type for $node")
    }

    override val valueParameters: List<KirValueParameter>
        get() = emptyList()
}