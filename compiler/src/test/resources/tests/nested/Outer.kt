package se.ansman.nested

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.ansman.deager.Eager
import javax.inject.Inject
import javax.inject.Singleton

object Outer {
    @Eager
    @Singleton
    class SomeThing @Inject constructor()

    @Module
    @InstallIn(SingletonComponent::class)
    object InnerModule {
        @Provides
        @Eager
        @Singleton
        fun provideString(): String = "foo"
    }
}