package se.ansman.dagger.auto;

/**
 * How generic types are bound when using multibinding such as [AutoBindIntoSet] or [AutoBindIntoMap].
 *
 * The default is [ExactType] unless the bound type is annotated with [Default] in which case that is used
 * as the default.
 *
 * @since 1.0.0
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0006B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0007"}, d2 = {"Lse/ansman/dagger/auto/BindGenericAs;", "", "(Ljava/lang/String;I)V", "ExactType", "Wildcard", "ExactTypeAndWildcard", "Default", "core"})
public enum BindGenericAs {
    /*public static final*/ ExactType /* = new ExactType() */,
    /*public static final*/ Wildcard /* = new Wildcard() */,
    /*public static final*/ ExactTypeAndWildcard /* = new ExactTypeAndWildcard() */;
    
    BindGenericAs() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<se.ansman.dagger.auto.BindGenericAs> getEntries() {
        return null;
    }
    
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
     *  fun produce(): T
     *  fun close()
     * }
     *
     * @AutoBindIntoSet // Will bind this as Resource<*> instead of Resource<SomeThing>
     * class SomeResource @Inject constructor() : Resource<SomeThing> {
     *  ...
     *  override fun close() { ... }
     * }
     *
     * class ResourceCloser @Inject constructor(
     *  val resources: Set<@JvmSuppressWildcards Resource<*>>
     * ) {
     *  fun closeAllResources() {
     *    resources.forEach { it.close() }
     *  }
     * }
     * ```
     *
     * Example when using exact type:
     * ```
     * @BindGenericAs.Default(BindGenericAs.Type)
     * interface Interceptor<T> {
     *  fun intercept(value: T): T
     * }
     *
     * @AutoBindIntoSet // Will bind this as Interceptor<SomeThing>
     * class SomeInterceptor @Inject constructor() : Interceptor<SomeThing> {
     *  ...
     * }
     *
     * class SomeOperation @Inject constructor(
     *  val interceptors: Set<@JvmSuppressWildcards Interceptor<SomeThing>>
     * ) {
     *  fun performOperation(value: SomeThing): SomeThing {
     *    return interceptors.fold(value) { v, interceptor -> interceptor.intercept(v) }
     *  }
     * }
     * ```
     *
     * @see AutoBindIntoSet
     * @see AutoBindIntoMap
     * @since 1.0.0
     */
    @kotlin.annotation.MustBeDocumented()
    @kotlin.annotation.Target(allowedTargets = {kotlin.annotation.AnnotationTarget.CLASS})
    @kotlin.annotation.Retention(value = kotlin.annotation.AnnotationRetention.BINARY)
    @java.lang.annotation.Documented()
    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.CLASS)
    @java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\u0002\u0018\u00002\u00020\u0001B\b\u0012\u0006\u0010\u0002\u001a\u00020\u0003R\u000f\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0006\u001a\u0004\b\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lse/ansman/dagger/auto/BindGenericAs$Default;", "", "value", "Lse/ansman/dagger/auto/BindGenericAs;", "()Lse/ansman/dagger/auto/BindGenericAs;", "core"})
    public static abstract @interface Default {
        
        public abstract se.ansman.dagger.auto.BindGenericAs value();
    }
}