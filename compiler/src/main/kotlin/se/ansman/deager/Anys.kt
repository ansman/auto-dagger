package se.ansman.deager

fun <T, E> T.applyEach(iterable: Iterable<E>, block: T.(E) -> Unit): T = apply {
    for (element in iterable) {
        block(element)
    }
}