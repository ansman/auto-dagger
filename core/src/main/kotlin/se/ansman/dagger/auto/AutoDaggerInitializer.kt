package se.ansman.dagger.auto

import javax.inject.Inject

/**
 * A class that can initialize multiple [Initializable] in order of their [priority][AutoInitialize.priority].
 *
 * @since 1.0.0
 */
public class AutoDaggerInitializer @Inject constructor(
    private val initializables: Set<@JvmSuppressWildcards Initializable>
) : Initializable {
    /**
     * If the initializer has been initialized or not (i.e. if [initialize] has been called).
     */
    @Volatile
    public var isInitialized: Boolean = false
        private set

    private var isInitializing: Boolean = false

    /**
     * Initializes the provided [Initializable]. This method is thread safe and calling it multiple
     * times will only initialize the initializables once.
     *
     * If any initializable throws an exception the remaining initializes are still initialized before rethrowing the
     * exception. If multiple initializables throw then the first one is thrown with the other exceptions added as
     * suppressed exceptions.
     *
     * If this method fails, calling it again will complete successfully without attempting to initialize the failed
     * initializables again.
     */
    public override fun initialize() {
        if (isInitialized) {
            return
        }
        synchronized(this) {
            if (isInitialized) return
            check(!isInitializing) {
                "Calls to AutoDaggerInitializable.initialize is from an initializer is not allowed"
            }
            isInitializing = true
            var exception: Exception? = null
            for (initializable in initializables.sortedWith(InitializableComparator)) {
                try {
                    initializable.initialize()
                } catch (e: Exception) {
                    if (exception == null) {
                        exception = e
                    } else {
                        exception.addSuppressed(e)
                    }
                }
            }
            exception?.let { throw it }

            isInitialized = true
            isInitializing = false
        }
    }
}

private val InitializableComparator = Comparator<Initializable> { i1, i2 -> i2.priority.compareTo(i1.priority) }

internal val Initializable.priority: Int
    get() = if (this is OrderedInitializable) priority else AutoInitialize.defaultPriority