package se.ansman.deager

/**
 * Marks the given objects as being eagerly initializable.
 *
 * This will instruct Deager to provide the object as an [Initializable].
 * Use [EagerInitializer] to later initialize the annotated objects.
 *
 * If the object implements the [Initializable] interface then it will be created when
 * [EagerInitializer] is injected and [Initializable.initialize] will be called when
 * [EagerInitializer.initialize] is called.
 *
 * Otherwise, the object is only created when [EagerInitializer.initialize] is called.
 *
 * @property priority The priority of the object which determines initialization order. Objects with a higher priority
 *                    are initialized first. If two initializables have the same priority then they are initialized in
 *                    an undefined order. The default priority is 1.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
)
public annotation class Eager(
    val priority: Int = defaultPriority
) {
    public companion object {
        public const val defaultPriority: Int = 1
    }
}
