package se.ansman.dagger.auto

import dagger.Lazy

public interface Initializable {
    /** Initializes the initializable. */
    public fun initialize()

    public companion object {

        /**
         * Converts the provided [Lazy] into an [Initializable] with the given [priority] (see [AutoInitialize.priority]).
         *
         * When the returned [Initializable] is initialized, the lazy value is computed.
         */
        @JvmStatic
        @JvmName("fromLazy")
        @JvmOverloads
        public fun Lazy<*>.asInitializable(priority: Int = AutoInitialize.defaultPriority): Initializable =
            OrderedInitializable(priority = priority) {
                // As a fallback if the lazy value is also initializable.
                (get() as? Initializable)?.initialize()
            }
    }
}