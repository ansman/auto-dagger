package se.ansman.dagger.auto

import kotlin.reflect.KClass

class TypeLookup<T : Any>(private val lookup: (String) -> T?) {
    private val cache = mutableMapOf<KClass<*>, T>()
    operator fun get(klass: KClass<*>): T = cache.getOrPut(klass) {
        val name = requireNotNull(klass.qualifiedName) {
            "Cannot get type from anonymous or local class $klass"
        }
        requireNotNull(lookup(name)) {
            "Could not find class for name $name"
        }
    }
}