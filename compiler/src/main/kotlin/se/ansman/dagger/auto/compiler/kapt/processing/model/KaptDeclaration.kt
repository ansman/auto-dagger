package se.ansman.dagger.auto.compiler.kapt.processing.model

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.model.Declaration
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

sealed class KaptDeclaration : KaptNode(), Declaration<Element, TypeName, ClassName> {
    abstract override val node: Element

    override val name: String get() = node.simpleName.toString()

    override val annotations: List<KaptAnnotationNode> by lazy(LazyThreadSafetyMode.NONE) {
        node.annotationMirrors.map { (KaptAnnotationNode(it, resolver)) }
    }

    override val isAbstract: Boolean
        get() = Modifier.ABSTRACT in node.modifiers

    override val enclosingDeclaration: Declaration<Element, TypeName, ClassName>? by lazy(LazyThreadSafetyMode.NONE) {
        node.enclosingElement?.toKaptDeclaration()
    }
}