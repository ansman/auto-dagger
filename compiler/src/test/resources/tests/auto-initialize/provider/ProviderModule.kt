package se.ansman.provider

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Singleton

class ProvidedThing

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {
    @Provides
    @AutoInitialize
    @Singleton
    fun provideThing(): ProvidedThing = ProvidedThing()
}