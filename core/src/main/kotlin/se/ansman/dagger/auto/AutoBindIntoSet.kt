package se.ansman.dagger.auto

import dagger.hilt.GeneratesRootInput
import dagger.multibindings.IntoSet
import kotlin.reflect.KClass

/**
 * A version of [AutoBind] that binds the object using [IntoSet].
 *
 * For more documentation on auto bind see [AutoBind].
 *
 * See also [Set Multibindings](https://dagger.dev/dev-guide/multibindings.html#set-multibindings)
 *
 * @param inComponent Which component to install the binding in. Defaults to being inferred based on the scope.
 * @param asTypes Specifies which supertypes to bind the object as. Required if there are multiple supertypes.
 * @param bindGenericAs Specifies how generic supertypes should be bound. Defaults to [BindGenericAs.ExactType] or if the
 *   target type is annotated with [BindGenericAs.Default].
 * @see AutoBind
 * @see AutoBindIntoSet
 * @see IntoSet
 * @see BindGenericAs
 * @see BindGenericAs.Default
 * @since 1.0.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@GeneratesRootInput
public annotation class AutoBindIntoSet(
    val inComponent: KClass<*> = Nothing::class,
    val asTypes: Array<KClass<*>> = [],
    val bindGenericAs: BindGenericAs = BindGenericAs.ExactType,
)
