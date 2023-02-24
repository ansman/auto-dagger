package se.ansman.dagger.auto.ksp.processing

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.processing.Node

sealed class KspNode : Node<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    protected abstract val processing: KspProcessing

    override val annotations: List<KspAnnotationModel> by lazy(LazyThreadSafetyMode.NONE) {
        node.annotations.map(::KspAnnotationModel).toList()
    }

    override val isPublic: Boolean
        get() = node.isPublic()
}