package se.ansman.dagger.auto.compiler.ksp.processing

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration.Kind
import se.ansman.dagger.auto.compiler.common.processing.Type

data class KspClassDeclaration(
    override val node: KSClassDeclaration,
    override val resolver: KspResolver,
) : KspNode(), ClassDeclaration<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    override val kind: Kind
        get() = when (node.classKind) {
            ClassKind.INTERFACE -> Kind.Interface
            ClassKind.CLASS -> Kind.Class
            ClassKind.ENUM_CLASS -> Kind.EnumClass
            ClassKind.ENUM_ENTRY -> Kind.EnumEntry
            ClassKind.OBJECT -> if (node.isCompanionObject) {
                Kind.CompanionObject
            } else {
                Kind.Object
            }
            ClassKind.ANNOTATION_CLASS -> Kind.AnnotationClass
        }

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

    override val declaredNodes: List<KspExecutableNode> by lazy(LazyThreadSafetyMode.NONE) {
        node.declarations
            .mapNotNull {
                when (it) {
                    is KSFunctionDeclaration -> KspFunction(it, resolver)
                    is KSPropertyDeclaration -> KspProperty(it, resolver)
                    else -> null
                }
            }
            .toList()
    }

    override val enclosingType: KspClassDeclaration? by lazy(LazyThreadSafetyMode.NONE) {
        (node.parentDeclaration as KSClassDeclaration?)?.let {
            KspClassDeclaration(it, resolver)
        }
    }

    override val isGeneric: Boolean
        get() = node.typeParameters.isNotEmpty()

    override val isAbstract: Boolean
        get() = node.isAbstract()

    override val isSealedClass: Boolean
        get() = Modifier.SEALED in node.modifiers

    override val superclass: Type<KSDeclaration, TypeName, ClassName, AnnotationSpec>?
        get() = supertypes.find { it.declaration?.node?.classKind == ClassKind.CLASS }

    override fun asType(): KspType = KspType(node.asStarProjectedType(), resolver)
}