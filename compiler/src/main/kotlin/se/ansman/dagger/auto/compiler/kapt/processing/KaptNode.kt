package se.ansman.dagger.auto.compiler.kapt.processing

import com.google.auto.common.MoreElements
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.Node
import javax.lang.model.element.Element

sealed class KaptNode : Node<Element, TypeName, ClassName, AnnotationSpec> {
    abstract val resolver: KaptResolver
    override val annotations: List<KaptAnnotationModel> by lazy(LazyThreadSafetyMode.NONE) {
        node.annotationMirrors.map { (KaptAnnotationModel(it, resolver)) }
    }

    @Suppress("UnstableApiUsage")
    override val enclosingType: KaptClassDeclaration? by lazy(LazyThreadSafetyMode.NONE) {
        node.enclosingElement
            ?.takeIf { it.kind.isClass || it.kind.isInterface }
            ?.let(MoreElements::asType)
            ?.let { KaptClassDeclaration(it, resolver) }
    }
}