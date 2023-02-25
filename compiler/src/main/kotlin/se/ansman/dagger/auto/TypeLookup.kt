package se.ansman.dagger.auto

import kotlin.reflect.KClass

class TypeLookup<out T : Any>(private val lookup: (String) -> T?) {
    private val cache = mutableMapOf<String, T>()

    operator fun get(name: String): T = cache.getOrPut(name) {
        requireNotNull(lookup(name)) {
            "Could not find class for name $name"
        }
    }
}

operator fun <T : Any> TypeLookup<T>.get(klass: KClass<*>): T =
    get(requireNotNull(klass.qualifiedName) {
        "Cannot get type from anonymous or local class $klass"
    })