package se.ansman.dagger.auto.compiler.common

import kotlin.reflect.KClass

class TypeLookup<out T>(private val lookup: (String) -> T) {
    private val cache = mutableMapOf<String, T>()

    operator fun get(name: String): T = cache.getOrPut(name) {
        lookup(name)
    }
}

operator fun <T> TypeLookup<T>.get(klass: KClass<*>): T =
    get(requireNotNull(klass.qualifiedName) {
        "Cannot get type from anonymous or local class $klass"
    })