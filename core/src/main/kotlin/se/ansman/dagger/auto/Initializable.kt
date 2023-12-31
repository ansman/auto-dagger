package se.ansman.dagger.auto

import dagger.Lazy

/**
 * An interface used to indicate that initialization isn't done when the object is created but rather
 * when calling [initialize].
 *
 * This is useful if you want to be able to test your object.
 *
 * Although you can use this interface by itself, it's meant to be used in conjunction with [AutoInitialize].
 *
 * @see AutoInitialize
 * @since 1.0.0
 */
public interface Initializable {
    /** Initializes the initializable. */
    public fun initialize()

    public companion object {

        /**
         * Converts the provided [Lazy] into an [Initializable] with the given [priority] (see [AutoInitialize.priority]).
         *
         * When the returned [Initializable] is initialized, the lazy value is computed.
         *
         * @since 1.0.0
         */
        @JvmStatic
        @JvmName("fromLazy")
        @JvmOverloads
        public fun Lazy<*>.asInitializable(priority: Int = AutoInitialize.defaultPriority): Initializable =
            OrderedInitializable(priority = priority) {
                // As a fallback if the lazy value is also initializable.
                (get() as? Initializable)?.initialize()
            }

        /**
         * Returns a new [Initializable] with the given [priority].
         *
         * @since 1.0.0
         */
        @JvmStatic
        public fun Initializable.withPriority(priority: Int): Initializable =
            OrderedInitializable(
                priority = priority,
                initializable = (this as? OrderedInitializable)?.initializable ?: this
            )
    }
}