package se.ansman.dagger.auto.compiler.common.kapt.processing

import com.google.auto.common.MoreElements
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import kotlinx.metadata.ClassKind
import kotlinx.metadata.Visibility
import kotlinx.metadata.kind
import kotlinx.metadata.visibility
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind

data class KaptClassDeclaration(
    override val node: TypeElement,
    override val resolver: KaptResolver,
) : KaptNode(), ClassDeclaration<Element, TypeName, ClassName, AnnotationSpec> {
    val kmClass by lazy { resolver.kmClassLookup[node.qualifiedName.toString()] }

    override val className: ClassName by lazy(LazyThreadSafetyMode.NONE) { ClassName.get(node) }

    override val supertypes: List<KaptType> by lazy(LazyThreadSafetyMode.NONE) {
        buildList {
            superclass?.let { add(it) }
            node.interfaces.mapTo(this) { KaptType(it, resolver) }
        }
    }

    @Suppress("UnstableApiUsage")
    override val declaredNodes: List<KaptFunction> by lazy(LazyThreadSafetyMode.NONE) {
        node.enclosedElements
            .filter { it.kind == ElementKind.METHOD }
            .map(MoreElements::asExecutable)
            .map { KaptFunction(it, resolver) }
    }

    override val isObject: Boolean
        get() = kmClass?.kind == ClassKind.OBJECT

    override val isCompanionObject: Boolean
        get() = kmClass?.kind == ClassKind.COMPANION_OBJECT

    override val isGeneric: Boolean
        get() = node.typeParameters.isNotEmpty()

    override val isInterface: Boolean
        get() = node.kind == ElementKind.INTERFACE

    override val isPublic: Boolean
        get() = kmClass?.let { it.visibility == Visibility.PUBLIC } ?: (Modifier.PUBLIC in node.modifiers)

    override val isPrivate: Boolean
        get() = kmClass?.let { it.visibility == Visibility.PRIVATE } ?: false // Java classes cannot be private

    override val superclass: KaptType? by lazy(LazyThreadSafetyMode.NONE) {
        node.superclass
            .takeUnless { it.kind == TypeKind.NONE || it.isObject }
            ?.let { KaptType(it, resolver) }
    }

    override fun asType(): KaptType = KaptType(node.asType(), resolver)
}