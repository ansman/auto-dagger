package tests.auto_initialize.binding.annotated

import dagger.Binds
import dagger.Module
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Singleton

interface Thing

class RealThing : Thing

@Module
interface BindingModule {
    @Binds
    @AutoInitialize
    @Singleton
    fun RealThing.bindThing(): Thing
}