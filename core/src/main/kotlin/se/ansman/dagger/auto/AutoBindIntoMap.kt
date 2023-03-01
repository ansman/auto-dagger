package se.ansman.dagger.auto

import dagger.MapKey
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import kotlin.reflect.KClass

/**
 * A version of [AutoBind] that binds the object using [IntoMap].
 * The object must also be annotated with a [MapKey] annotated annotation such as [StringKey], [MapKey], or
 * a custom annotation.
 *
 * For more documentation on auto bind see [AutoBind].
 *
 * See also [Map Multibindings][https://dagger.dev/dev-guide/multibindings.html#map-multibindings]
 *
 * @see [AutoBind]
 * @see [AutoBindIntoSet]
 * @see [MapKey]
 * @see [IntoMap]
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
public annotation class AutoBindIntoMap(
    val inComponent: KClass<*> = Nothing::class,
    val asTypes: Array<KClass<*>> = [],
)
