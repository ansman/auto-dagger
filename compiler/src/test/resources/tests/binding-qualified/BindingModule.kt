package se.ansman.binding.qualified

import dagger.Binds
import se.ansman.deager.Eager
import javax.inject.Named
import javax.inject.Singleton

interface Thing
class RealThing : Thing

@dagger.Module
interface BindingModule {
    @get:Binds
    @get:Eager
    @get:Named("someName")
    @get:Singleton
    val RealThing.thing: Thing
}