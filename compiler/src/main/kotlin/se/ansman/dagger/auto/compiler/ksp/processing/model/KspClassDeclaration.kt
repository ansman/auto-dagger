package se.ansman.dagger.auto.compiler.ksp.processing.model

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration.Kind

data class KspClassDeclaration(
    override val node: KSClassDeclaration,
    override val resolver: KspResolver,
) : KspDeclaration(), ClassDeclaration<KSNode, TypeName, ClassName> {

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

    override val name: String
        get() = className.simpleName

    override val superTypes: List<KspType> by lazy(LazyThreadSafetyMode.NONE) {
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

    override val enclosingDeclaration: KspClassDeclaration?
        get() = super.enclosingDeclaration as KspClassDeclaration?

    override val declarations: List<KspDeclaration> by lazy(LazyThreadSafetyMode.NONE) {
        node.declarations
            .mapNotNull { it.toKspDeclaration() }
            .toList()
    }

    override val isGeneric: Boolean
        get() = node.typeParameters.isNotEmpty()

    override val isAbstract: Boolean
        get() = node.isAbstract()

    override val isSealedClass: Boolean
        get() = Modifier.SEALED in node.modifiers

    override val superclass: KspType?
        get() = superTypes.find { it.declaration?.node?.classKind == ClassKind.CLASS }

    override fun asType(): KspType = KspType(node.asStarProjectedType(), resolver)

    override fun isType(className: ClassName): Boolean = isType(className.canonicalName)

    override fun isType(qualifiedName: String): Boolean = className.canonicalName == qualifiedName
}