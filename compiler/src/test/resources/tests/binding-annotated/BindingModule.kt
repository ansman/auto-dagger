package se.ansman.binding.annotated

import dagger.Binds
import dagger.Module
import se.ansman.deager.Eager
import javax.inject.Singleton

interface Thing

class RealThing : Thing

@Module
interface BindingModule {
    @Binds
    @Eager
    @Singleton
    fun RealThing.bindThing(): Thing
}