package se.ansman.dagger.auto.compiler.common.ksp.processing

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.processing.ExecutableNode

abstract class KspExecutableNode : KspNode(), ExecutableNode<KSDeclaration, TypeName, ClassName, AnnotationSpec> {
    final override val enclosingType: KspClassDeclaration? by lazy(LazyThreadSafetyMode.NONE) {
        (node.parentDeclaration as KSClassDeclaration?)?.let {
            KspClassDeclaration(it, resolver)
        }
    }

    final override val name: String
        get() = node.simpleName.asString()
}