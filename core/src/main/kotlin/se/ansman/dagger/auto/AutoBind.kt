package se.ansman.dagger.auto

import dagger.hilt.GeneratesRootInput
import javax.inject.Qualifier
import kotlin.reflect.KClass

/**
 * Instructs Auto Dagger to automatically bind the annotated type as its parent type.
 *
 * To use this annotation the class must:
 * - Not be generic - To bind generic types implement your own binding that provides the type arguments.
 * - Have exactly one direct supertype - If you have multiple supertypes then specify which to bind using [asTypes].
 *
 * Only direct supertypes can be bound. For example, if your class implements `Closeable` you cannot bind it as
 * `AutoCloseable` (which is a supertype of `Closeable`).
 * To work around this, add the bound type as an explicit supertype.
 *
 * ## Example
 * ```
 * interface Repository
 *
 * @AutoBind
 * @Singleton
 * class RealRepository @Inject constructor() : Repository
 *
 * // Generates the equivalent to:
 * @Binds
 * abstract fun RealRepository.bindRealRepositoryAsRepository(): Repository
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
 * the [inComponent] parameter to explicit provide the component.
 *
 * ## Multiple supertypes
 * If you have multiple supertypes use the [asTypes] parameter to specify which of them to bind. You only need to
 * specify the raw class name if the type is generic (i.e. if your type implements `Callable<Stuff>` you pass
 * `asTypes = [Callable::class]` to indicate you want to bind it).
 *
 * ## Objects
 * Normally the bound object needs to be provided to the dependency graph using either an `@Provides` annotated method
 * or using an `@Inject` annotated constructor.
 *
 * Auto Dagger allows you to annotate a Kotlin object with `@AutoBind` without it being provided in the graph.
 * This is especially useful for tests:
 * ```
 * @AutoBind
 * object DirectExecutor : Executor {
 *   override fun execute(command: Runnable) {
 *     command.run()
 *   }
 * }
 * ```
 *
 * ## Multibinding
 * To bind the object using multibinding, use [AutoBindIntoSet] and/or [AutoBindIntoMap].
 * These can be used at the same time as [AutoBind].
 *
 * ## Qualifiers
 * Any [Qualifier]s on the annotated type will be carried over to the binding:
 * ```
 * @Named("Prod")
 * @AutoBind
 * class ProdAuthenticator @Inject constructor() : Authenticator
 *
 * // Generates the equivalent to:
 * @Binds @Named("Prod")
 * abstract fun ProdAuthenticator.bindProdAuthenticatorAsAuthenticator(): Authenticator
 * ```
 *
 * @param inComponent Which component to install the binding in. Defaults to being inferred based on the scope.
 * @param asTypes Specifies which supertypes to bind the object as. Required if there are multiple supertypes.
 *
 * @see AutoBindIntoSet
 * @see AutoBindIntoMap
 * @since 1.0.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@GeneratesRootInput
public annotation class AutoBind(
    val inComponent: KClass<*> = Nothing::class,
    val asTypes: Array<KClass<*>> = [],
)
