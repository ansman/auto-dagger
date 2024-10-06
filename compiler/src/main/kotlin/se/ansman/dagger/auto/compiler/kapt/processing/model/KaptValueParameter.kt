package se.ansman.dagger.auto.compiler.kapt.processing.model

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.model.ValueParameter
import se.ansman.dagger.auto.compiler.kapt.processing.KaptResolver
import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement

data class KaptValueParameter(
    override val node: VariableElement,
    override val resolver: KaptResolver
) : KaptNode(), ValueParameter<Element, TypeName, ClassName> {
    override val name: String
        get() = node.simpleName.toString()

    override val type: KaptType by lazy(LazyThreadSafetyMode.NONE) {
        KaptType(node.asType(), resolver)
    }
}