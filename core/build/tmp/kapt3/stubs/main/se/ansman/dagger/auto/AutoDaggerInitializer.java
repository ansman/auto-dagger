package se.ansman.dagger.auto;

/**
 * A class that can initialize multiple [Initializable] in order of their [priority][AutoInitialize.priority].
 *
 * @since 1.0.0
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u001a\b\u0007\u0012\u0011\u0010\u0002\u001a\r\u0012\t\u0012\u00070\u0001\u00a2\u0006\u0002\b\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\b\u0010\u000b\u001a\u00020\fH\u0016R\u0019\u0010\u0002\u001a\r\u0012\t\u0012\u00070\u0001\u00a2\u0006\u0002\b\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u0007@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lse/ansman/dagger/auto/AutoDaggerInitializer;", "Lse/ansman/dagger/auto/Initializable;", "initializables", "", "Lkotlin/jvm/JvmSuppressWildcards;", "(Ljava/util/Set;)V", "<set-?>", "", "isInitialized", "()Z", "isInitializing", "initialize", "", "core"})
public final class AutoDaggerInitializer implements se.ansman.dagger.auto.Initializable {
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<se.ansman.dagger.auto.Initializable> initializables = null;
    
    /**
     * If the initializer has been initialized or not (i.e. if [initialize] has been called).
     */
    @kotlin.jvm.Volatile()
    private volatile boolean isInitialized = false;
    private boolean isInitializing = false;
    
    @javax.inject.Inject()
    public AutoDaggerInitializer(@org.jetbrains.annotations.NotNull()
    java.util.Set<se.ansman.dagger.auto.Initializable> initializables) {
        super();
    }
    
    /**
     * If the initializer has been initialized or not (i.e. if [initialize] has been called).
     */
    public final boolean isInitialized() {
        return false;
    }
    
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
    @java.lang.Override()
    public void initialize() {
    }
}