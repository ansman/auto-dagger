package se.ansman.binding

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.ansman.deager.Eager
import javax.inject.Inject
import javax.inject.Singleton

interface Thing

@Singleton
class RealThing @Inject constructor() : Thing

@Module
@InstallIn(SingletonComponent::class)
interface BindingModule {
    @Binds
    @Eager
    fun bindThing(thing: RealThing): Thing
}