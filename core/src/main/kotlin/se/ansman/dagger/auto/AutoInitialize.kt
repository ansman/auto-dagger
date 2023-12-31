package se.ansman.dagger.auto

import dagger.hilt.GeneratesRootInput

/**
 * Marks the given objects as being initializable.
 *
 * This will instruct Auto Dagger to provide the object as an [Initializable] which allows you to use
 * [AutoDaggerInitializer] to later initialize the annotated objects.
 *
 * If the object implements the [Initializable] interface then it will be created when
 * [AutoDaggerInitializer] is injected and [Initializable.initialize] will be called when
 * [AutoDaggerInitializer.initialize] is called.
 *
 * Otherwise, the object is only created when [AutoDaggerInitializer.initialize] is called.
 *
 * @property priority The priority of the object which determines initialization order. Objects with a higher priority
 *                    are initialized first. If two initializables have the same priority then they are initialized in
 *                    an undefined order. The default priority is 1.
 * @since 1.0.0
 */
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
)
@GeneratesRootInput
public annotation class AutoInitialize(
    val priority: Int = defaultPriority
) {
    public companion object {
        public const val defaultPriority: Int = 1
    }
}
