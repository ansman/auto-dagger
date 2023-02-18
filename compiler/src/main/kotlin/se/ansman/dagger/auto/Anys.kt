package se.ansman.dagger.auto

fun <T, E> T.applyEach(iterable: Iterable<E>, block: T.(E) -> Unit): T = apply {
    for (element in iterable) {
        block(element)
    }
}

fun <T> T.applyIf(predicate: Boolean, block: T.() -> Unit): T = apply {
    if (predicate) {
        block()
    }
}