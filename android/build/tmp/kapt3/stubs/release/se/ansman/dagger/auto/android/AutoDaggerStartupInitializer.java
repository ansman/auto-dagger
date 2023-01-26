package se.ansman.dagger.auto.android;

/**
 * An [Initializer] that will initialize a [Singleton] scoped [AutoDaggerStartupInitializer].
 *
 * This will be called by androidx.startup, provides no result and has no dependencies.
 *
 * @since 1.0.0
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u001a\u0010\u0007\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u00010\t0\bH\u0016\u00a8\u0006\n"}, d2 = {"Lse/ansman/dagger/auto/android/AutoDaggerStartupInitializer;", "Landroidx/startup/Initializer;", "", "()V", "create", "context", "Landroid/content/Context;", "dependencies", "", "Ljava/lang/Class;", "android_release"})
public final class AutoDaggerStartupInitializer implements androidx.startup.Initializer<kotlin.Unit> {
    
    public AutoDaggerStartupInitializer() {
        super();
    }
    
    @java.lang.Override()
    public void create(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.List<java.lang.Class<? extends androidx.startup.Initializer<?>>> dependencies() {
        return null;
    }
}