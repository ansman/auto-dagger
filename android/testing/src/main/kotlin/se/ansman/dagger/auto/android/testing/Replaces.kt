package se.ansman.dagger.auto.android.testing

import dagger.hilt.GeneratesRootInput
import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.AutoBindIntoMap
import se.ansman.dagger.auto.AutoBindIntoSet
import se.ansman.dagger.auto.AutoInitialize
import kotlin.reflect.KClass

/**
 * Denotes that the class referenced using [type] is to be replaced by this type.
 *
 * The type to replace must be annotated with [AutoBind].
 * Any qualifiers and binding keys will be copied from the referenced type.
 *
 * The annotated class must implement the types which are bound by the referenced type.
 *
 * ## Multibindings
 * If the replaced type uses multibindings ([AutoBindIntoSet] or [AutoBindIntoMap]) then those are only replaced if
 * the annotated type also implements them. Otherwise the multibinding is removed
 *
 * So for example. If the target type uses `@AutoBindIntoSet` to bind it as a `Closeable` but your replacement doesn't
 * implement `Closeable` then that binding is removed. If it does implement `Closeable` then the binding is replaced with
 * the fake binding.
 *
 * ## Objects
 * Normally the replacement object needs to be provided to the dependency graph using either an `@Provides` annotated
 * method or using an `@Inject` annotated constructor.
 *
 * Auto Dagger allows you to annotate a Kotlin object with `@Replaces` without it being provided in the graph.
 * This is especially useful for tests:
 * ```
 * @Replaces(ThreadPoolExecutor::class)
 * object DirectExecutor : Executor {
 *   override fun execute(command: Runnable) {
 *     command.run()
 *   }
 * }
 * ```
 *
 * ## Auto Initialize
 * If the target type is annotated with [AutoInitialize], then an empty module will be generated to replace the auto
 * initialize module, effectively disabling it.
 *
 * ## Example
 * ```kotlin
 * // In your `main` source set
 * interface Repository
 *
 * @AutoBind(asTypes = [Repository::class])
 * @AutoBindIntoSet(asTypes = [Closeable::class])
 * @Singleton
 * class RealRepository @Inject constructor() : Repository, Closeable {
 *   override fun close() {}
 * }
 *
 * // In your `test` source set
 * // Since `FakeRepository` doesn't implement `Closeable` then it's
 * // not bound as `Closable` and the real `RealRepository -> Closeable`
 * // binding is removed.
 * @Replaces(RealRepository::class)
 * class FakeRepository @Inject constructor() : Repository
 * ```
 *
 * @since 0.3
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
@GeneratesRootInput
public annotation class Replaces(
    val type: KClass<*>,
)
