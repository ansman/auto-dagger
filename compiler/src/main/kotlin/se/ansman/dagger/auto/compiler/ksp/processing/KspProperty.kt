package se.ansman.dagger.auto.compiler.ksp.processing

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.Property

data class KspProperty(
    override val node: KSPropertyDeclaration,
    override val resolver: KspResolver,
) : KspExecutableNode(), Property<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    override val annotations: List<KspAnnotationModel> by lazy(LazyThreadSafetyMode.NONE) {
        (node.annotations + (node.getter?.annotations ?: emptySequence()))
            .map { KspAnnotationModel(it, resolver) }
            .toList()
    }

    override val receiver: KspType? by lazy(LazyThreadSafetyMode.NONE) {
        node.extensionReceiver?.resolve()?.let { KspType(it, resolver) }
    }

    override val returnType: KspType by lazy(LazyThreadSafetyMode.NONE) {
        node.type.resolve().let { KspType(it, resolver) }
    }
}