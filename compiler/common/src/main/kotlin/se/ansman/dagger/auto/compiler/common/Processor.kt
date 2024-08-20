package se.ansman.dagger.auto.compiler.common

import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver

interface Processor<N, TypeName, ClassName : TypeName, AnnotationSpec> {
    val annotations: Set<String>
    val logger: AutoDaggerLogger<N>?
    val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, *>
    fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>)
}