// Code generated by Deager. Do not edit.
package se.ansman.provider.qualified

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.codegen.OriginatingElement
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Named
import se.ansman.deager.Initializable
import se.ansman.deager.Initializable.Companion.asInitializable

@Module
@InstallIn(SingletonComponent::class)
@OriginatingElement(topLevelClass = QualifiedProviderModule::class)
public object EagerQualifiedProviderModule {
  @Provides
  @IntoSet
  public fun getThingAsInitializable(@Named(`value` = "someName")
      qualifiedProvidedThing: Lazy<QualifiedProvidedThing>): Initializable =
      qualifiedProvidedThing.asInitializable()
}