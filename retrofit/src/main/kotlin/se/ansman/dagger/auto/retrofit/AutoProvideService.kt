package se.ansman.dagger.auto.retrofit

import dagger.hilt.GeneratesRootInput
import dagger.hilt.components.SingletonComponent
import kotlin.reflect.KClass

/**
 * Marks a [Retrofit](https://square.github.io/retrofit/) Service for automatic contribution to your dependency graph.
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
 * // You'll also need to provide a Retrofit instance to the component you want to inject the service into:
 * @Module
 * @InstallIn(SingletonComponent::class)
 * object RetrofitModule {
 *   @Provides
 *   @Singleton
 *   fun provideRetrofit(): Retrofit = Retrofit.Builder()
 *       .baseUrl("https://api.example.com/")
 *       .addConverterFactory(SomeConverterFactory.create())
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
 * If you have multiple retrofit instances and use qualifiers to differentiate them, you can specify the qualifier on the
 * service:
 * ```
 * @AutoProvideService
 * @Named("api1")
 * class ApiService {}
 * ```
 *
 * ## Making the service scoped or reusable
 * By default, provided service is unscoped and will be created every time it is injected. Retrofit caches the parsing of
 * services so this is not an expensive operation, but it injected frequently you can avoid this by annotating your service
 * with a scope or `@Reusable`:
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
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@GeneratesRootInput
public annotation class AutoProvideService(
    val inComponent: KClass<*> = SingletonComponent::class,
)
