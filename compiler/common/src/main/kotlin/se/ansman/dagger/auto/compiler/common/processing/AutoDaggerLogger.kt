package se.ansman.dagger.auto.compiler.common.processing

interface AutoDaggerLogger<in N> {
    fun withTag(tag: String): AutoDaggerLogger<N>

    fun error(message: String, node: N)
    fun warning(message: String, node: N)
    fun info(message: String, node: N? = null)
}

fun <N> AutoDaggerLogger<N>.error(message: String, node: Node<N, *, *, *>) = error(message, node.node)
fun <N> AutoDaggerLogger<N>.warning(message: String, node: Node<N, *, *, *>) = warning(message, node.node)
fun <N> AutoDaggerLogger<N>.info(message: String, node: Node<N, *, *, *>? = null) = info(message, node?.node)
