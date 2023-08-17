package se.ansman.dagger.auto.compiler.utils

import dagger.hilt.DefineComponent
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.Node
import se.ansman.dagger.auto.compiler.common.processing.error
import se.ansman.dagger.auto.compiler.common.processing.getAnnotation

object ComponentValidator {
    fun <N> ClassDeclaration<N, *, *, *>.validateComponent(
        node: Node<N, *, *, *>,
        logger: AutoDaggerLogger<N>?,
    ): Boolean {
        val defineComponent = getAnnotation(DefineComponent::class)
        if (defineComponent == null) {
            logger?.error(Errors.invalidComponent(toString()), node)
            return false
        }
        return true
    }
}