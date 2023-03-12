package tests.auto_initialize.binding

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

interface Thing

@Singleton
class RealThing @Inject constructor() : Thing

@Module
@InstallIn(SingletonComponent::class)
interface BindingModule {
    @Binds
    @AutoInitialize
    fun bindThing(thing: RealThing): Thing
}