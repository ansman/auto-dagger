package se.ansman.dagger.auto.compiler.processing

interface AutoDaggerEnvironment<in N, TypeName, ClassName : TypeName, AnnotationSpec, F> {
    val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>
    fun logError(message: String, node: N)
    fun logWarning(message: String, node: N)
    fun logInfo(message: String, node: N? = null)
    fun write(file: F)
}

fun <N> AutoDaggerEnvironment<N, *, *, *, *>.logError(message: String, node: Node<N, *, *, *>) {
    logError(message, node.node)
}

fun <N> AutoDaggerEnvironment<N, *, *, *, *>.logWarning(message: String, node: Node<N, *, *, *>) {
    logWarning(message, node.node)
}

fun <N> AutoDaggerEnvironment<N, *, *, *, *>.logInfo(message: String, node: Node<N, *, *, *>? = null) {
    logInfo(message, node?.node)
}
