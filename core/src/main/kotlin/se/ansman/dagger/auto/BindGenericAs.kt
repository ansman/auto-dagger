package se.ansman.dagger.auto

/**
 * How generic types are bound when using multibinding such as [AutoBindIntoSet] or [AutoBindIntoMap].
 *
 * The default is [ExactType] unless the bound type is annotated with [Default] in which case that is used
 * as the default.
 *
 * @since 0.6.0
 */
public enum class BindGenericAs {
    /**
     * Only the exact supertype is bound. For example, if the type is `List<String>` then only `List<String>` is bound.
     *
     * This is the default unless the target type is annotated with [Default].
     */
    ExactType,

    /**
     * The type is bound as a wildcard type. For example, if the type is `List<String>` then it's bound as `List<*>`.
     */
    Wildcard,

    /**
     * The type is bound as the exact supertype and as a wildcard type. For example, if the type is `List<String>` then
     * it's bound as both `List<String>` and `List<*>`.
     */
    ExactTypeAndWildcard;

    /**
     * Specifies the default [AutoBindIntoSet.bindGenericAs] and [AutoBindIntoMap.bindGenericAs] for the annotated type.
     *
     * This is useful if you know that the generic is always unique or if it's always shared.
     * For example, an `Interceptor<T>` should probably be bound as [ExactType] because you want to get all
     * interceptors for a given type where as `Resource<T>` should probably be bound as [Wildcard] since
     * you're likely interested in getting all resources because there are no duplicates.
     *
     * You can only add this to generic types.
     *
     * Example when using wildcard:
     * ```
     * @BindGenericAs.Default(BindGenericAs.Wildcard)
     * interface Resource<T> {
     *   fun produce(): T
     *   fun close()
     * }
     *
     * @AutoBindIntoSet // Will bind this as Resource<*> instead of Resource<SomeThing>
     * class SomeResource @Inject constructor() : Resource<SomeThing> {
     *   ...
     *   override fun close() { ... }
     * }
     *
     * class ResourceCloser @Inject constructor(
     *   val resources: Set<@JvmSuppressWildcards Resource<*>>
     * ) {
     *   fun closeAllResources() {
     *     resources.forEach { it.close() }
     *   }
     * }
     * ```
     *
     * Example when using exact type:
     * ```
     * @BindGenericAs.Default(BindGenericAs.Type)
     * interface Interceptor<T> {
     *   fun intercept(value: T): T
     * }
     *
     * @AutoBindIntoSet // Will bind this as Interceptor<SomeThing>
     * class SomeInterceptor @Inject constructor() : Interceptor<SomeThing> {
     *   ...
     * }
     *
     * class SomeOperation @Inject constructor(
     *   val interceptors: Set<@JvmSuppressWildcards Interceptor<SomeThing>>
     * ) {
     *   fun performOperation(value: SomeThing): SomeThing {
     *     return interceptors.fold(value) { v, interceptor -> interceptor.intercept(v) }
     *   }
     * }
     * ```
     *
     * @see AutoBindIntoSet
     * @see AutoBindIntoMap
     * @since 0.10.0
     */
    @MustBeDocumented
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    public annotation class Default(val value: BindGenericAs)
}