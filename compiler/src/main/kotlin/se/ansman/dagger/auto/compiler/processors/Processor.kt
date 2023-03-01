package se.ansman.dagger.auto.compiler.processors

import se.ansman.dagger.auto.compiler.processing.AutoDaggerResolver
import kotlin.reflect.KClass

interface Processor<N, TypeName, ClassName : TypeName, AnnotationSpec> {
    val annotations: Set<KClass<out Annotation>>
    fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>)
}