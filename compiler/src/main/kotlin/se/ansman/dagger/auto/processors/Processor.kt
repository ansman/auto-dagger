package se.ansman.dagger.auto.processors

import se.ansman.dagger.auto.processing.AutoDaggerResolver
import kotlin.reflect.KClass

interface Processor<N, TypeName, ClassName : TypeName, AnnotationSpec> {
    val annotations: Set<KClass<out Annotation>>
    fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>)
}