package se.ansman.dagger.auto.compiler.ksp.processing

import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.Node

sealed class KspNode : Node<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    abstract val resolver: KspResolver

    override val annotations: List<KspAnnotationModel> by lazy(LazyThreadSafetyMode.NONE) {
        node.annotations.map { KspAnnotationModel(it, resolver) }.toList()
    }

    override val isPublic: Boolean
        get() = node.isPublic()

    override val isPrivate: Boolean
        get() = node.isPrivate()
}