package se.ansman.dagger.auto.compiler.common

import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import kotlin.reflect.KClass

interface Processor<N, TypeName, ClassName : TypeName, AnnotationSpec> {
    val annotations: Set<KClass<out Annotation>>
    fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>)
}