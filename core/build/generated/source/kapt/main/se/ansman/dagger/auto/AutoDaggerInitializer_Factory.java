package se.ansman.dagger.auto;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.Set;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class AutoDaggerInitializer_Factory implements Factory<AutoDaggerInitializer> {
  private final Provider<Set<Initializable>> initializablesProvider;

  public AutoDaggerInitializer_Factory(Provider<Set<Initializable>> initializablesProvider) {
    this.initializablesProvider = initializablesProvider;
  }

  @Override
  public AutoDaggerInitializer get() {
    return newInstance(initializablesProvider.get());
  }

  public static AutoDaggerInitializer_Factory create(
      Provider<Set<Initializable>> initializablesProvider) {
    return new AutoDaggerInitializer_Factory(initializablesProvider);
  }

  public static AutoDaggerInitializer newInstance(Set<Initializable> initializables) {
    return new AutoDaggerInitializer(initializables);
  }
}
