package se.ansman.provider.qualified

import dagger.Module
import dagger.Provides
import se.ansman.deager.Eager
import javax.inject.Named
import javax.inject.Singleton

class QualifiedProvidedThing

@Module
object QualifiedProviderModule {
    val thing
        @Provides
        @Eager
        @Named("someName")
        @Singleton
        get() = QualifiedProvidedThing()
}