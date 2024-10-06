package se.ansman.dagger.auto.compiler.ksp.processing.model

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.model.FunctionDeclaration

data class KspFunctionDeclaration(
    override val node: KSFunctionDeclaration,
    override val resolver: KspResolver,
) : KspExecutableDeclaration(), FunctionDeclaration<KSNode, TypeName, ClassName> {
    override val valueParameters: List<KspValueParameter> by lazy(LazyThreadSafetyMode.NONE) {
        node.parameters.map { KspValueParameter(it, resolver) }
    }

    override val receiver: KspType? by lazy(LazyThreadSafetyMode.NONE) {
        node.extensionReceiver?.resolve()?.let { KspType(it, resolver) }
    }

    override val returnType: KspType by lazy(LazyThreadSafetyMode.NONE) {
        node.returnType?.resolve()?.let { KspType(it, resolver) } ?: run {
            resolver.environment.logger.error("Could not determine return type", node)
            KspType(resolver.resolver.builtIns.nothingType, resolver)
        }
    }

    override val isConstructor: Boolean get() = node.isConstructor()
}