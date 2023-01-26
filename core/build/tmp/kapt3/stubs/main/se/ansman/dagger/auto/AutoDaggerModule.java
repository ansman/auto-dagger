package se.ansman.dagger.auto;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\b\u00c1\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0007\u00a8\u0006\u0006"}, d2 = {"Lse/ansman/dagger/auto/AutoDaggerModule;", "", "()V", "primeInitializables", "", "Lse/ansman/dagger/auto/Initializable;", "core"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class AutoDaggerModule {
    @org.jetbrains.annotations.NotNull()
    public static final se.ansman.dagger.auto.AutoDaggerModule INSTANCE = null;
    
    private AutoDaggerModule() {
        super();
    }
    
    @dagger.Provides()
    @dagger.multibindings.ElementsIntoSet()
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<se.ansman.dagger.auto.Initializable> primeInitializables() {
        return null;
    }
}