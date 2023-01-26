package se.ansman.internal_binding

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.ansman.deager.Eager
import javax.inject.Inject
import javax.inject.Singleton

interface Thing

@Singleton
internal class RealThing @Inject constructor() : Thing

@Module
@InstallIn(SingletonComponent::class)
internal interface BindingModule {
    @Binds
    @Eager
    fun bindThing(thing: RealThing): Thing
}