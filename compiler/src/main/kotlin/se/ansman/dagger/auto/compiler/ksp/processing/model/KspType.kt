package se.ansman.dagger.auto.compiler.ksp.processing.model

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import se.ansman.dagger.auto.compiler.common.processing.model.Type
import se.ansman.dagger.auto.compiler.ksp.processing.unwrapTypeAlias

data class KspType(
    val type: KSType,
    private val resolver: KspResolver,
) : Type<KSNode, TypeName, ClassName> {
    override val declaration: KspClassDeclaration? by lazy(LazyThreadSafetyMode.NONE) {
        val declaration = type.unwrapTypeAlias().declaration as? KSClassDeclaration
            ?: return@lazy null
        KspClassDeclaration(declaration, resolver)
    }

    override val isGeneric: Boolean
        get() = type.arguments.isNotEmpty()

    override fun toTypeName(): TypeName = type.toTypeName()
    override fun isAssignableTo(type: Type<KSNode, TypeName, ClassName>): Boolean =
        isAssignableTo((type as KspType).type)

    override fun isAssignableTo(type: ClassName): Boolean =
        isAssignableTo(resolver.lookupType(type).node.asStarProjectedType())

    override fun isAssignableTo(type: String): Boolean =
        isAssignableTo(ClassName.bestGuess(type))

    private fun isAssignableTo(type: KSType): Boolean = type.isAssignableFrom(this.type)
}