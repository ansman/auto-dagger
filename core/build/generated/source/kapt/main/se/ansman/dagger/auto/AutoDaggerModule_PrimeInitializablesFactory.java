package se.ansman.dagger.auto;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class AutoDaggerModule_PrimeInitializablesFactory implements Factory<Set<Initializable>> {
  @Override
  public Set<Initializable> get() {
    return primeInitializables();
  }

  public static AutoDaggerModule_PrimeInitializablesFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static Set<Initializable> primeInitializables() {
    return Preconditions.checkNotNullFromProvides(AutoDaggerModule.INSTANCE.primeInitializables());
  }

  private static final class InstanceHolder {
    private static final AutoDaggerModule_PrimeInitializablesFactory INSTANCE = new AutoDaggerModule_PrimeInitializablesFactory();
  }
}
