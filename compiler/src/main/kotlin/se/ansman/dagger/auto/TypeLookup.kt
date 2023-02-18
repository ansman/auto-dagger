package se.ansman.dagger.auto

import kotlin.reflect.KClass

interface TypeLookup<T : Any> {
    fun getTypeForClass(klass: KClass<*>): T
}