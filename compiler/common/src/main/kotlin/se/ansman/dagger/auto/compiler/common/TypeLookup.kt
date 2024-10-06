package se.ansman.dagger.auto.compiler.common

import kotlin.reflect.KClass

interface TypeLookup<in I, out T> {
    operator fun get(name: I): T
}

fun <I, T> TypeLookup(lookup: (I) -> T): TypeLookup<I, T> = TypeLookupImpl(lookup)

private class TypeLookupImpl<in I, out T>(private val lookup: (I) -> T) : TypeLookup<I, T> {
    private val cache = mutableMapOf<I, T>()

    override operator fun get(name: I): T = cache.getOrPut(name) {
        lookup(name)
    }
}

operator fun <T> TypeLookup<String, T>.get(klass: KClass<*>): T =
    get(requireNotNull(klass.qualifiedName) {
        "Cannot get type from anonymous or local class $klass"
    })