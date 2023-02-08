// Code generated by Deager. Do not edit.
package se.ansman.binding

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.codegen.OriginatingElement
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import se.ansman.deager.Initializable
import se.ansman.deager.Initializable.Companion.asInitializable

@Module
@InstallIn(SingletonComponent::class)
@OriginatingElement(topLevelClass = BindingModule::class)
public object EagerBindingModule {
  @Provides
  @IntoSet
  public fun bindThingAsInitializable(thing: Lazy<Thing>): Initializable = thing.asInitializable()
}