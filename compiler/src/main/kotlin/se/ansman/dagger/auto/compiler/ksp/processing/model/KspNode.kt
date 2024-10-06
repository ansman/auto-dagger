package se.ansman.dagger.auto.compiler.ksp.processing.model

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSPropertyGetter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.model.Node

sealed class KspNode : Node<KSNode, TypeName, ClassName> {
    abstract val resolver: KspResolver

    protected fun KSDeclaration.toKspDeclaration() = toKspDeclaration(resolver)
}

fun KSAnnotated.toKspDeclaration(resolver: KspResolver) = when (this) {
    is KSFunctionDeclaration -> KspFunctionDeclaration(this, resolver)
    is KSPropertyDeclaration -> KspPropertyDeclaration(this, resolver)
    is KSPropertyGetter -> KspPropertyDeclaration(parent as KSPropertyDeclaration, resolver)
    is KSClassDeclaration -> KspClassDeclaration(this, resolver)
    else -> null
}