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
 * @param inComponent Which component to install the binding in. Defaults to being inferred based on the scope.
 * @param asTypes Specifies which supertypes to bind the object as. Required if there are multiple supertypes.
 * @param bindGenericAs Specifies how generic supertypes should be bound. Defaults to [BindGenericAs.Type].
 * @see AutoBind
 * @see AutoBindIntoSet
 * @see BindGenericAs
 * @see MapKey
 * @see IntoMap
 * @since 0.2
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
public annotation class AutoBindIntoMap(
    val inComponent: KClass<*> = Nothing::class,
    val asTypes: Array<KClass<*>> = [],
    val bindGenericAs: BindGenericAs = BindGenericAs.Type,
)
