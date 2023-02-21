package se.ansman.dagger.auto.ksp.processing

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.processing.Function

data class KspFunction(
    override val node: KSFunctionDeclaration,
    override val processing: KspProcessing,
) : KspNode(), Function<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    override val enclosingType: KspClassDeclaration? by lazy(LazyThreadSafetyMode.NONE) {
        (node.parentDeclaration as KSClassDeclaration?)?.let {
            KspClassDeclaration(it, processing)
        }
    }

    override val arguments: Sequence<KspType> by lazy(LazyThreadSafetyMode.NONE) {
        node.parameters.map { KspType(it.type.resolve(), processing) }.asSequence()
    }
    override val name: String
        get() = node.simpleName.asString()

    override val receiver: KspType? by lazy(LazyThreadSafetyMode.NONE) {
        node.extensionReceiver?.resolve()?.let { KspType(it, processing) }
    }

    override val returnType: KspType by lazy(LazyThreadSafetyMode.NONE) {
        node.returnType?.resolve()?.let { KspType(it, processing) } ?: run {
            processing.logError("Could not determine return type", node)
            KspType(processing.resolver.builtIns.nothingType, processing)
        }
    }
}