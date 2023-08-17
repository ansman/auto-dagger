package se.ansman.dagger.auto.compiler.common

fun <T, E> T.applyEach(iterable: Iterable<E>, block: T.(E) -> Unit): T = apply {
    for (element in iterable) {
        block(element)
    }
}

fun <T, E> T.applyEachIndexed(iterable: Iterable<E>, block: T.(index: Int, E) -> Unit): T = apply {
    iterable.forEachIndexed { i, e -> block(i, e) }
}

fun <T> T.applyIf(predicate: Boolean, block: T.() -> Unit): T = apply {
    if (predicate) {
        block()
    }
}