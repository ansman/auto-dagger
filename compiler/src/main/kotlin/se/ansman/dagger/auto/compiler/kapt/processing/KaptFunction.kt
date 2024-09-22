package se.ansman.dagger.auto.compiler.kapt.processing

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.Function
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier

data class KaptFunction(
    override val node: ExecutableElement,
    override val resolver: KaptResolver,
) : KaptNode(), Function<Element, TypeName, ClassName, AnnotationSpec> {
    override val arguments: Sequence<KaptType> by lazy(LazyThreadSafetyMode.NONE) {
        node.parameters.asSequence().map { KaptType(it.asType(), resolver) }
    }

    override val name: String
        get() = node.simpleName.toString()

    override val receiver: KaptType?
        // We don't have access to receivers here
        get() = null

    override val returnType: KaptType by lazy(LazyThreadSafetyMode.NONE) {
        KaptType(node.returnType, resolver)
    }

    override val isPublic: Boolean
        get() = Modifier.PUBLIC in node.modifiers

    override val isPrivate: Boolean
        get() = Modifier.PRIVATE in node.modifiers

    override val isConstructor: Boolean
        get() = node.kind == ElementKind.CONSTRUCTOR
}