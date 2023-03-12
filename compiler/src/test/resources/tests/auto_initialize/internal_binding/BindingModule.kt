package tests.auto_initialize.internal_binding

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

interface Thing

@Singleton
internal class RealThing @Inject constructor() : Thing

@Module
@InstallIn(SingletonComponent::class)
internal interface BindingModule {
    @Binds
    @AutoInitialize
    fun bindThing(thing: RealThing): Thing
}