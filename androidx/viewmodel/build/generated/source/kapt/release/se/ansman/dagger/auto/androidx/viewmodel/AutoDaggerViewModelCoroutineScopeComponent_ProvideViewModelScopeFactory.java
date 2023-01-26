package se.ansman.dagger.auto.androidx.viewmodel;

import dagger.hilt.android.ViewModelLifecycle;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

@ScopeMetadata
@QualifierMetadata("se.ansman.dagger.auto.androidx.viewmodel.ViewModelSpecific")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AutoDaggerViewModelCoroutineScopeComponent_ProvideViewModelScopeFactory implements Factory<CoroutineScope> {
  private final Provider<ViewModelLifecycle> lifecycleProvider;

  public AutoDaggerViewModelCoroutineScopeComponent_ProvideViewModelScopeFactory(
      Provider<ViewModelLifecycle> lifecycleProvider) {
    this.lifecycleProvider = lifecycleProvider;
  }

  @Override
  public CoroutineScope get() {
    return provideViewModelScope(lifecycleProvider.get());
  }

  public static AutoDaggerViewModelCoroutineScopeComponent_ProvideViewModelScopeFactory create(
      Provider<ViewModelLifecycle> lifecycleProvider) {
    return new AutoDaggerViewModelCoroutineScopeComponent_ProvideViewModelScopeFactory(lifecycleProvider);
  }

  public static CoroutineScope provideViewModelScope(ViewModelLifecycle lifecycle) {
    return Preconditions.checkNotNullFromProvides(AutoDaggerViewModelCoroutineScopeComponent.INSTANCE.provideViewModelScope(lifecycle));
  }
}
