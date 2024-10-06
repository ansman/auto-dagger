package se.ansman.dagger.auto.compiler.kapt.processing.model

import com.google.auto.common.MoreElements
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.model.Node
import se.ansman.dagger.auto.compiler.kapt.processing.KaptResolver
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind


sealed class KaptNode : Node<Element, TypeName, ClassName> {
    abstract val resolver: KaptResolver

    @Suppress("UnstableApiUsage")
    protected fun Element.toKaptDeclaration(): KaptDeclaration? = when (kind) {
        ElementKind.ANNOTATION_TYPE,
        ElementKind.INTERFACE,
        ElementKind.ENUM,
        ElementKind.CLASS -> KaptClassDeclaration(MoreElements.asType(this), resolver)

        ElementKind.METHOD,
        ElementKind.CONSTRUCTOR -> KaptFunctionDeclaration(MoreElements.asExecutable(this), resolver)
        else -> null
    }
}