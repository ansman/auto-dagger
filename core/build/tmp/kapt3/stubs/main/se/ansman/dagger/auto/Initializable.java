package se.ansman.dagger.auto;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bf\u0018\u0000 \u00042\u00020\u0001:\u0001\u0004J\b\u0010\u0002\u001a\u00020\u0003H&\u00a8\u0006\u0005"}, d2 = {"Lse/ansman/dagger/auto/Initializable;", "", "initialize", "", "Companion", "core"})
public abstract interface Initializable {
    @org.jetbrains.annotations.NotNull()
    public static final se.ansman.dagger.auto.Initializable.Companion Companion = null;
    
    /**
     * Initializes the initializable.
     */
    public abstract void initialize();
    
    @kotlin.jvm.JvmStatic()
    @kotlin.jvm.JvmName(name = "fromLazy")
    @kotlin.jvm.JvmOverloads()
    @org.jetbrains.annotations.NotNull()
    public static se.ansman.dagger.auto.Initializable fromLazy(@org.jetbrains.annotations.NotNull()
    dagger.Lazy<?> $this$fromLazy) {
        return null;
    }
    
    @kotlin.jvm.JvmStatic()
    @kotlin.jvm.JvmName(name = "fromLazy")
    @kotlin.jvm.JvmOverloads()
    @org.jetbrains.annotations.NotNull()
    public static se.ansman.dagger.auto.Initializable fromLazy(@org.jetbrains.annotations.NotNull()
    dagger.Lazy<?> $this$fromLazy, int priority) {
        return null;
    }
    
    @kotlin.jvm.JvmStatic()
    @org.jetbrains.annotations.NotNull()
    public static se.ansman.dagger.auto.Initializable withPriority(@org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.Initializable $this$withPriority, int priority) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001f\u0010\u0003\u001a\u00020\u0004*\u0006\u0012\u0002\b\u00030\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0007\u00a2\u0006\u0002\b\bJ\u0014\u0010\t\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007H\u0007\u00a8\u0006\n"}, d2 = {"Lse/ansman/dagger/auto/Initializable$Companion;", "", "()V", "asInitializable", "Lse/ansman/dagger/auto/Initializable;", "Ldagger/Lazy;", "priority", "", "fromLazy", "withPriority", "core"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Converts the provided [Lazy] into an [Initializable] with the given [priority] (see [AutoInitialize.priority]).
         *
         * When the returned [Initializable] is initialized, the lazy value is computed.
         *
         * @since 1.0.0
         */
        @kotlin.jvm.JvmStatic()
        @kotlin.jvm.JvmName(name = "fromLazy")
        @kotlin.jvm.JvmOverloads()
        @org.jetbrains.annotations.NotNull()
        public final se.ansman.dagger.auto.Initializable fromLazy(@org.jetbrains.annotations.NotNull()
        dagger.Lazy<?> $this$asInitializable, int priority) {
            return null;
        }
        
        /**
         * Returns a new [Initializable] with the given [priority].
         *
         * @since 1.0.0
         */
        @kotlin.jvm.JvmStatic()
        @org.jetbrains.annotations.NotNull()
        public final se.ansman.dagger.auto.Initializable withPriority(@org.jetbrains.annotations.NotNull()
        se.ansman.dagger.auto.Initializable $this$withPriority, int priority) {
            return null;
        }
        
        @kotlin.jvm.JvmStatic()
        @kotlin.jvm.JvmName(name = "fromLazy")
        @kotlin.jvm.JvmOverloads()
        @org.jetbrains.annotations.NotNull()
        public final se.ansman.dagger.auto.Initializable fromLazy(@org.jetbrains.annotations.NotNull()
        dagger.Lazy<?> $this$asInitializable) {
            return null;
        }
    }
}