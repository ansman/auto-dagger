package se.ansman.dagger.auto.kapt.processing

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.processing.ClassDeclaration
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind

data class KaptClassDeclaration(
    override val node: TypeElement,
    override val resolver: KaptResolver,
) : KaptNode(), ClassDeclaration<Element, TypeName, ClassName, AnnotationSpec> {
    override val className: ClassName by lazy(LazyThreadSafetyMode.NONE) { ClassName.get(node) }

    override val supertypes: List<KaptType> by lazy(LazyThreadSafetyMode.NONE) {
        buildList {
            superclass?.let { add(it) }
            node.interfaces.mapTo(this) { KaptType(it, resolver) }
        }
    }

    override val isCompanionObject: Boolean
        // We cannot determine this in Kapt
        get() = false

    override val isGeneric: Boolean
        get() = node.typeParameters.isNotEmpty()

    override val superclass: KaptType? by lazy(LazyThreadSafetyMode.NONE) {
        node.superclass
            .takeUnless { it.kind == TypeKind.NONE || it.isObject }
            ?.let { KaptType(it, resolver) }
    }
    override fun asType(): KaptType = KaptType(node.asType(), resolver)
}