package tests.auto_initialize.provider.qualified

import dagger.Module
import dagger.Provides
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Named
import javax.inject.Singleton

class QualifiedProvidedThing

@Module
object QualifiedProviderModule {
    val thing
        @Provides
        @AutoInitialize
        @Named("someName")
        @Singleton
        get() = QualifiedProvidedThing()
}