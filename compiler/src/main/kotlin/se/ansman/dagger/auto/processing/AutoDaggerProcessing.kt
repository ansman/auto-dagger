package se.ansman.dagger.auto.processing

import kotlin.reflect.KClass

interface AutoDaggerProcessing<N, TypeName, ClassName : TypeName, AnnotationSpec, F> {
    val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>
    fun nodesAnnotatedWith(annotation: KClass<out Annotation>): Sequence<Node<N, TypeName, ClassName, AnnotationSpec>>
    fun logError(message: String, node: N)
    fun write(file: F)
}

fun <N> AutoDaggerProcessing<N, *, *, *, *>.logError(message: String, node: Node<N, *, *, *>) {
    logError(message, node.node)
}
