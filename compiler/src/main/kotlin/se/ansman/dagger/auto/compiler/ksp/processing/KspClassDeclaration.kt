package se.ansman.dagger.auto.compiler.ksp.processing

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import se.ansman.dagger.auto.compiler.processing.ClassDeclaration
import se.ansman.dagger.auto.compiler.processing.Type

data class KspClassDeclaration(
    override val node: KSClassDeclaration,
    override val resolver: KspResolver,
) : KspNode(), ClassDeclaration<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    override val className: ClassName by lazy(LazyThreadSafetyMode.NONE) {
        node.toClassName()
    }

    override val supertypes: List<KspType> by lazy(LazyThreadSafetyMode.NONE) {
        node.superTypes
            .mapNotNull {
                val type = it.resolve()
                if (type == resolver.resolver.builtIns.anyType) {
                    null
                } else {
                    KspType(type, resolver)
                }
            }
            .toList()
    }

    override val enclosingType: KspClassDeclaration? by lazy(LazyThreadSafetyMode.NONE) {
        (node.parentDeclaration as KSClassDeclaration?)?.let {
            KspClassDeclaration(it, resolver)
        }
    }

    override val isCompanionObject: Boolean
        get() = node.isCompanionObject

    override val isGeneric: Boolean
        get() = node.typeParameters.isNotEmpty()

    override val superclass: Type<KSDeclaration, TypeName, ClassName, AnnotationSpec>?
        get() = supertypes.find { it.declaration.node.classKind == ClassKind.CLASS }

    override fun asType(): KspType = KspType(node.asStarProjectedType(), resolver)
}