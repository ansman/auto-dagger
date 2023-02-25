package se.ansman.dagger.auto

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
public annotation class AutoBindIntoMap(
    val inComponent: KClass<*> = Nothing::class,
    val asTypes: Array<KClass<*>> = [],
)
