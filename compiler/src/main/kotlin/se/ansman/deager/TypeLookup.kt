package se.ansman.deager

import kotlin.reflect.KClass

interface TypeLookup<T : Any> {
    fun getTypeForClass(klass: KClass<*>): T
}