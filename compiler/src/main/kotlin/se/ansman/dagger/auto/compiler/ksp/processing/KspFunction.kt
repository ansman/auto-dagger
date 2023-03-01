package se.ansman.dagger.auto.compiler.ksp.processing

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.processing.Function

data class KspFunction(
    override val node: KSFunctionDeclaration,
    override val resolver: KspResolver,
) : KspNode(), Function<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    override val enclosingType: KspClassDeclaration? by lazy(LazyThreadSafetyMode.NONE) {
        (node.parentDeclaration as KSClassDeclaration?)?.let {
            KspClassDeclaration(it, resolver)
        }
    }

    override val arguments: Sequence<KspType> by lazy(LazyThreadSafetyMode.NONE) {
        node.parameters.map { KspType(it.type.resolve(), resolver) }.asSequence()
    }
    override val name: String
        get() = node.simpleName.asString()

    override val receiver: KspType? by lazy(LazyThreadSafetyMode.NONE) {
        node.extensionReceiver?.resolve()?.let { KspType(it, resolver) }
    }

    override val returnType: KspType by lazy(LazyThreadSafetyMode.NONE) {
        node.returnType?.resolve()?.let { KspType(it, resolver) } ?: run {
            resolver.environment.logger.error("Could not determine return type", node)
            KspType(resolver.resolver.builtIns.nothingType, resolver)
        }
    }
}