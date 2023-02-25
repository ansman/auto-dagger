package se.ansman.dagger.auto.ksp.processing

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import se.ansman.dagger.auto.processing.Type
import kotlin.reflect.KClass

data class KspType(
    val node: KSType,
    private val resolver: KspResolver,
) : Type<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    override val declaration: KspClassDeclaration by lazy(LazyThreadSafetyMode.NONE) {
        KspClassDeclaration(node.unwrapTypeAlias().declaration as KSClassDeclaration, resolver)
    }

    override fun toTypeName(): TypeName = node.toTypeName()

    override fun isAssignableTo(type: KClass<*>): Boolean = isAssignableTo(type.qualifiedName!!)

    override fun isAssignableTo(type: ClassName): Boolean = isAssignableTo(type.canonicalName)

    private fun isAssignableTo(type: String): Boolean =
        resolver.typeLookup[type].node.asStarProjectedType().isAssignableFrom(node)
}