package se.ansman.dagger.auto.compiler.common

import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver

interface Processor<N, TypeName, ClassName : TypeName, AnnotationSpec> {
    val annotations: Set<String>
    fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>)
}