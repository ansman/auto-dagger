package se.ansman.dagger.auto.compiler.ksp.processing.model

import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.model.ExecutableDeclaration

abstract class KspExecutableDeclaration : KspDeclaration(), ExecutableDeclaration<KSNode, TypeName, ClassName> {
    final override val name: String
        get() = node.simpleName.asString()
}