package se.ansman.provider

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.ansman.deager.Eager
import javax.inject.Singleton

class ProvidedThing

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {
    @Provides
    @Eager
    @Singleton
    fun provideThing(): ProvidedThing = ProvidedThing()
}