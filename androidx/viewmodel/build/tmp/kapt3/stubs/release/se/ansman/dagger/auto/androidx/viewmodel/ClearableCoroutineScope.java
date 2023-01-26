package se.ansman.dagger.auto.androidx.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005J\b\u0010\b\u001a\u00020\tH\u0016R\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\n"}, d2 = {"Lse/ansman/dagger/auto/androidx/viewmodel/ClearableCoroutineScope;", "Lkotlinx/coroutines/CoroutineScope;", "Ldagger/hilt/android/lifecycle/RetainedLifecycle$OnClearedListener;", "coroutineContext", "Lkotlin/coroutines/CoroutineContext;", "(Lkotlin/coroutines/CoroutineContext;)V", "getCoroutineContext", "()Lkotlin/coroutines/CoroutineContext;", "onCleared", "", "androidx-viewmodel_release"})
final class ClearableCoroutineScope implements kotlinx.coroutines.CoroutineScope, dagger.hilt.android.lifecycle.RetainedLifecycle.OnClearedListener {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.coroutines.CoroutineContext coroutineContext = null;
    
    public ClearableCoroutineScope(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.CoroutineContext coroutineContext) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlin.coroutines.CoroutineContext getCoroutineContext() {
        return null;
    }
    
    @java.lang.Override()
    public void onCleared() {
    }
}