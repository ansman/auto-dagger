package se.ansman.dagger.auto.compiler.common.kapt.processing

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import kotlinx.metadata.ClassKind
import kotlinx.metadata.kind
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind

data class KaptClassDeclaration(
    override val node: TypeElement,
    override val resolver: KaptResolver,
) : KaptNode(), ClassDeclaration<Element, TypeName, ClassName, AnnotationSpec> {
    private val kmClass by lazy { resolver.kmClassLookup[node.qualifiedName.toString()] }

    override val className: ClassName by lazy(LazyThreadSafetyMode.NONE) { ClassName.get(node) }

    override val supertypes: List<KaptType> by lazy(LazyThreadSafetyMode.NONE) {
        buildList {
            superclass?.let { add(it) }
            node.interfaces.mapTo(this) { KaptType(it, resolver) }
        }
    }

    override val isObject: Boolean
        get() = kmClass?.kind == ClassKind.OBJECT

    override val isCompanionObject: Boolean
        get() = kmClass?.kind == ClassKind.COMPANION_OBJECT

    override val isGeneric: Boolean
        get() = node.typeParameters.isNotEmpty()

    override val superclass: KaptType? by lazy(LazyThreadSafetyMode.NONE) {
        node.superclass
            .takeUnless { it.kind == TypeKind.NONE || it.isObject }
            ?.let { KaptType(it, resolver) }
    }

    override fun asType(): KaptType = KaptType(node.asType(), resolver)
}