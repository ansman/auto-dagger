package se.ansman.dagger.auto.processors

import kotlin.reflect.KClass

interface Processor {
    val annotations: Set<KClass<out Annotation>>
    fun process()
}