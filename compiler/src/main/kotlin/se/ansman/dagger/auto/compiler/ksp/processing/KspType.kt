package se.ansman.dagger.auto.compiler.ksp.processing

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import se.ansman.dagger.auto.compiler.processing.Type
import kotlin.reflect.KClass

data class KspType(
    val type: KSType,
    private val resolver: KspResolver,
) : Type<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    override val declaration: KspClassDeclaration by lazy(LazyThreadSafetyMode.NONE) {
        KspClassDeclaration(type.unwrapTypeAlias().declaration as KSClassDeclaration, resolver)
    }

    override fun toTypeName(): TypeName = type.toTypeName()
    override fun isAssignableTo(type: Type<KSDeclaration, TypeName, ClassName, AnnotationSpec>): Boolean =
        isAssignableTo((type as KspType).type)
    override fun isAssignableTo(type: KClass<*>): Boolean = isAssignableTo(type.qualifiedName!!)
    override fun isAssignableTo(type: ClassName): Boolean = isAssignableTo(type.canonicalName)

    private fun isAssignableTo(type: String): Boolean =
        isAssignableTo(resolver.typeLookup[type].node.asStarProjectedType())

    private fun isAssignableTo(type: KSType): Boolean = type.isAssignableFrom(this.type)
}