package tests.auto_initialize.companion_object

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ObjectModule {
    companion object {
        @Provides
        @AutoInitialize
        @Singleton
        fun provideThing() = 1
    }
}