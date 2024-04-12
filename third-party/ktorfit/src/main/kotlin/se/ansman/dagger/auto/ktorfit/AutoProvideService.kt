package se.ansman.dagger.auto.ktorfit

import dagger.hilt.GeneratesRootInput
import dagger.hilt.components.SingletonComponent
import kotlin.reflect.KClass

/**
 * Marks a [Ktorfit](https://foso.github.io/Ktorfit/) Service for automatic contribution to your dependency graph.
 *
 * Simply add this annotation to the API services you want to inject and auto-dagger will generate the necessary modules.
 *
 * Example:
 * ```
 * @AutoProvideService
 * class ApiService {
 *   @GET("users")
 *   suspend fun getUsers(): List<User>
 * }
 *
 * // You'll also need to provide a Ktorfit instance to the component you want to inject the service into:
 * @Module
 * @InstallIn(SingletonComponent::class)
 * object KtorfitModule {
 *   @Provides
 *   @Singleton
 *   fun provideKtorfit(): Ktorfit = Ktorfit.Builder()
 *       .baseUrl("https://api.example.com/")
 *       .build()
 * }
 * ```
 *
 * ## Changing the target component
 * By default, services are installed in the [SingletonComponent]. If you'd like to change this you can do so by
 * specifying the `inComponent` parameter:
 * ```
 * @AutoProvideService(inComponent = SomeOtherComponent::class)
 * class ApiService {}
 * ```
 *
 * ## Qualifiers
 * If you have multiple ktorfit instances and use qualifiers to differentiate them, you can specify the qualifier on the
 * service:
 * ```
 * @AutoProvideService
 * @Named("api1")
 * class ApiService {}
 * ```
 *
 * ## Making the service scoped or reusable
 * If you'd like to cache/reuse the service instances you can annotate the service with a scope or `@Reusable`:
 * ```
 * @Singleton // This must match the `inComponent` parameter of @AutoProvideService
 * @AutoProvideService
 * class ApiService {
 *     // Service methods
 * }
 *
 * // Or if you want to just make it reusable
 * @Reusable
 * @AutoProvideService
 * class ApiService {
 *     // Service methods
 * }
 * ```
 *
 * @param inComponent The component to install the service in. Defaults to [SingletonComponent].
 * @since 1.1.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@GeneratesRootInput
public annotation class AutoProvideService(
    val inComponent: KClass<*> = SingletonComponent::class,
)
