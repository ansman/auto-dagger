package se.ansman.nested

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

object Outer {
    @AutoInitialize
    @Singleton
    class SomeThing @Inject constructor()

    @Module
    @InstallIn(SingletonComponent::class)
    object InnerModule {
        @Provides
        @AutoInitialize
        @Singleton
        fun provideString(): String = "foo"
    }
}