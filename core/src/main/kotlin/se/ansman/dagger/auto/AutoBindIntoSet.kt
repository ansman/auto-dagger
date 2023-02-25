package se.ansman.dagger.auto

import dagger.multibindings.IntoSet
import kotlin.reflect.KClass

/**
 * A version of [AutoBind] that binds the object using [IntoSet].
 *
 * For more documentation on auto bind see [AutoBind].
 *
 * See also [Set Multibindings](https://dagger.dev/dev-guide/multibindings.html#set-multibindings)
 *
 * @see [AutoBind]
 * @see [AutoBindIntoSet]
 * @see [IntoSet]
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
public annotation class AutoBindIntoSet(
    val inComponent: KClass<*> = Nothing::class,
    val asTypes: Array<KClass<*>> = [],
)
