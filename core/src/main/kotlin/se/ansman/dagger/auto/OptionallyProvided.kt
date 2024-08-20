package se.ansman.dagger.auto

import dagger.BindsOptionalOf
import dagger.hilt.GeneratesRootInput
import javax.inject.Qualifier
import kotlin.reflect.KClass

/**
 * Denotes that the annotated type might not be provided to the dependency graph.
 *
 * This will generate a [`@BindsOptionalOf`][BindsOptionalOf] binding for the annotated type.
 *
 * This is useful when the type will only be provided under certain circumstances. For example
 * an Android app might have some debug settings that are only available in debuggable builds.
 *
 * ## Example
 * ```
 * @OptionallyProvided
 * interface DebugApi
 *
 * // This generates
 * @BindsOptionalOf
 * abstract fun bindsOptionalDebugApi(): DebugApi
 *
 * // Then in your implementation module
 * @AutoBind
 * @Singleton
 * class RealDebugApi @Inject constructor() : DebugApi
 * ```
 *
 * ## Component
 * Auto Dagger tries to infer the component based on the scope of the object using the following mapping:
 *
 * | Scope                    | Component                   |
 * |--------------------------|-----------------------------|
 * | No scope                 | `SingletonComponent`        |
 * | `Singleton`              | `SingletonComponent`        |
 * | `Reusable`               | `SingletonComponent`        |
 * | `ActivityRetainedScoped` | `ActivityRetainedComponent` |
 * | `ActivityScoped`         | `ActivityComponent`         |
 * | `FragmentScoped`         | `FragmentComponent`         |
 * | `ServiceScoped`          | `ServiceComponent`          |
 * | `ViewScoped`             | `ViewComponent`             |
 * | `ViewModelScoped`        | `ViewModelComponent`        |
 * | `ViewWithFragmentScoped` | `ViewWithFragmentComponent` |
 *
 * If you want to install the binding in a different component or if you're using a custom scope, then you can use
 * the [inComponent] parameter to explicit provide the component:
 * ```
 * @OptionallyProvided(inComponent = SomeComponent::class)
 * interface DebugApi
 * ```
 *
 * ## Qualifiers
 * Any [Qualifier]s on the annotated type will be carried over to the binding:
 * ```
 * @Named("Prod")
 * @OptionallyProvided
 * interface Authenticator
 *
 * // Generates the equivalent to:
 * @BindsOptionalOf
 * @Named("Prod")
 * abstract fun bindsOptionalAuthenticator(): Authenticator
 * ```
 *
 * @param inComponent Which component to install the binding in. Defaults to being inferred based on the scope.
 *
 * @see AutoBind
 * @since 1.3.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@GeneratesRootInput
public annotation class OptionallyProvided(
    val inComponent: KClass<*> = Nothing::class,
)
