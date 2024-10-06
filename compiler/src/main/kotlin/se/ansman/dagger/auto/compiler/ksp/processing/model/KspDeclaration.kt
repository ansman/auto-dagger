package se.ansman.dagger.auto.compiler.ksp.processing.model

import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.model.Declaration

sealed class KspDeclaration : KspNode(), Declaration<KSNode, TypeName, ClassName> {
    abstract override val node: KSDeclaration

    final override val isPublic: Boolean
        get() = node.isPublic()

    final override val isPrivate: Boolean
        get() = node.isPrivate()

    override val isGeneric: Boolean
        get() = node.typeParameters.isNotEmpty()

    override val isAbstract: Boolean
        get() = Modifier.ABSTRACT in node.modifiers

    override val annotations: List<KspAnnotationNode> by lazy(LazyThreadSafetyMode.NONE) {
        node.annotations.map { KspAnnotationNode(it, resolver) }.toList()
    }

    override val enclosingDeclaration: Declaration<KSNode, TypeName, ClassName>? by lazy(LazyThreadSafetyMode.NONE) {
        node.parentDeclaration?.toKspDeclaration()
    }
}