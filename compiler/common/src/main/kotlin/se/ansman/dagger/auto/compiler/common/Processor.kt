package se.ansman.dagger.auto.compiler.common

import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.model.Declaration
import se.ansman.dagger.auto.compiler.common.processing.model.Node
import kotlin.reflect.KClass

interface Processor<N, TypeName, ClassName : TypeName, AnnotationSpec> {
    val annotations: Set<String>
    val logger: AutoDaggerLogger<N>?
    val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, *>
    fun process(
        elements: Map<String, List<Declaration<N, TypeName, ClassName>>>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName>
    )
}

operator fun <N : Node<*, *, *>> Map<String, List<N>>.get(type: KClass<*>): List<N> =
    getValue(type.java.canonicalName)