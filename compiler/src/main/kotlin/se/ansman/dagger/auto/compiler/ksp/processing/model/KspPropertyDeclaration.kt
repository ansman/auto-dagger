package se.ansman.dagger.auto.compiler.ksp.processing.model

import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.model.PropertyDeclaration

data class KspPropertyDeclaration(
    override val node: KSPropertyDeclaration,
    override val resolver: KspResolver,
) : KspExecutableDeclaration(), PropertyDeclaration<KSNode, TypeName, ClassName> {
    override val annotations: List<KspAnnotationNode> by lazy(LazyThreadSafetyMode.NONE) {
        super.annotations.plus(node.getter?.annotations
            ?.map { KspAnnotationNode(it, resolver) }
            ?: emptySequence())
    }

    override val receiver: KspType? by lazy(LazyThreadSafetyMode.NONE) {
        node.extensionReceiver?.resolve()?.let { KspType(it, resolver) }
    }

    override val returnType: KspType by lazy(LazyThreadSafetyMode.NONE) {
        node.type.resolve().let { KspType(it, resolver) }
    }
}