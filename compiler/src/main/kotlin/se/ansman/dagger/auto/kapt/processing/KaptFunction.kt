package se.ansman.dagger.auto.kapt.processing

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.processing.Function
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

data class KaptFunction(
    override val node: ExecutableElement,
    override val resolver: KaptResolver,
) : KaptNode(), Function<Element, TypeName, ClassName, AnnotationSpec> {
    override val arguments: Sequence<KaptType>
        get() = node.parameters.asSequence().map { KaptType(it.asType(), resolver) }

    override val name: String
        get() = node.simpleName.toString()

    override val receiver: KaptType?
        // We don't have access to receivers here
        get() = null

    override val returnType: KaptType
        get() = KaptType(node.returnType, resolver)
}