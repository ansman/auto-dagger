package se.ansman.dagger.auto.ksp.processing

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import se.ansman.dagger.auto.processing.ClassDeclaration
import se.ansman.dagger.auto.processing.Type

data class KspClassDeclaration(
    override val node: KSClassDeclaration,
    override val processing: KspProcessing,
) : KspNode(), ClassDeclaration<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    override val supertypes: Sequence<KspType> by lazy(LazyThreadSafetyMode.NONE) {
        node.superTypes.map { KspType(it.resolve(), processing) }.toList().asSequence()
    }

    override val enclosingType: KspClassDeclaration? by lazy(LazyThreadSafetyMode.NONE) {
        (node.parentDeclaration as KSClassDeclaration?)?.let {
            KspClassDeclaration(it, processing)
        }
    }

    override val isCompanionObject: Boolean
        get() = node.isCompanionObject

    override val superclass: Type<KSDeclaration, TypeName, ClassName, AnnotationSpec>?
        get() = supertypes.find { it.declaration.node.classKind == ClassKind.CLASS }

    override fun toClassName(): ClassName = node.toClassName()

    override fun asType(): KspType = KspType(node.asStarProjectedType(), processing)
}