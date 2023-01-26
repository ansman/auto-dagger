package se.ansman.dagger.auto.androidx.viewmodel;

/**
 * A Dagger module which provides a [CoroutineScope] that is tied to the lifecycle of a [ViewModel].
 *
 * The provided scope is qualified with [ViewModelSpecific] and will be canceled when the viewmodel is cleared and it
 * ses a [SupervisorJob] and the `[Dispatchers].Main.immediate` dispatcher.
 *
 * To inject:
 * ```
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *  @ViewModelSpecific
 *  private val viewModelScope: CoroutineScope
 * ) : ViewModel()
 * ```
 *
 * @since 1.0.0
 */
@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00a8\u0006\u0007"}, d2 = {"Lse/ansman/dagger/auto/androidx/viewmodel/AutoDaggerViewModelCoroutineScopeComponent;", "", "()V", "provideViewModelScope", "Lkotlinx/coroutines/CoroutineScope;", "lifecycle", "Ldagger/hilt/android/ViewModelLifecycle;", "androidx-viewmodel_release"})
@dagger.hilt.InstallIn(value = {dagger.hilt.android.components.ViewModelComponent.class})
public final class AutoDaggerViewModelCoroutineScopeComponent {
    @org.jetbrains.annotations.NotNull()
    public static final se.ansman.dagger.auto.androidx.viewmodel.AutoDaggerViewModelCoroutineScopeComponent INSTANCE = null;
    
    private AutoDaggerViewModelCoroutineScopeComponent() {
        super();
    }
    
    @dagger.Provides()
    @ViewModelSpecific()
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.CoroutineScope provideViewModelScope(@org.jetbrains.annotations.NotNull()
    dagger.hilt.android.ViewModelLifecycle lifecycle) {
        return null;
    }
}