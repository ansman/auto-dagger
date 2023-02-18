// Code generated by Auto Dagger. Do not edit.
package se.ansman.binding.annotated;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoSet;
import se.ansman.dagger.auto.Initializable;

@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = BindingModule.class
)
public abstract class AutoInitializeBindingModule {
  private AutoInitializeBindingModule() {
  }

  @Provides
  @IntoSet
  public static Initializable bindThingAsInitializable(Lazy<Thing> thing) {
    return Initializable.fromLazy(thing);
  }
}