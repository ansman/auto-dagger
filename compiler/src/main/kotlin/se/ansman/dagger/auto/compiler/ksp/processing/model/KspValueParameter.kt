package se.ansman.dagger.auto.compiler.ksp.processing.model

import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.model.ValueParameter
import se.ansman.dagger.auto.compiler.common.processing.model.Type

data class KspValueParameter(
    override val node: KSValueParameter,
    override val resolver: KspResolver,
) : KspNode(), ValueParameter<KSNode, TypeName, ClassName> {
    override val name: String
        get() = node.name!!.asString()

    override val type: Type<KSNode, TypeName, ClassName> by lazy(LazyThreadSafetyMode.NONE) {
        KspType(node.type.resolve(), resolver)
    }
}